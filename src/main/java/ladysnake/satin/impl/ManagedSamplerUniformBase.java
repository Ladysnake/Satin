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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import ladysnake.satin.api.managed.uniform.SamplerUniform;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.ShaderProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

/**
 * Mojank working in divergent branches for half a century, {@link net.minecraft.client.gl.ShaderProgram}
 * is a copy of an old implementation of {@link net.minecraft.client.gl.JsonEffectShaderProgram}.
 * The latter has since been updated to have the {@link net.minecraft.client.gl.JsonEffectShaderProgram#bindSampler(String, IntSupplier)}
 * while the former still uses {@link net.minecraft.client.gl.ShaderProgram#addSampler(String, Object)}.
 *
 * <p>So we need to deal with both those extremely similar implementations
 */
public abstract class ManagedSamplerUniformBase extends ManagedUniformBase implements SamplerUniform {
    protected SamplerAccess[] targets = new SamplerAccess[0];
    protected int[] locations = new int[0];
    protected Object cachedValue;

    public ManagedSamplerUniformBase(String name) {
        super(name);
    }

    @Override
    public boolean findUniformTargets(List<PostEffectPass> shaders) {
        List<SamplerAccess> targets = new ArrayList<>(shaders.size());
        IntList rawTargets = new IntArrayList(shaders.size());
        for (PostEffectPass shader : shaders) {
            JsonEffectShaderProgram program = shader.getProgram();
            SamplerAccess access = (SamplerAccess) program;
            if (access.satin$hasSampler(this.name)) {
                targets.add(access);
                rawTargets.add(getSamplerLoc(access));
            }
        }
        this.targets = targets.toArray(new SamplerAccess[0]);
        this.locations = rawTargets.toArray(new int[0]);
        this.syncCurrentValues();
        return this.targets.length > 0;
    }

    private int getSamplerLoc(SamplerAccess access) {
        return access.satin$getSamplerShaderLocs().get(access.satin$getSamplerNames().indexOf(this.name));
    }

    @Override
    public boolean findUniformTarget(ShaderProgram shader) {
        return findUniformTarget(((SamplerAccess) shader));
    }

    private boolean findUniformTarget(SamplerAccess access) {
        if (access.satin$hasSampler(this.name)) {
            this.targets = new SamplerAccess[] {access};
            this.locations = new int[] {getSamplerLoc(access)};
            this.syncCurrentValues();
            return true;
        }
        return false;
    }

    private void syncCurrentValues() {
        Object value = this.cachedValue;
        if (value != null) { // after the first upload
            this.cachedValue = null;
            this.set(value);
        }
    }

    protected abstract void set(Object value);

    @Override
    public void setDirect(int activeTexture) {
        int length = this.locations.length;
        for (int i = 0; i < length; i++) {
            this.targets[i].satin$removeSampler(this.name);
            GlUniform.uniform1(this.locations[i], activeTexture);
        }
    }
}
