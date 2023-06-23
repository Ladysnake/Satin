package ladysnake.satin.mixin.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import ladysnake.satin.impl.CustomFormatFramebuffers;
import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows framebuffers to have custom formats. Default is still GL_RGBA8.
 * @see CustomFormatFramebuffers
 */
@Mixin(Framebuffer.class)
public abstract class CustomFormatFramebufferMixin {
    @Unique
    private int satin$format = GlConst.GL_RGBA8;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void satin$setFormat(boolean useDepth, CallbackInfo ci) {
        Integer format = CustomFormatFramebuffers.FORMAT.get();
        if (format != null) {
            this.satin$format = format;
            CustomFormatFramebuffers.FORMAT.remove();
        }
    }

    @ModifyArg(
            method = "initFbo",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;setTexFilter(I)V"),
                    to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glBindFramebuffer(II)V")
            ),
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"),
            index = 2
    )
    private int satin$modifyInternalFormat(int internalFormat) {
        return this.satin$format;
    }
}
