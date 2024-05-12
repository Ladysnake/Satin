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
package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.WorldRenderer;

@FunctionalInterface
public interface WorldRendererReloadCallback {
    /**
     * Fired in {@link WorldRenderer#reload()}, typically called when video settings are updated or the player joins a world
     */
    Event<WorldRendererReloadCallback> EVENT = EventFactory.createArrayBacked(WorldRendererReloadCallback.class,
            (listeners) -> (renderer) -> {
                for (WorldRendererReloadCallback event : listeners) {
                    event.onRendererReload(renderer);
                }
            });

    void onRendererReload(WorldRenderer renderer);
}
