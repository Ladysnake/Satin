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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.apiguardian.api.API;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.satin.Satin;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import org.ladysnake.satin.api.managed.uniform.Uniform1i;
import org.ladysnake.satin.api.managed.uniform.Uniform2f;
import org.ladysnake.satin.api.managed.uniform.Uniform2i;
import org.ladysnake.satin.api.managed.uniform.Uniform3f;
import org.ladysnake.satin.api.managed.uniform.Uniform3i;
import org.ladysnake.satin.api.managed.uniform.Uniform4f;
import org.ladysnake.satin.api.managed.uniform.Uniform4i;
import org.ladysnake.satin.api.managed.uniform.UniformFinder;
import org.ladysnake.satin.api.managed.uniform.UniformMat4;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.apiguardian.api.API.Status.INTERNAL;

public abstract class ResettableManagedShaderBase<S, U extends ManagedUniformBase> implements UniformFinder {
    /**Location of the shader json definition file*/
    private final Identifier location;
    private final Map<String, U> managedUniforms = new HashMap<>();
    private final List<U> allUniforms = new ArrayList<>();
    private boolean errored;
    @CheckForNull
    protected S shader;

    public ResettableManagedShaderBase(Identifier location) {
        this.location = location;
    }

    @API(status = INTERNAL)
    public void initializeOrLog(ResourceFactory mgr) {
        try {
            this.initialize(mgr);
        } catch (ShaderLoader.LoadException e) {
            this.errored = true;
            this.logInitError(e);
        }
    }

    protected abstract void logInitError(ShaderLoader.LoadException e);

    protected void initialize(ResourceFactory resourceManager) throws ShaderLoader.LoadException {
        this.release();
        this.shader = parseShader(resourceManager, MinecraftClient.getInstance(), this.location);
        this.setup();
    }

    protected abstract S parseShader(ResourceFactory resourceFactory, MinecraftClient mc, Identifier location) throws ShaderLoader.LoadException;

    public void release() {
        if (this.isInitialized()) {
            try {
                assert this.shader != null;
                this.doRelease(shader);
                this.shader = null;
            } catch (Exception e) {
                throw new RuntimeException("Failed to release shader " + this.location, e);
            }
        }
        this.errored = false;
    }

    protected abstract void doRelease(S shader);

    protected Collection<U> getManagedUniforms() {
        return this.allUniforms;
    }

    protected abstract boolean setupUniform(U uniform, S shader);

    public boolean isInitialized() {
        return this.shader != null;
    }

    public boolean isErrored() {
        return this.errored;
    }

    public Identifier getLocation() {
        return location;
    }

    protected <V extends U> V manageUniform(Map<String, V> uniformMap, Function<String, V> factory, String uniformName, String uniformKind) {
        V existing = uniformMap.get(uniformName);
        if (existing != null) {
            return existing;
        }
        V ret = factory.apply(uniformName);
        if (this.shader != null) {
            boolean found = setupUniform(ret, shader);
            if (!found) {
                Satin.LOGGER.warn("No {} found with name {} in shader {}", uniformKind, uniformName, this.location);
            }
        }
        uniformMap.put(uniformName, ret);
        allUniforms.add(ret);
        return ret;
    }

    @Override
    public Uniform1i findUniform1i(String uniformName) {
        return manageUniform(this.managedUniforms, name -> createUniform(name, 1), uniformName, "uniform");
    }

    protected abstract @NotNull U createUniform(String name, int count);

    @Override
    public Uniform2i findUniform2i(String uniformName) {
        return manageUniform(this.managedUniforms, name -> createUniform(name, 2), uniformName, "uniform");
    }

    @Override
    public Uniform3i findUniform3i(String uniformName) {
        return manageUniform(this.managedUniforms, name -> createUniform(name, 3), uniformName, "uniform");
    }

    @Override
    public Uniform4i findUniform4i(String uniformName) {
        return manageUniform(this.managedUniforms, name -> createUniform(name, 4), uniformName, "uniform");
    }

    @Override
    public Uniform1f findUniform1f(String uniformName) {
        return manageUniform(this.managedUniforms, name -> createUniform(name, 1), uniformName, "uniform");
    }

    @Override
    public Uniform2f findUniform2f(String uniformName) {
        return manageUniform(this.managedUniforms, name -> createUniform(name, 2), uniformName, "uniform");
    }

    @Override
    public Uniform3f findUniform3f(String uniformName) {
        return manageUniform(this.managedUniforms, name -> createUniform(name, 3), uniformName, "uniform");
    }

    @Override
    public Uniform4f findUniform4f(String uniformName) {
        return manageUniform(this.managedUniforms, name -> createUniform(name, 4), uniformName, "uniform");
    }

    @Override
    public UniformMat4 findUniformMat4(String uniformName) {
        return manageUniform(this.managedUniforms, name -> createUniform(name, 16), uniformName, "uniform");
    }

    @API(status = INTERNAL)
    public abstract void setup();

    @Override
    public String toString() {
        return "%s[%s]".formatted(this.getClass().getSimpleName(), this.location);
    }
}
