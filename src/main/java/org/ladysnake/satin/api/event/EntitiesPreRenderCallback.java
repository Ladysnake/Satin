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
import net.minecraft.client.render.Frustum;

@FunctionalInterface
public interface EntitiesPreRenderCallback {
    /**
     * Fired before Minecraft has rendered all entities
     *
     * @deprecated {@link net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents#BEFORE_ENTITIES} in Fabric
     * API is equivalent to this event
     */
    @Deprecated
    Event<EntitiesPreRenderCallback> EVENT = EventFactory.createArrayBacked(EntitiesPreRenderCallback.class,
            (listeners) -> (camera, frustum, tickDelta) -> {
                for (EntitiesPreRenderCallback handler : listeners) {
                    handler.beforeEntitiesRender(camera, frustum, tickDelta);
                }
            });

    void beforeEntitiesRender(Camera camera, Frustum frustum, float tickDelta);
}
