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
package org.ladysnake.satin.impl;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.Satin;
import org.ladysnake.satin.api.managed.ManagedCoreShader;
import org.ladysnake.satin.api.managed.uniform.SamplerUniform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ResettableManagedCoreShader extends ResettableManagedShaderBase<ShaderProgram, ManagedUniform> implements ManagedCoreShader {
    /**
     * Callback to run once each time the shader effect is initialized
     */
    private final Consumer<ManagedCoreShader> initCallback;
    private final RenderLayerSupplier renderLayerSupplier;
    private final VertexFormat vertexFormat;
    private final Map<String, ManagedSamplerUniformV1> managedSamplers = new HashMap<>();

    public ResettableManagedCoreShader(Identifier location, VertexFormat vertexFormat, Consumer<ManagedCoreShader> initCallback) {
        super(location);
        this.vertexFormat = vertexFormat;
        this.initCallback = initCallback;
        this.renderLayerSupplier = RenderLayerSupplier.shader(
                String.format("%s_%d", location, System.identityHashCode(this)),
                vertexFormat,
                this.getLocation()
        );
    }

    @Override
    protected ShaderProgram parseShader(ResourceFactory resourceManager, MinecraftClient mc, Identifier location) throws ShaderLoader.LoadException {
        throw new ShaderLoader.LoadException("unsupported");
//        return mc.getShaderLoader().getOrCreateProgram(new ShaderProgramKey(this.getLocation(), this.vertexFormat, Defines.builder().build()));
    }

    @Override
    public void setup() {
        Preconditions.checkNotNull(this.shader);
        for (ManagedUniform uniform : this.getManagedUniforms()) {
            setupUniform(uniform, this.shader);
        }
        this.initCallback.accept(this);
    }

    @Override
    protected void doRelease(ShaderProgram shader) {
        shader.close();
    }

    @Override
    public ShaderProgram getProgram() {
        return this.shader;
    }

    @Override
    public RenderLayer getRenderLayer(RenderLayer baseLayer) {
        return this.renderLayerSupplier.getRenderLayer(baseLayer);
    }

    @Override
    protected boolean setupUniform(ManagedUniform uniform, ShaderProgram shader) {
        return uniform.findUniformTarget(shader);
    }

    @Override
    public SamplerUniform findSampler(String samplerName) {
        return manageUniform(this.managedSamplers, ManagedSamplerUniformV1::new, samplerName, "sampler");
    }

    @Override
    protected void logInitError(ShaderLoader.LoadException e) {
        Satin.LOGGER.error("Could not create shader program {}", this.getLocation(), e);
    }
}
