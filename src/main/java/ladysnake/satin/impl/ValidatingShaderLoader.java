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
package ladysnake.satin.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import ladysnake.satin.Satin;
import ladysnake.satin.api.util.ShaderLinkException;
import ladysnake.satin.api.util.ShaderLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL30;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;

/**
 * A {@link ShaderLoader} that validates loaded GLSL code and logs errors
 */
public final class ValidatingShaderLoader implements ShaderLoader {
    public static final ShaderLoader INSTANCE = new ValidatingShaderLoader();

    /**
     * Initializes a program from a fragment and a vertex source file
     *
     * @param vertexLocation   the name or relative location of the vertex shader
     * @param fragmentLocation the name or relative location of the fragment shader
     * @return the reference to the initialized program
     * @throws IOException If an I/O error occurs while reading the shader source files
     */
    public int loadShader(ResourceManager resourceManager, @Nullable Identifier vertexLocation, @Nullable Identifier fragmentLocation) throws IOException {

        // program creation
        int programId = GlStateManager.glCreateProgram();

        int vertexShaderId = 0;
        int fragmentShaderId = 0;

        // vertex shader creation
        if (vertexLocation != null) {
            vertexShaderId = GlStateManager.glCreateShader(GL30.GL_VERTEX_SHADER);
            ARBShaderObjects.glShaderSourceARB(vertexShaderId, fromFile(resourceManager, vertexLocation));
            ARBShaderObjects.glCompileShaderARB(vertexShaderId);
            ARBShaderObjects.glAttachObjectARB(programId, vertexShaderId);
            String log = glGetShaderInfoLog(vertexShaderId, 1024);
            if (!log.isEmpty()) {
                Satin.LOGGER.error("Could not compile vertex shader {}: {}", vertexLocation, log);
            }
        }

        // fragment shader creation
        if (fragmentLocation != null) {
            fragmentShaderId = GlStateManager.glCreateShader(GL30.GL_FRAGMENT_SHADER);
            ARBShaderObjects.glShaderSourceARB(fragmentShaderId, fromFile(resourceManager, fragmentLocation));
            ARBShaderObjects.glCompileShaderARB(fragmentShaderId);
            ARBShaderObjects.glAttachObjectARB(programId, fragmentShaderId);
            String log = glGetShaderInfoLog(fragmentShaderId, 1024);
            if (!log.isEmpty()) {
                Satin.LOGGER.error("Could not compile fragment shader {}: {}", fragmentLocation, log);
            }
        }

        GlStateManager.glLinkProgram(programId);
        // check potential linkage errors
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new ShaderLinkException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        // free up the vertex and fragment shaders
        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
            glDeleteShader(vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
            glDeleteShader(fragmentShaderId);
        }

        // validate the program
        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            Satin.LOGGER.warn("Warning validating Shader code: {}", glGetProgramInfoLog(programId, 1024));
        }

        return programId;
    }

    /**
     * Reads a text file into a single String
     *
     * @param fileLocation the path to the file to read
     * @return a string with the content of the file
     * @throws java.io.FileNotFoundException if the designated shader file does not exist
     */
    private String fromFile(ResourceManager resourceManager, Identifier fileLocation) throws IOException {
        StringBuilder source = new StringBuilder();

        try (InputStream in = resourceManager.getResourceOrThrow(fileLocation).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line).append('\n');
            }
        }
        return source.toString();
    }
}
