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

import ladysnake.satin.api.event.PostWorldRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.options.GraphicsMode;
import net.minecraft.client.render.WorldRenderer;
import org.apiguardian.api.API;

import javax.annotation.CheckForSigned;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Implemented by every {@link Framebuffer} when the feature is enabled in the config.
 * <p>
 * This allows access to a depth texture that the framebuffer writes to instead of the usual render buffer.
 * <p>
 * The replacement of the render buffer with a readable depth texture is only done when at least one mod
 * {@link #useFeature() declares the feature as used}.
 */
@API(status = EXPERIMENTAL)
public interface ReadableDepthFramebuffer {

    /**
     * Marks the readable depth framebuffer feature as used.
     * <p>
     * After this method has been called, all future framebuffers will have
     * a texture instead of a render buffer as their depth attachment point.
     * @deprecated starting from 20w22a, {@link Framebuffer} always uses
     * a depth texture, making this feature redundant
     */
    @Deprecated
    @API(status = DEPRECATED, since = "1.4.0")
    static void useFeature() { }

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
     * @deprecated use {@link Framebuffer#method_30278}
     */
    @CheckForSigned
    @Deprecated
    @API(status = DEPRECATED, since = "1.4.0")
    int getCurrentDepthTexture();

    /**
     * Returns a still of this framebuffer's depth texture.
     * For most intents and purposes, this is the texture that API consumers should read from.
     *
     * <p>This texture is updated only when {@link #freezeDepthMap()} is called. For the main framebuffer,
     * it is updated automatically right before {@link PostWorldRenderCallback#EVENT} is fired.
     * <strong>Note that the content of the main framebuffer's depth texture depends on the graphic quality setting.</strong>
     * With {@link GraphicsMode#FABULOUS}, no translucent object will appear on the main depth texture, so consumers may
     * need to also use the depth textures from the various layers used by the {@link WorldRenderer}'s transparency shader.
     *
     * <p>Because this texture is independent from the framebuffer's actual depth texture,
     * it is safe to use anytime (whereas using the actual depth texture may cause corruption if it is simultaneously written to).
     *
     * <p>The returned value may be -1 if the framebuffer does not have a {@linkplain Framebuffer#useDepthAttachment depth attachment}.
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
     * once every frame, right before {@link PostWorldRenderCallback} is fired.
     * <p>
     * Calling this method will bind {@code this} framebuffer.
     */
    @API(status = EXPERIMENTAL)
    void freezeDepthMap();
}
