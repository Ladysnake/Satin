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
package ladysnake.satin.impl;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.client.render.RenderLayer;

import java.util.Set;

public final class BlockRenderLayerRegistry {
    public static final BlockRenderLayerRegistry INSTANCE = new BlockRenderLayerRegistry();
    private final Set<RenderLayer> renderLayers = new ObjectArraySet<>();   // ArraySet for faster iteration
    private volatile boolean registryLocked = false;
    
    private BlockRenderLayerRegistry(){}
    
    public void registerRenderLayer(RenderLayer layer) {
        if(registryLocked){
            throw new IllegalStateException(String.format(
                "RenderLayer %s was added too late.",
                    layer
            ));
        }
        
        renderLayers.add(layer);
    }
    
    public Set<RenderLayer> getLayers() {
        registryLocked = true;
        return renderLayers;
    }
}
