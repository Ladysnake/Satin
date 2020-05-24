/*
 * Satin
 * Copyright (C) 2019-2020 Ladysnake
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

import net.minecraft.client.gl.GlShader;
import net.minecraft.client.gl.JsonGlProgram;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Minecraft does not take into account domains when parsing a shader program.
 * These hooks redirect identifier instantiations to allow specifying a domain for shader files.
 */
@Mixin(JsonGlProgram.class)
public abstract class JsonGlProgramMixin {
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
            method = "<init>"
    )
    private Identifier constructProgramIdentifier(String arg, ResourceManager unused, String id) {
        if (!arg.contains(":")) {
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
            method = "getShader"
    )
    private static Identifier constructProgramIdentifier(String arg, ResourceManager unused, GlShader.Type shaderType, String id) {
        if (!arg.contains(":")) {
            return new Identifier(arg);
        }
        Identifier split = new Identifier(id);
        return new Identifier(split.getNamespace(), "shaders/program/" + split.getPath() + shaderType.getFileExtension());
    }
}
