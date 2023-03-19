/*
 * Satin
 * Copyright (C) 2019-2023 Ladysnake
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

import ladysnake.satin.impl.RenderLayerDuplicator;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(RenderLayer.MultiPhase.class)
public abstract class RenderLayerMultiPhaseMixin extends RenderLayer implements RenderLayerDuplicator.SatinRenderLayer {
    @Shadow
    @Final
    private MultiPhaseParameters phases;

    public RenderLayerMultiPhaseMixin(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    @Override
    public RenderLayer satin$copy(String newName, @Nullable VertexFormat vertexFormat, Consumer<MultiPhaseParameters.Builder> op) {
        return RenderLayerAccessor.satin$of(
                newName,
                vertexFormat == null ? this.getVertexFormat() : vertexFormat,
                this.getDrawMode(),
                this.getExpectedBufferSize(),
                this.hasCrumbling(),
                ((RenderLayerAccessor) this).isTranslucent(),
                this.satin$copyPhaseParameters(op)
        );
    }

    @Override
    public MultiPhaseParameters satin$copyPhaseParameters(Consumer<MultiPhaseParameters.Builder> op) {
        // Yes we can cast to accessor
        @SuppressWarnings("ConstantConditions") RenderLayerMixin.MultiPhaseParametersAccessor access = ((RenderLayerMixin.MultiPhaseParametersAccessor) (Object) this.phases);
        MultiPhaseParameters.Builder builder = MultiPhaseParameters.builder()
                .texture(access.getTexture())
                .program(access.getProgram())
                .transparency(access.getTransparency())
                .depthTest(access.getDepthTest())
                .cull(access.getCull())
                .lightmap(access.getLightmap())
                .overlay(access.getOverlay())
                .layering(access.getLayering())
                .target(access.getTarget())
                .texturing(access.getTexturing())
                .writeMaskState(access.getWriteMaskState())
                .lineWidth(access.getLineWidth());
        op.accept(builder);
        return builder.build(access.getOutlineMode());
    }
}
