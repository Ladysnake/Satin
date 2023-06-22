package ladysnake.satin.impl;

import com.mojang.blaze3d.platform.GlConst;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomFramebuffers {
    public static Framebuffer createFramebuffer(int width, int height, String formatString) {
        int format = decodeFormatString(formatString);
        return new CustomFormatFramebuffer(format, width, height, true, MinecraftClient.IS_SYSTEM_MAC);
    }

    public static int decodeFormatString(String formatString) {
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

    public enum FramebufferFormat {
        RGBA8(GL11.GL_RGBA8),
        RGBA16(GL11.GL_RGBA16),
        RGBA16F(GL30.GL_RGBA16F),
        RGBA32F(GL30.GL_RGBA32F),
        ;

        private final int internalFormat;

        FramebufferFormat(int internalFormat) {
            this.internalFormat = internalFormat;
        }
    }
}
