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

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import ladysnake.satin.Satin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.util.Identifier;
import org.apiguardian.api.API;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.IntConsumer;

import static org.apiguardian.api.API.Status.*;

/**
 * This class consists exclusively of static methods that operate on
 * OpenGL shader program objects.
 */
public final class ShaderPrograms {
    private ShaderPrograms() {}

    /**A map of programs to maps of uniform names to location*/
    private static final Int2ObjectMap<Object2IntMap<String>> uniformsCache = new Int2ObjectOpenHashMap<>();

    /**
     * Sets the currently used program.
     *
     * <p>
     *     If shaders are disallowed in the current game instance, this method returns immediately
     *     with no side effect.
     *
     * @param program the reference to the desired shader (0 to remove any current shader)
     */
    @API(status = MAINTAINED)
    public static void useShader(int program) {
        if (Satin.areShadersDisabled()) {
            return;
        }

        GlProgramManager.useProgram(program);
    }

    /**
     * Sets the value of an attribute from the current shader program using the given operation.
     * <p>
     * <code>operation</code> will only be called if shaders are enabled and an attribute with the given name exists
     * in the current program. It should call one of {@link GL20} attrib functions (eg. {@link GL20#glBindAttribLocation(int, int, ByteBuffer)}).
     *
     * @param program    OpenGL shader program object
     * @param attribName the name of the attribute field in the shader source file
     * @param operation  a gl operation to apply to this uniform
     */
    @API(status = EXPERIMENTAL)
    public static void setAttribValue(int program, String attribName, IntConsumer operation) {
        if (Satin.areShadersDisabled() || program == 0) {
            return;
        }

        int attrib = GlUniform.getAttribLocation(program, attribName);
        if (attrib != -1) {
            operation.accept(attrib);
        }
    }

    /**
     * Sets the value of a uniform from the current shader program using the given operation.
     * <p>
     * <code>operation</code> will only be called if shaders are enabled and a uniform with the given name exists
     * in the current program. It should call one of {@link GL20} uniform functions (eg. {@link GL20#glUniform1iv(int, IntBuffer)}).
     *
     * @param program     OpenGL shader program object
     * @param uniformName the name of the uniform field in the shader source file
     * @param operation   a gl operation to apply to this uniform
     */
    @API(status = EXPERIMENTAL)
    public static void setUniformValue(int program, String uniformName, IntConsumer operation) {
        if (Satin.areShadersDisabled() || program == 0) {
            return;
        }

        int uniform = getUniformLocation(program, uniformName);
        if (uniform != -1) {
            operation.accept(uniform);
        }
    }

    /**
     * Sets the value of an int uniform from the current shader program
     *
     * @param program     OpenGL shader program object
     * @param uniformName the name of the uniform field in the shader source file
     * @param value       an int value for this uniform
     */
    @API(status = STABLE)
    public static void setUniform(int program, String uniformName, int value) {
        if (Satin.areShadersDisabled() || program == 0) {
            return;
        }

        int uniform = getUniformLocation(program, uniformName);
        if (uniform != -1) {
            GL20.glUniform1i(uniform, value);
        }
    }

    /**
     * Sets the value of a float uniform field from the current shader program
     *
     * @param program     OpenGL shader program object
     * @param uniformName the name of the uniform field in the shader source file
     * @param value       float value of the uniform
     */
    @API(status = EXPERIMENTAL)
    public static void setUniform(int program, String uniformName, float value) {
        if (Satin.areShadersDisabled() || program == 0) {
            return;
        }

        int uniform = getUniformLocation(program, uniformName);
        if (uniform != -1) {
            GL20.glUniform1f(uniform, value);
        }
    }

    /**
     * Sets the value of a mat4 uniform in the current shader
     *
     * @param program     OpenGL shader program object
     * @param uniformName the name of the uniform field in the shader source file
     * @param mat4        a raw array of float values
     */
    @API(status = EXPERIMENTAL)
    public static void setUniform(int program, String uniformName, FloatBuffer mat4) {
        if (Satin.areShadersDisabled() || program == 0) {
            return;
        }

        int uniform = getUniformLocation(program, uniformName);
        if (uniform != -1) {
            GL20.glUniformMatrix4fv(uniform, false, mat4);
        }
    }

    /**
     * {@code getUniformLocation} returns an integer that represents the location
     * of a specific uniform variable within a program object.
     * <p>
     *     {@code name} must be a string that contains no white space.
     *     {@code name} must be an active uniform variable name in program that is not a structure,
     *     an array of structures, or a subcomponent of a vector or a matrix.
     * <p>
     *     This function returns -1 if name does not correspond to an active
     *     uniform variable in {@code program} or if {@code name} starts with the
     *     reserved prefix "gl_".
     * <p>
     *     Uniform locations obtained through this method are cached, limiting
     *     performance loss from consecutive calls.
     *
     * @param program     program object to be queried
     * @param uniformName string containing the name of the uniform variable
     *                    whose location is to be queried
     * @return an integer that represents the location of a specific uniform
     * variable within a program object
     */
    @API(status = MAINTAINED)
    public static int getUniformLocation(int program, String uniformName) {
        // Gets the uniform cache for the current program
        Object2IntMap<String> shaderUniformsCache = uniformsCache.get(program);
        // Compute if absent
        if (shaderUniformsCache == null) {
            shaderUniformsCache = new Object2IntOpenHashMap<>();
            uniformsCache.put(program, shaderUniformsCache);
        }
        // Gets the uniform location from the cache
        int uniform;
        if (shaderUniformsCache.containsKey(uniformName)) {
            uniform = shaderUniformsCache.getInt(uniformName);
        } else {
            // Compute if absent
            uniform = GlUniform.getUniformLocation(program, uniformName);
            shaderUniformsCache.put(uniformName, uniform);
        }
        return uniform;
    }

    /**
     * Binds any number of additional textures to be used by the current shader.
     * <p>
     * The default texture (0) is unaffected.
     * Shaders can access these textures by using uniforms named "textureN" with N
     * being the index of the additional texture, starting at 1.
     * </p>
     *
     * <u>Example:</u> The call {@code bindAdditionalTextures(rl1, rl2, rl3)} will let the shader
     * access those textures via the uniforms <pre>{@code
     * uniform sampler2D texture;   // the texture that's currently being drawn
     * uniform sampler2D texture1;  // the texture designated by rl1
     * uniform sampler2D texture2;  // the texture designated by rl2
     * uniform sampler2D texture3;  // the texture designated by rl3
     * }</pre>
     */
    @API(status = EXPERIMENTAL)
    public static void bindAdditionalTextures(int program, Identifier... textures) {
        for (int i = 0; i < textures.length; i++) {
            Identifier texture = textures[i];
            // don't mess with the lightmap (1) nor the default texture (0)
            RenderSystem.activeTexture(i + GL20.GL_TEXTURE2);
            MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
            // start texture uniforms at 1, as 0 would be the default texture which doesn't require any special operation
            setUniform(program, "texture" + (i + 1), i + 2);
        }
        RenderSystem.activeTexture(GL20.GL_TEXTURE0);
    }
}
