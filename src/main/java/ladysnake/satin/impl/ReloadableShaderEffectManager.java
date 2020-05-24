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
package ladysnake.satin.impl;

import ladysnake.satin.Satin;
import ladysnake.satin.api.event.ResolutionChangeCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * A {@link ShaderEffectManager} that is reloaded along with resources
 *
 * @see ShaderEffectManager
 * @see ResettableManagedShaderEffect
 */
public final class ReloadableShaderEffectManager implements ShaderEffectManager, SimpleSynchronousResourceReloadListener, ResolutionChangeCallback {
    public static final ReloadableShaderEffectManager INSTANCE = new ReloadableShaderEffectManager();
    public static final Identifier SHADER_RESOURCE_KEY = new Identifier("dissolution:shaders");

    // Let shaders be garbage collected when no one uses them
    private Set<ResettableManagedShaderEffect> managedShaderEffects = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * Manages a post processing shader loaded from a json definition file
     *
     * @param location the location of the json within your mod's assets
     * @return a lazily initialized shader effect
     */
    @Override
    public ManagedShaderEffect manage(Identifier location) {
        return manage(location, s -> { });
    }

    /**
     * Manages a post processing shader loaded from a json definition file
     *
     * @param location            the location of the json within your mod's assets
     * @param initCallback a block ran once the shader effect is initialized
     * @return a lazily initialized screen shader
     */
    @Override
    public ManagedShaderEffect manage(Identifier location, Consumer<ManagedShaderEffect> initCallback) {
        ResettableManagedShaderEffect ret = new ResettableManagedShaderEffect(location, initCallback);
        managedShaderEffects.add(ret);
        return ret;
    }

    /**
     * Removes a shader from the global list of managed shaders,
     * making it not respond to resource reloading and screen resizing.
     * This also calls {@link ResettableManagedShaderEffect#release()} to release the shader's resources.
     * A <code>ManagedShaderEffect</code> object cannot be used after it has been disposed of.
     *
     * @param shader the shader to stop managing
     * @see ResettableManagedShaderEffect#release()
     */
    @Override
    public void dispose(ManagedShaderEffect shader) {
        shader.release();
        managedShaderEffects.remove(shader);
    }

    @Override
    public Identifier getFabricId() {
        return SHADER_RESOURCE_KEY;
    }

    @Override
    public void apply(ResourceManager var1) {
        for (ResettableManagedShaderEffect ss : managedShaderEffects) {
            try {
                ss.initialize();
            } catch (Exception e) {
                Satin.LOGGER.error("[Satin] Could not create screen shader {}", ss.getLocation(), e);
                ss.setErrored(true);
            }
        }
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        if (!Satin.areShadersDisabled() && !managedShaderEffects.isEmpty()) {
            for (ResettableManagedShaderEffect ss : managedShaderEffects) {
                if (ss.isInitialized()) {
                    ss.setup(newWidth, newHeight);
                }
            }
        }
    }

}
