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
package org.ladysnake.satinrenderlayer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import org.jetbrains.annotations.Nullable;

public class IllusionGolemEntityRenderer extends IronGolemEntityRenderer {
    public IllusionGolemEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected @Nullable RenderLayer getRenderLayer(IronGolemEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
        RenderLayer baseLayer = super.getRenderLayer(state, showBody, translucent, showOutline);
        return baseLayer == null ? null : SatinRenderLayerTest.illusionBuffer.getRenderLayer(baseLayer);
    }
}
