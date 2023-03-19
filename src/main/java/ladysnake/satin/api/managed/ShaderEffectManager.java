/*
 * Satin
 * Copyright (C) 2019-2023 Ladysnake
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
package ladysnake.satin.api.managed;

import ladysnake.satin.impl.ReloadableShaderEffectManager;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.apiguardian.api.API;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see ManagedShaderEffect
 */
public interface ShaderEffectManager {
    @API(status = STABLE)
    static ShaderEffectManager getInstance() {
        return ReloadableShaderEffectManager.INSTANCE;
    }

    /**
     * Manages a post-process {@link PostEffectProcessor} loaded from a json definition file
     *
     * @param location the location of the json within your mod's assets
     * @return a screen shader that will be automatically reloaded as needed
     */
    @API(status = STABLE, since = "1.0.0")
    ManagedShaderEffect manage(Identifier location);

    /**
     * Manages a post-process {@link PostEffectProcessor} loaded from a json definition file
     *
     * @param location         the location of the json within your mod's assets
     * @param initCallback a block ran once the shader effect is initialized
     * @return a screen shader that will be automatically reloaded as needed
     */
    @API(status = STABLE, since = "1.0.0")
    ManagedShaderEffect manage(Identifier location, Consumer<ManagedShaderEffect> initCallback);

    /**
     * @deprecated use {@link #manageCoreShader(Identifier)}.
     * <strong>Shader files must go in {@code shaders/core} instead of {@code shaders/program}
     */
    @Deprecated(since = "1.6.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval
    default ManagedCoreShader manageProgram(Identifier location) {
        return manageCoreShader(location);
    }

    /**
     * @deprecated use {@link #manageCoreShader(Identifier, VertexFormat, Consumer)}.
     * <strong>Shader files must go in {@code shaders/core} instead of {@code shaders/program}
     */
    @Deprecated(since = "1.6.0", forRemoval = true)
    @ApiStatus.ScheduledForRemoval
    default ManagedCoreShader manageProgram(Identifier location, Consumer<ManagedCoreShader> initCallback) {
        return manageCoreShader(location, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, initCallback);
    }

    /**
     * Manages a core {@link net.minecraft.client.gl.ShaderProgram} loaded from a json definition file
     *
     * <p>This overload forwards to {@link #manageCoreShader(Identifier, VertexFormat)} with {@link VertexFormats#POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL},
     * the format used by entity rendering
     *
     * <p>The shader location must be in {@code assets/shaders/core}.
     * Eg. to get a shader located at {@code mymod/assets/shaders/core/myshader.json}:
     * <pre>{@code ShaderEffectManager.getInstance().manageCoreShader(new Identifier("mymod", "myshader")}</pre>
     *
     * @param location the location of the json within your mod's assets
     * @return a core render shader that will be reloaded as needed
     */
    @API(status = EXPERIMENTAL, since = "1.6.0")
    ManagedCoreShader manageCoreShader(Identifier location);

    /**
     * Manages a core {@link net.minecraft.client.gl.ShaderProgram} loaded from a json definition file
     *
     * <p>The shader location must be in {@code assets/shaders/core}.
     * Eg. to get a shader located at {@code mymod/assets/shaders/core/myshader.json}:
     * <pre>{@code ShaderEffectManager.getInstance().manageCoreShader(new Identifier("mymod", "myshader")}</pre>
     *
     * @param location the location of the json within your mod's assets
     * @param vertexFormat the format expected by your shader
     * @return a core render shader that will be reloaded as needed
     */
    @API(status = EXPERIMENTAL, since = "1.6.0")
    ManagedCoreShader manageCoreShader(Identifier location, VertexFormat vertexFormat);

    /**
     * Manages a core {@link net.minecraft.client.gl.ShaderProgram} loaded from a json definition file
     *
     * <p>The shader location must be in {@code assets/shaders/core}.
     * E.g. to get a shader located at {@code mymod/assets/shaders/core/myshader.json}:
     * <pre>{@code ShaderEffectManager.getInstance().manageCoreShader(new Identifier("mymod", "myshader")}</pre>
     *
     * @param location the location of the json within your mod's assets
     * @param vertexFormat the format expected by your shader
     * @param initCallback a block ran once the shader effect is initialized
     * @return a core render shader that will be reloaded as needed
     */
    @API(status = EXPERIMENTAL, since = "1.6.0")
    ManagedCoreShader manageCoreShader(Identifier location, VertexFormat vertexFormat, Consumer<ManagedCoreShader> initCallback);

    /**
     * Removes a shader from the global list of managed shaders,
     * making it not respond to resource reloading and screen resizing.
     * This also calls {@link ManagedShaderEffect#release()} to release the shader's resources.
     * A <code>ManagedShaderEffect</code> object cannot be used after it has been disposed of.
     *
     * @param shader the shader to stop managing
     * @see ManagedShaderEffect#release()
     */
    @API(status = STABLE, since = "1.0.0")
    void dispose(ManagedShaderEffect shader);

    @API(status = EXPERIMENTAL, since = "1.4.0")
    void dispose(ManagedCoreShader shader);
}
