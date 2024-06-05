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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.impl.SamplerAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

/**
 * Minecraft does not take into account domains when parsing a shader program.
 * These hooks redirect identifier instantiations to allow specifying a domain for shader files.
 */
@Mixin(JsonEffectShaderProgram.class)
public abstract class JsonEffectGlShaderMixin implements SamplerAccess {
    @Shadow @Final private Map<String, IntSupplier> samplerBinds;

    @Override
    public void satin$removeSampler(String name) {
        this.samplerBinds.remove(name);
    }

    @Override
    public boolean satin$hasSampler(String name) {
        return this.samplerBinds.containsKey(name);
    }

    @Override
    @Accessor("samplerNames")
    public abstract List<String> satin$getSamplerNames();

    @Override
    @Accessor("samplerLocations")
    public abstract List<Integer> satin$getSamplerShaderLocs();

    /**
     * Fix identifier creation to allow different namespaces
     */
    @WrapOperation(
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/util/Identifier.ofVanilla (Ljava/lang/String;)Lnet/minecraft/util/Identifier;",
                    ordinal = 0
            ),
            method = "<init>"
    )
    Identifier constructProgramIdentifier(String arg, Operation<Identifier> original, ResourceFactory unused, String id) {
        if (!id.contains(":")) {
            return original.call(arg);
        }
        Identifier split = Identifier.of(id);
        return Identifier.of(split.getNamespace(), "shaders/program/" + split.getPath() + ".json");
    }

    @WrapOperation(
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/util/Identifier.ofVanilla (Ljava/lang/String;)Lnet/minecraft/util/Identifier;",
                    ordinal = 0
            ),
            method = "loadEffect"
    )
    private static Identifier constructProgramIdentifier(String arg, Operation<Identifier> original, ResourceFactory unused, ShaderStage.Type shaderType, String id) {
        if (!arg.contains(":")) {
            return original.call(arg);
        }
        Identifier split = Identifier.of(id);
        return Identifier.of(split.getNamespace(), "shaders/program/" + split.getPath() + shaderType.getFileExtension());
    }
}
