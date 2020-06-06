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
package ladysnake.satin.api.managed;

import ladysnake.satin.impl.ReloadableShaderEffectManager;
import net.minecraft.util.Identifier;
import org.apiguardian.api.API;

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
     * Manages a post processing shader loaded from a json definition file
     *
     * @param location the location of the json within your mod's assets
     * @return a lazily initialized shader effect
     */
    @API(status = STABLE, since = "1.0.0")
    ManagedShaderEffect manage(Identifier location);

    /**
     * Manages a post processing shader loaded from a json definition file
     *
     * @param location         the location of the json within your mod's assets
     * @param initCallback a block ran once the shader effect is initialized
     * @return a lazily initialized screen shader
     */
    @API(status = STABLE, since = "1.0.0")
    ManagedShaderEffect manage(Identifier location, Consumer<ManagedShaderEffect> initCallback);

    @API(status = EXPERIMENTAL, since = "1.4.0")
    ManagedShaderProgram manageProgram(Identifier location);

    @API(status = EXPERIMENTAL, since = "1.4.0")
    ManagedShaderProgram manageProgram(Identifier location, Consumer<ManagedShaderProgram> initCallback);

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
    void dispose(ManagedShaderProgram shader);
}
