/*
 * Satin
 * Copyright (C) 2019-2021 Ladysnake
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
package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;

@FunctionalInterface
public interface PostWorldRenderCallbackV2 {
    /**
     * Fired after Minecraft has rendered everything in the world, before it renders hands, HUDs and GUIs.
     */
    Event<PostWorldRenderCallbackV2> EVENT = EventFactory.createArrayBacked(PostWorldRenderCallbackV2.class,
            (listeners) -> (matrices, camera, tickDelta, nanoTime) -> {
                PostWorldRenderCallback.EVENT.invoker().onWorldRendered(camera, tickDelta, nanoTime);
                for (PostWorldRenderCallbackV2 handler : listeners) {
                    handler.onWorldRendered(matrices, camera, tickDelta, nanoTime);
                }
            });

    void onWorldRendered(MatrixStack matrices, Camera camera, float tickDelta, long nanoTime);
}
