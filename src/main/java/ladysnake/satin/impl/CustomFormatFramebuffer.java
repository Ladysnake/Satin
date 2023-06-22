package ladysnake.satin.impl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.types.templates.Check;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebuffer;

public class CustomFormatFramebuffer extends Framebuffer {
    private final int format;

    public CustomFormatFramebuffer(int format, int width, int height, boolean useDepth, boolean getError) {
        super(useDepth);
        this.format = format;
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, getError);
    }

    /**
     * <b> Check me when updating!</b> This duplicates logic from {@link SimpleFramebuffer#initFbo(int, int, boolean)}, so make sure that method
     * has not changed.
     * @param width     the width of the framebuffer
     * @param height    the height of the frambuffer
     * @param getError  whether to get error when clearing
     */
    @Override
    public void initFbo(int width, int height, boolean getError) {
        RenderSystem.assertOnRenderThreadOrInit();
        int i = RenderSystem.maxSupportedTextureSize();
        if (width > 0 && width <= i && height > 0 && height <= i) {
            this.viewportWidth = width;
            this.viewportHeight = height;
            this.textureWidth = width;
            this.textureHeight = height;
            this.fbo = GlStateManager.glGenFramebuffers();
            this.colorAttachment = TextureUtil.generateTextureId();
            if (this.useDepthAttachment) {
                this.depthAttachment = TextureUtil.generateTextureId();
                GlStateManager._bindTexture(this.depthAttachment);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_NEAREST);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_NEAREST);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_COMPARE_MODE, 0);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_S, GlConst.GL_CLAMP_TO_EDGE);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_T, GlConst.GL_CLAMP_TO_EDGE);
                GlStateManager._texImage2D(
                        GlConst.GL_TEXTURE_2D, 0, GlConst.GL_DEPTH_COMPONENT, this.textureWidth, this.textureHeight, 0, GlConst.GL_DEPTH_COMPONENT, GlConst.GL_FLOAT, null
                );
            }

            this.setTexFilter(GlConst.GL_NEAREST);
            GlStateManager._bindTexture(this.colorAttachment);
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_S, GlConst.GL_CLAMP_TO_EDGE);
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_T, GlConst.GL_CLAMP_TO_EDGE);
            GlStateManager._texImage2D(
                    GlConst.GL_TEXTURE_2D, 0, format, this.textureWidth, this.textureHeight, 0, GlConst.GL_RGBA, GlConst.GL_UNSIGNED_BYTE, null
            );
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, this.fbo);
            GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, GlConst.GL_TEXTURE_2D, this.colorAttachment, 0);
            if (this.useDepthAttachment) {
                GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_DEPTH_ATTACHMENT, GlConst.GL_TEXTURE_2D, this.depthAttachment, 0);
            }

            this.checkFramebufferStatus();
            this.clear(getError);
            this.endRead();
        } else {
            throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + i + ")");
        }
    }
}
