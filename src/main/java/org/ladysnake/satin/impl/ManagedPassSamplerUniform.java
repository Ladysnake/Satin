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

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.managed.uniform.SamplerUniformV2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * A sampler uniform applying to a {@link PostEffectPass}
 */
public final class ManagedPassSamplerUniform extends ManagedSamplerUniformBase implements SamplerUniformV2, PostEffectPass.Sampler {
    public ManagedPassSamplerUniform(String name) {
        super(name);
    }

    @Override
    public void preRender(FramePass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
        // NO-OP
    }

    @Override
    public void bindSampler(RenderPass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
        pass.bindSampler(this.name, this.value.get());
    }

    @Override
    public boolean findUniformTargets(List<PostEffectPass> passes) {
        List<SamplerAccess> targets = new ArrayList<>(passes.size());
        boolean found = false;
        for (PostEffectPass pass : passes) {
            pass.addSampler(this);
            found = true;
        }
        this.targets = targets.toArray(new SamplerAccess[0]);
        this.syncCurrentValues();
        return found;
    }

    @Override
    public void set(AbstractTexture texture) {
        set(texture::getGlTexture);
    }

    @Override
    public void set(Framebuffer textureFbo) {
        set(textureFbo::getColorAttachment);
    }

    @Override
    public void set(Supplier<GpuTexture> value) {
        SamplerAccess[] targets = this.targets;
        if (targets.length > 0 && this.value != value) {
            this.value = value;
        }
    }
}
