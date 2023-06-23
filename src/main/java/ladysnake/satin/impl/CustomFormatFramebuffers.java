/*
 * Satin
 * Copyright (C) 2019-2023 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package ladysnake.satin.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Handles creating framebuffers with custom pixel formats.
 */
public class CustomFormatFramebuffers {
    public static final ThreadLocal<Integer> FORMAT = new ThreadLocal<>();

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
        FORMAT.set(format);
        // FORMAT's value is then consumed in gl.FramebufferMixin
        return new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
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
}
