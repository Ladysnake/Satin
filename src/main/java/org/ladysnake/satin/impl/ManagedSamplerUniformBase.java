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

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.gl.ShaderProgram;
import org.ladysnake.satin.api.managed.uniform.SamplerUniform;

import java.util.List;
import java.util.function.Supplier;

/**
 *
 *
 * <p>So we need to deal with both those extremely similar implementations
 */
public abstract class ManagedSamplerUniformBase extends ManagedUniformBase implements SamplerUniform {
    protected SamplerAccess[] targets = new SamplerAccess[0];
    protected int[] locations = new int[0];
    protected Supplier<GpuTexture> value;

    public ManagedSamplerUniformBase(String name) {
        super(name);
    }

    private int getSamplerLoc(SamplerAccess access) {
        List<String> samplerNames = access.satin$getSamplerNames();
        for (int i = 0; i < samplerNames.size(); i++) {
            if (samplerNames.get(i).equals(this.name)) {
                return access.satin$getSamplerShaderLocs().getInt(i);
            }
        }
        return -1;
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

    protected void syncCurrentValues() {
        Supplier<GpuTexture> value = this.value;
        if (value != null) { // after the first upload
            this.value = null;
            this.set(value);
        }
    }

    protected abstract void set(Supplier<GpuTexture> value);

}
