package ladysnake.satin.impl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.types.templates.Check;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Much like SimpleFramebuffer, but with a non-hardcoded pixel format.
 */
public class CustomFormatFramebuffer extends Framebuffer {
    private final int format;

    public CustomFormatFramebuffer(int format, int width, int height, boolean useDepth, boolean getError) {
        super(useDepth);
        this.format = format;
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, getError);
    }

    /**
     * Creates a framebuffer with the given width, height, and pixel format.
     * <p>Valid formats are:</p>
     * <ul>
     *     <li>RGBA8</li>
     *     <li>RGBA16</li>
     *     <li>RGBA16F</li>
     *     <li>RGBA32F</li>
     * </ul>
     * @param width         the framebuffer width
     * @param height        the framebuffer height
     * @param formatString  the pixel format, as specified by {@link #decodeFormatString(String)}
     * @return              the framebuffer
     */
    public static Framebuffer createFramebuffer(int width, int height, String formatString) {
        int format = decodeFormatString(formatString);
        return new CustomFormatFramebuffer(format, width, height, true, MinecraftClient.IS_SYSTEM_MAC);
    }

    /**
     * Decodes a pixel format specified by name into its corresponding OpenGL enum.
     * <strong>This only supports the formats needed for {@link #createFramebuffer(int, int, String)}.</strong>
     * @param formatString  the format name
     * @return              the format enum value
     * @see #createFramebuffer(int, int, String)
     */
    private static int decodeFormatString(String formatString) {
        return switch (formatString) {
            // unsigned normalised 8 bits
            case "RGBA8" -> GL11.GL_RGBA8;
            // unsigned normalised 16 bits
            case "RGBA16" -> GL11.GL_RGBA16;
            // float 16 bits
            case "RGBA16F" -> GL30.GL_RGBA16F;
            // float 32 bits
            case "RGBA32F" -> GL30.GL_RGBA32F;
            // we don't support un-normalised signed or unsigned integral formats here
            // because it's not valid to blit between them and normalised/float formats
            default -> throw new IllegalArgumentException("Unsupported texture format "+formatString);
        };
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
                    // this ⬇️ is the only line changed from the super method
                    GlConst.GL_TEXTURE_2D, 0, this.format, this.textureWidth, this.textureHeight, 0, GlConst.GL_RGBA, GlConst.GL_UNSIGNED_BYTE, null
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
