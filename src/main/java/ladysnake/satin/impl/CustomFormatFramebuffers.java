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

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.apiguardian.api.API;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Handles creating framebuffers with custom pixel formats.
 */
public class CustomFormatFramebuffers {
    public static final String FORMAT_KEY = "satin:format";
    private static final ThreadLocal<TextureFormat> FORMAT = new ThreadLocal<>();

    /**
     * Experimental method to create a new framebuffer from code, please open an issue if you actually use it
     *
     * <p>Refer to {@link SimpleFramebuffer} for the list of parameters
     */
    @API(status = API.Status.EXPERIMENTAL)
    public static Framebuffer create(int width, int height, boolean useDepth, boolean getError, TextureFormat format) {
        try {
            FORMAT.set(format);
            return new SimpleFramebuffer(width, height, useDepth, getError);
        } finally {
            FORMAT.remove();
        }
    }

    /**
     * Prepares a framebuffer creation for the given pixel format.
     * <p>Valid formats are:</p>
     * <ul>
     *     <li>RGBA8</li>
     *     <li>RGBA16</li>
     *     <li>RGBA16F</li>
     *     <li>RGBA32F</li>
     * </ul>
     * @param formatString  the pixel format, as specified by {@link TextureFormat#decode(String)}
     */
    public static void prepareCustomFormat(String formatString) {
        // FORMAT's value is then consumed in gl.FramebufferMixin
        FORMAT.set(TextureFormat.decode(formatString));
    }

    public static @Nullable CustomFormatFramebuffers.TextureFormat getCustomFormat() {
        return FORMAT.get();
    }

    public static void clearCustomFormat() {
        FORMAT.remove();
    }

    public enum TextureFormat {
        RGBA8(GL11.GL_RGBA8),
        RGBA16(GL11.GL_RGBA16),
        RGBA16F(GL30.GL_RGBA16F),
        RGBA32F(GL30.GL_RGBA32F),
        ;

        public final int value;

        /**
         * Decodes a pixel format specified by name into its corresponding OpenGL enum.
         * <strong>This only supports the formats needed for {@link #prepareCustomFormat(String)}.</strong>
         * @param formatString  the format name
         * @return              the format enum value
         * @see #prepareCustomFormat(String)
         */
        public static TextureFormat decode(String formatString) {
            return switch (formatString) {
                // unsigned normalised 8 bits
                case "RGBA8" -> RGBA8;
                // unsigned normalised 16 bits
                case "RGBA16" -> RGBA16;
                // float 16 bits
                case "RGBA16F" -> RGBA16F;
                // float 32 bits
                case "RGBA32F" -> RGBA32F;
                // we don't support un-normalised signed or unsigned integral formats here
                // because it's not valid to blit between them and normalised/float formats
                default -> throw new IllegalArgumentException("Unsupported texture format "+formatString);
            };
        }

        TextureFormat(int value) {
            this.value = value;
        }
    }
}
