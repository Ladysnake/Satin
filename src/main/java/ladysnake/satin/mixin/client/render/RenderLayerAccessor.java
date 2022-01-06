/*
 * Satin
 * Copyright (C) 2019-2022 Ladysnake
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
package ladysnake.satin.mixin.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderLayer.class)
public interface RenderLayerAccessor {
    @Accessor
    boolean isTranslucent();

    @Invoker("of")
    static RenderLayer.MultiPhase satin$of(@SuppressWarnings("unused") String name, @SuppressWarnings("unused") VertexFormat vertexFormat, @SuppressWarnings("unused") VertexFormat.DrawMode drawMode, @SuppressWarnings("unused") int expectedBufferSize, @SuppressWarnings("unused") boolean hasCrumbling, @SuppressWarnings("unused") boolean translucent, @SuppressWarnings("unused") RenderLayer.MultiPhaseParameters phases) {
        throw new IllegalStateException("Mixin not transformed");
    }
}
