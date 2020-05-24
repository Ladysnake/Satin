/*
 * Satin
 * Copyright (C) 2019-2020 Ladysnake
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
package ladysnake.satin.api.experimental;

import ladysnake.satin.Satin;
import ladysnake.satin.config.OptionalFeature;
import ladysnake.satin.config.SatinFeatures;
import net.minecraft.client.MinecraftClient;
import org.apiguardian.api.API;

import javax.annotation.CheckForSigned;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Implemented by every {@link net.minecraft.client.gl.Framebuffer} when the feature is enabled in the config.
 * <p>
 * This allows access to a depth texture that the framebuffer writes to instead of the usual render buffer.
 * <p>
 * The replacement of the render buffer with a readable depth texture is only done when at least one mod
 * {@link #useFeature() declares the feature as used}.
 */
public interface ReadableDepthFramebuffer {

    /**
     * Marks the readable depth framebuffer feature as used.
     * <p>
     * After this method has been called, all future framebuffers will have
     * a texture instead of a render buffer as their depth attachment point.
     */
    @API(status = EXPERIMENTAL)
    static void useFeature() {
        Satin.LOGGER.info("[Satin] Enabling readable depth framebuffers. This may cause incompatibilities with other graphical mods.");
        OptionalFeature option = SatinFeatures.getInstance().readableDepthFramebuffers;
        if (option.isConfigEnabled()) {
            option.use();
        } else {
            Satin.LOGGER.warn("[Satin] Couldn't activate the feature as it is disabled by the config!");
        }
    }

    /**
     * Returns the depth texture used by this buffer.
     * <p>
     * This buffer <b>MUST</b> be in use when this method is called.
     * The reason for this is that the depth texture is retrieved
     * reflectively to account for other mods doing similar changes.
     * <p>
     * If the feature is not enabled, or an incompatibility prevents the
     * retrieval of the texture, the returned value will be -1.
     * <p>
     * Note that the depth texture is updated whenever something draws on screen.
     * Reading from and writing to the depth texture simultaneously will result
     * in undefined behaviour. Do also note that Minecraft clears all depth information
     * after world render finishes, and each time a {@link net.minecraft.client.gl.PostProcessShader}
     * is rendered.
     *
     * @return the gl id of the depth texture, or -1 if it cannot be obtained
     * @see #getStillDepthMap()
     */
    @CheckForSigned
    @API(status = EXPERIMENTAL)
    int getCurrentDepthTexture();

    /**
     * Returns a still of this framebuffer's depth texture.
     * For most intents and purposes, this is the texture that API consumers should read from.
     * <p>
     * This texture is updated only when {@link #freezeDepthMap()} is called.
     * <p>
     * If the feature is not enabled, or an incompatibility prevents the retrieval of the
     * {@link #getCurrentDepthTexture() current depth texture}, the still depth texture
     * will never be updated.
     * <p>
     * Because this texture is independent from the framebuffer's actual depth texture,
     * it is safe to use anytime.
     * <p>
     * The returned value may be -1 if the framebuffer does not have a depth attachment.
     * This should never be the case for the main framebuffer.
     *
     * @return a still of this framebuffer's depth texture
     */
    @API(status = EXPERIMENTAL)
    int getStillDepthMap();

    /**
     * Freezes the {@link #getCurrentDepthTexture() current depth texture} for use in
     * {@link #getStillDepthMap()}.
     * <p>
     * This is called by default on the {@link MinecraftClient#getFramebuffer() main framebuffer}
     * once every frame, right before {@link ladysnake.satin.api.event.PostWorldRenderCallback} is fired.
     * <p>
     * Calling this method will bind {@code this} framebuffer.
     */
    @API(status = EXPERIMENTAL)
    void freezeDepthMap();
}
