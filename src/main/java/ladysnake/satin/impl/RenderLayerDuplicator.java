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
package ladysnake.satin.impl;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class RenderLayerDuplicator {

    public static RenderLayer copy(RenderLayer existing, String newName,  Consumer<RenderLayer.MultiPhaseParameters.Builder> op) {
        return copy(existing, newName, null, op);
    }

    /**
     *
     * @param existing the {@link RenderLayer} to copy
     * @param newName a unique name for the new {@link RenderLayer}
     * @param vertexFormat the new vertex format, or {@code null} if {@code existing}'s format should be used
     * @param op the transformation to apply to {@code existing}'s parameters
     * @return a new {@link RenderLayer} based on {@code existing}
     */
    public static RenderLayer copy(RenderLayer existing, String newName, @Nullable VertexFormat vertexFormat, Consumer<RenderLayer.MultiPhaseParameters.Builder> op) {
        checkDefaultImpl(existing);
        return ((SatinRenderLayer) existing).satin$copy(newName, vertexFormat, op);
    }

    public static RenderLayer.MultiPhaseParameters copyPhaseParameters(RenderLayer existing, Consumer<RenderLayer.MultiPhaseParameters.Builder> op) {
        checkDefaultImpl(existing);
        return ((SatinRenderLayer) existing).satin$copyPhaseParameters(op);
    }

    private static void checkDefaultImpl(RenderLayer existing) {
        if (!(existing instanceof SatinRenderLayer)) {
            throw new IllegalArgumentException("Unrecognized RenderLayer implementation " + existing.getClass() + ". Layer duplication is only applicable to the default (MultiPhase) implementation");
        }
    }

    public interface SatinRenderLayer {
        RenderLayer satin$copy(String newName, @Nullable VertexFormat vertexFormat, Consumer<RenderLayer.MultiPhaseParameters.Builder> op);
        RenderLayer.MultiPhaseParameters satin$copyPhaseParameters(Consumer<RenderLayer.MultiPhaseParameters.Builder> op);
    }
}
