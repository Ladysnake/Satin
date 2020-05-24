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
package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.SortedMap;

@ApiStatus.Experimental
public interface BufferBuildersInitCallback {
    /**
     * Fired when Minecraft initializes its {@link BufferBuilderStorage}.
     *
     * <p> Mods should initialize their {@link VertexConsumerProvider} and {@link BufferBuilder}
     * at this time.
     */
    Event<BufferBuildersInitCallback> EVENT = EventFactory.createArrayBacked(BufferBuildersInitCallback.class,
            (listeners) -> (bufferBuilders, entityBuffers) -> {
                for (BufferBuildersInitCallback handler : listeners) {
                    handler.initBufferBuilders(bufferBuilders, entityBuffers);
                }
            });

    void initBufferBuilders(BufferBuilderStorage bufferBuilders, SortedMap<RenderLayer, BufferBuilder> entityBuilders);
}
