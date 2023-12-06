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
package ladysnake.satin.mixin.client.gl;

import ladysnake.satin.impl.SamplerAccess;
import net.minecraft.client.gl.Program;
import net.minecraft.client.render.Shader;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Map;

@Mixin(Shader.class)
public abstract class CoreShaderMixin implements SamplerAccess {
    @Shadow @Final private Map<String, Object> samplers;

    @Override
    public void satin$removeSampler(String name) {
        this.samplers.remove(name);
    }

    @Override
    public boolean satin$hasSampler(String name) {
        return this.samplers.containsKey(name);
    }

    @Accessor("samplerNames")
    public abstract List<String> satin$getSamplerNames();

    @Accessor("loadedSamplerIds")
    public abstract List<Integer> satin$getSamplerShaderLocs();

    @ModifyVariable(method = "loadProgram", at = @At("STORE"), ordinal = 1)
    private static String fixPath(String path, final ResourceFactory factory, Program.Type type, String name) {
        if (!name.contains(":")) {
            return path;
        }
        Identifier split = new Identifier(name);
        return split.getNamespace() + ":shaders/core/" + split.getPath() + type.getFileExtension();
    }

}
