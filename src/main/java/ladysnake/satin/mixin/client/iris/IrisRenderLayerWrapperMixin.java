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
package ladysnake.satin.mixin.client.iris;

import ladysnake.satin.impl.RenderLayerDuplicator;
import net.coderbot.iris.layer.IrisRenderTypeWrapper;
import net.coderbot.iris.layer.UseProgramRenderStateShard;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@SuppressWarnings({"UnusedMixin", "unused"})    // added through mixin plugin
@Mixin(IrisRenderTypeWrapper.class)
public abstract class IrisRenderLayerWrapperMixin implements RenderLayerDuplicator.SatinRenderLayer {
    @Shadow public abstract RenderLayer unwrap();

    @Shadow @Final private UseProgramRenderStateShard useProgram;

    @Override
    public RenderLayer satin$copy(String newName, @Nullable VertexFormat vertexFormat, Consumer<RenderLayer.MultiPhaseParameters.Builder> op) {
        return new IrisRenderTypeWrapper(newName, RenderLayerDuplicator.copy(this.unwrap(), newName + "_wrapped", vertexFormat, op), this.useProgram);
    }

    @Override
    public RenderLayer.MultiPhaseParameters satin$copyPhaseParameters(Consumer<RenderLayer.MultiPhaseParameters.Builder> op) {
        return RenderLayerDuplicator.copyPhaseParameters(this.unwrap(), op);
    }
}
