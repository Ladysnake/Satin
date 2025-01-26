/*
 * Satin
 * Copyright (C) 2019-2024 Ladysnake
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
package org.ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.Camera;
import org.ladysnake.satin.api.experimental.ReadableDepthFramebuffer;

/**
 * @see PostWorldRenderCallbackV2
 * @see PostWorldRenderCallbackV3
 */
@FunctionalInterface
public interface PostWorldRenderCallback {
    /**
     * Fired after Minecraft has rendered everything in the world, before it renders hands, HUDs and GUIs.
     *
     * <p>{@link net.minecraft.client.gl.PostEffectProcessor}s <strong>must not</strong> be rendered in this callback, as they will prevent
     * {@link GraphicsMode#FABULOUS fabulous graphics} and other effects from working properly.
     *
     * @deprecated  {@link net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents#LAST} is equivalent to this
     * event
     */
    @Deprecated
    Event<PostWorldRenderCallback> EVENT = EventFactory.createArrayBacked(PostWorldRenderCallback.class,
            (listeners) -> (camera, tickDelta) -> {
                ((ReadableDepthFramebuffer) MinecraftClient.getInstance().getFramebuffer()).freezeDepthMap();
                for (PostWorldRenderCallback handler : listeners) {
                    handler.onWorldRendered(camera, tickDelta);
                }
            });

    /**
     * @param camera    the camera from which perspective the world is being rendered
     * @param tickDelta fraction of time between two consecutive ticks (before 0 and 1)
     */
    void onWorldRendered(Camera camera, float tickDelta);
}
