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
package ladysnake.satin.mixin.client.gl;

import ladysnake.satin.impl.SamplerAccess;
import net.minecraft.client.gl.JsonEffectGlShader;
import net.minecraft.client.gl.Program;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

/**
 * Minecraft does not take into account domains when parsing a shader program.
 * These hooks redirect identifier instantiations to allow specifying a domain for shader files.
 */
@Mixin(JsonEffectGlShader.class)
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
    @Accessor("samplerShaderLocs")
    public abstract List<Integer> satin$getSamplerShaderLocs();

    /**
     * Fix identifier creation to allow different namespaces
     *
     * <p>Why a redirect ?
     * <ul>
     * <li>Because letting the identifier be built will throw an exception, so no ModifyVariable</li>
     * <li>Because we need to access the original id, so no ModifyArg (alternatively we could use 2 injections and a ThreadLocal but:)</li>
     * <li>Because we assume other people who may want to do the same change can use this library</li>
     * </ul>
     * @param arg the string passed to the redirected Identifier constructor
     * @param id the actual id passed as an argument to the method
     * @return a new Identifier
     */
    @Redirect(
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/util/Identifier",
                    ordinal = 0
            ),
            method = "<init>"
    )
    Identifier constructProgramIdentifier(String arg, ResourceManager unused, String id) {
        if (!id.contains(":")) {
            return new Identifier(arg);
        }
        Identifier split = new Identifier(id);
        return new Identifier(split.getNamespace(), "shaders/program/" + split.getPath() + ".json");
    }

    /**
     * @param arg the string passed to the redirected Identifier constructor
     * @param id the actual id passed as an argument to the method
     * @return a new Identifier
     */
    @Redirect(
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/util/Identifier",
                    ordinal = 0
            ),
            method = "loadEffect"
    )
    private static Identifier constructProgramIdentifier(String arg, ResourceManager unused, Program.Type shaderType, String id) {
        if (!arg.contains(":")) {
            return new Identifier(arg);
        }
        Identifier split = new Identifier(id);
        return new Identifier(split.getNamespace(), "shaders/program/" + split.getPath() + shaderType.getFileExtension());
    }
}
