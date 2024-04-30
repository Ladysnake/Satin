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
package ladysnake.satin.api.util;

import ladysnake.satin.impl.ValidatingShaderLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * A {@link ShaderLoader} simplifies the process of loading, creating and linking OpenGL shader objects
 *
 * @since 1.0.0
 */
public interface ShaderLoader {
    @API(status = MAINTAINED)
    static ShaderLoader getInstance() {
        return ValidatingShaderLoader.INSTANCE;
    }

    /**
     * Initializes a program from a fragment and a vertex source file
     *
     * @param vertexLocation   the name or relative location of the vertex shader
     * @param fragmentLocation the name or relative location of the fragment shader
     * @return the reference to the initialized program
     * @throws IOException If an I/O error occurs while reading the shader source files
     * @throws ShaderLinkException if the shader fails to be linked
     */
    @API(status = MAINTAINED)
    int loadShader(ResourceManager resourceManager, @Nullable Identifier vertexLocation, @Nullable Identifier fragmentLocation) throws IOException;
}
