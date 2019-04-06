package ladysnake.satin.mixin.client.gl;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import ladysnake.satin.Satin;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satin.api.experimental.config.SatinFeatures;
import net.minecraft.client.gl.GlFramebuffer;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mojang.blaze3d.platform.GLX.GL_DEPTH_ATTACHMENT;
import static com.mojang.blaze3d.platform.GLX.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_DEFAULT;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(GlFramebuffer.class)
public abstract class DepthGlFramebufferMixin implements ReadableDepthFramebuffer {
    @Shadow @Final public boolean useDepthAttachment;

    @Shadow public int depthAttachment;
    @Shadow public int texWidth;
    @Shadow public int texHeight;

    @Shadow public int fbo;
    private int satin$depthTexture = -1;
    private int satin$actualDepthTexture = -99;

    @Inject(
            method = "initFbo",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GLX;glFramebufferRenderbuffer(IIII)V", shift = AFTER)
    )
    private void initFbo(int width, int height, boolean flushErrors, CallbackInfo ci) {
        if (this.useDepthAttachment && SatinFeatures.getInstance().readableDepthFramebuffers.isInUse()) {
            // Delete the depth render buffer, it will not be used
            GLX.glDeleteRenderbuffers(this.depthAttachment);
            this.depthAttachment = -1;

            this.satin$depthTexture = GL11.glGenTextures();
            GlStateManager.bindTexture(this.satin$depthTexture);
            GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            GlStateManager.texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, this.texWidth, this.texHeight, 0,GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, null);
            GLX.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,GL_TEXTURE_2D, this.satin$depthTexture,0);
        }
    }

    @Inject(method = "delete", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gl/GlFramebuffer;depthAttachment:I"))
    private void delete(CallbackInfo ci) {
        this.satin$deleteDepthTexture();
        this.satin$actualDepthTexture = -99;
    }

    private void satin$deleteDepthTexture() {
        if (this.satin$depthTexture > -1) {
            TextureUtil.releaseTextureId(this.satin$depthTexture);
            this.satin$depthTexture = -1;
        }
    }

    @Override
    public int getCurrentDepthTexture() {
        // < -1 means it hasn't been initialized yet
        if (this.satin$actualDepthTexture < -1) {
            int actualDepthTexture;
            int boundFbo = GLX.getBoundFramebuffer();
            if (boundFbo != this.fbo) {
                throw new IllegalStateException("Framebuffer must be bound before querying its depth texture for the first time");
            }

            // Get the type of depth attachment. In vanilla it should be GL_RENDERBUFFER, with our injections it should be GL_TEXTURE
            int attachmentObjectType = ARBFramebufferObject.glGetFramebufferAttachmentParameteri(
                    GL_FRAMEBUFFER,
                    GL_DEPTH_ATTACHMENT,
                    ARBFramebufferObject.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE
            );
            if (!this.useDepthAttachment || !SatinFeatures.getInstance().readableDepthFramebuffers.isInUse()) {
                // if we aren't using depth, there will be no depth texture
                actualDepthTexture = -1;
            } else if (attachmentObjectType != GL_TEXTURE) {
                // there is no depth texture attached! figure out what's going on
                String type;
                switch (attachmentObjectType) {
                    case GL30.GL_RENDERBUFFER: type = "GL_RENDERBUFFER"; break;
                    case GL30.GL_NONE: type = "GL_NONE"; break;
                    case GL_FRAMEBUFFER_DEFAULT: type = "GL_FRAMEBUFFER_DEFAULT"; break;
                    default: type = "UNKNOWN (" + attachmentObjectType + ")";
                }
                // Our framebuffer replacement is not doing its job
                Satin.LOGGER.error("A framebuffer has no readable depth attachment (expected a texture attachment, got {} in {})", type, this.getClass().getName());
                actualDepthTexture = -1;
            } else {
                // there is a depth texture attached
                actualDepthTexture = ARBFramebufferObject.glGetFramebufferAttachmentParameteri(
                        GL_FRAMEBUFFER,
                        GL_DEPTH_ATTACHMENT,
                        ARBFramebufferObject.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME
                );
                if (actualDepthTexture != this.satin$depthTexture) {
                    Satin.LOGGER.warn("A framebuffer has a depth texture different from the one assigned");
                    // Free our own depth texture, since it is not used
                    this.satin$deleteDepthTexture();
                }
            }
            this.satin$actualDepthTexture = actualDepthTexture;
        }
        return this.satin$actualDepthTexture;
    }
}
