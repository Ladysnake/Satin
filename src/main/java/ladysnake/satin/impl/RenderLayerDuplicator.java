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

import java.util.function.Consumer;

public final class RenderLayerDuplicator {

    public static RenderLayer copy(RenderLayer existing, String newName, Consumer<RenderLayer.MultiPhaseParameters.Builder> op) {
        checkDefaultImpl(existing);
        return ((SatinRenderLayer) existing).satin$copy(newName, op);
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
        RenderLayer satin$copy(String newName, Consumer<RenderLayer.MultiPhaseParameters.Builder> op);
        RenderLayer.MultiPhaseParameters satin$copyPhaseParameters(Consumer<RenderLayer.MultiPhaseParameters.Builder> op);
    }
}
