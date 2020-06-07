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
package ladysnake.satin.impl;

import ladysnake.satin.mixin.client.render.RenderPhaseAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

import java.util.HashMap;
import java.util.Map;

public class RenderLayerSupplier {
    private final RenderPhase.Target target;
    private final Map<RenderLayer, RenderLayer> renderLayerCache = new HashMap<>();
    private final String uniqueName;

    public RenderLayerSupplier(String name, Runnable setupState, Runnable cleanupState) {
        this.uniqueName = name;
        this.target = new RenderPhase.Target(
                uniqueName + "_target",
                setupState,
                cleanupState
        );
    }

    public RenderLayer getRenderLayer(RenderLayer baseLayer) {
        RenderLayer existing = this.renderLayerCache.get(baseLayer);
        if (existing != null) {
            return existing;
        }
        String newName = ((RenderPhaseAccessor) baseLayer).getName() + "_" + this.uniqueName;
        RenderLayer newLayer = RenderLayerDuplicator.copy(baseLayer, newName, builder -> builder.target(this.target));
        this.renderLayerCache.put(baseLayer, newLayer);
        return newLayer;
    }
}
