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
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

@FunctionalInterface
public interface PostWorldRenderCallbackV3 {
    /**
     * Fired after Minecraft has rendered everything in the world, before it renders hands, HUDs and GUIs.
     *
     * @deprecated  {@link net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents#LAST} is equivalent to this
     * event
     */
    @Deprecated
    Event<PostWorldRenderCallbackV3> EVENT = EventFactory.createArrayBacked(PostWorldRenderCallbackV3.class,
            (listeners) -> (matrices, projectionMat, modelViewMath, camera, tickDelta) -> {
                PostWorldRenderCallbackV2.EVENT.invoker().onWorldRendered(matrices, camera, tickDelta);
                for (PostWorldRenderCallbackV3 handler : listeners) {
                    handler.onWorldRendered(matrices, projectionMat, modelViewMath, camera, tickDelta);
                }
            });

    /**
     * @param posingStack a blank {@link MatrixStack} that can be used for rendering custom elements
     * @param projectionMat the base projection matrix for world rendering
     * @param modelViewMat the model-view matrix corresponding to the camera's perspective
     * @param camera the camera from which perspective the world is being rendered
     * @param tickDelta fraction of time between two consecutive ticks (before 0 and 1)
     */
    void onWorldRendered(MatrixStack posingStack, Matrix4f projectionMat, Matrix4f modelViewMat, Camera camera, float tickDelta);
}
