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
package org.ladysnake.satin.mixin.client.gl;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramDefinition;
import org.ladysnake.satin.impl.SamplerAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Mixin(ShaderProgram.class)
public abstract class CoreShaderMixin implements SamplerAccess {
    @Shadow @Final private List<ShaderProgramDefinition.Sampler> samplers;

    @Override
    public void satin$removeSampler(String name) {
        for (Iterator<ShaderProgramDefinition.Sampler> iterator = this.samplers.iterator(); iterator.hasNext(); ) {
            ShaderProgramDefinition.Sampler sampler = iterator.next();
            if (Objects.equals(sampler.name(), name)) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean satin$hasSampler(String name) {
        for (ShaderProgramDefinition.Sampler sampler : samplers) {
            if (sampler.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Accessor("samplers")
    public abstract List<ShaderProgramDefinition.Sampler> satin$getSamplerNames();

    @Override
    @Accessor("samplerLocations")
    public abstract IntList satin$getSamplerShaderLocs();
}
