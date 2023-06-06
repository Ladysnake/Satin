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
package ladysnake.satin.impl;

import ladysnake.satin.mixin.client.render.RenderPhaseAccessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RenderLayerSupplier {
    private final Consumer<RenderLayer.MultiPhaseParameters.Builder> transform;
    private final Map<RenderLayer, RenderLayer> renderLayerCache = new HashMap<>();
    private final String uniqueName;
    private final @Nullable VertexFormat vertexFormat;

    public static RenderLayerSupplier framebuffer(String name, Runnable setupState, Runnable cleanupState) {
        RenderPhase.Target target = new RenderPhase.Target(
                name + "_target",
                setupState,
                cleanupState
        );
        return new RenderLayerSupplier(name, builder -> builder.target(target));
    }

    public static RenderLayerSupplier shader(String name, VertexFormat vertexFormat, Supplier<ShaderProgram> shaderSupplier) {
        RenderPhase shader = Helper.makeShader(shaderSupplier);
        return new RenderLayerSupplier(name, vertexFormat, builder -> Helper.applyShader(builder, shader));
    }

    public RenderLayerSupplier(String name,  Consumer<RenderLayer.MultiPhaseParameters.Builder> transformer) {
        this(name, null, transformer);
    }

    public RenderLayerSupplier(String name, @Nullable VertexFormat vertexFormat, Consumer<RenderLayer.MultiPhaseParameters.Builder> transformer) {
        this.uniqueName = name;
        this.vertexFormat = vertexFormat;
        this.transform = transformer;
    }

    public RenderLayer getRenderLayer(RenderLayer baseLayer) {
        RenderLayer existing = this.renderLayerCache.get(baseLayer);
        if (existing != null) {
            return existing;
        }
        String newName = ((RenderPhaseAccessor) baseLayer).getName() + "_" + this.uniqueName;
        RenderLayer newLayer = RenderLayerDuplicator.copy(baseLayer, newName, this.vertexFormat, this.transform);
        this.renderLayerCache.put(baseLayer, newLayer);
        return newLayer;
    }

    /**
     * Big brain move right there
     */
    private static class Helper extends RenderPhase {
        public static RenderPhase makeShader(Supplier<net.minecraft.client.gl.ShaderProgram> shader) {
            return new ShaderProgram(shader);
        }

        public static void applyShader(RenderLayer.MultiPhaseParameters.Builder builder, RenderPhase shader) {
            builder.program((ShaderProgram) shader);
        }

        private Helper(String name, Runnable beginAction, Runnable endAction) {
            super(name, beginAction, endAction);
        }
    }
}
