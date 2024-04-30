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

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import ladysnake.satin.Satin;
import ladysnake.satin.api.event.ResolutionChangeCallback;
import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link ShaderEffectManager} that is reloaded along with resources
 *
 * @see ShaderEffectManager
 * @see ResettableManagedShaderEffect
 */
public final class ReloadableShaderEffectManager implements ShaderEffectManager, ResolutionChangeCallback {
    public static final ReloadableShaderEffectManager INSTANCE = new ReloadableShaderEffectManager();
    public static final Identifier SHADER_RESOURCE_KEY = new Identifier("dissolution:shaders");

    private final Set<ResettableManagedShaderBase<?>> managedShaders = new ReferenceOpenHashSet<>();

    /**
     * Manages a post-processing shader loaded from a json definition file
     *
     * @param location the location of the json within your mod's assets
     * @return a lazily initialized shader effect
     */
    @Override
    public ManagedShaderEffect manage(Identifier location) {
        return manage(location, s -> { });
    }

    /**
     * Manages a post-processing shader loaded from a json definition file
     *
     * @param location            the location of the json within your mod's assets
     * @param initCallback a block ran once the shader effect is initialized
     * @return a lazily initialized screen shader
     */
    @Override
    public ManagedShaderEffect manage(Identifier location, Consumer<ManagedShaderEffect> initCallback) {
        ResettableManagedShaderEffect ret = new ResettableManagedShaderEffect(location, initCallback);
        managedShaders.add(ret);
        return ret;
    }

    @Override
    public ManagedCoreShader manageCoreShader(Identifier location) {
        return manageCoreShader(location, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
    }

    @Override
    public ManagedCoreShader manageCoreShader(Identifier location, VertexFormat vertexFormat) {
        return manageCoreShader(location, vertexFormat, (s) -> {});
    }

    @Override
    public ManagedCoreShader manageCoreShader(Identifier location, VertexFormat vertexFormat, Consumer<ManagedCoreShader> initCallback) {
        ResettableManagedCoreShader ret = new ResettableManagedCoreShader(location, vertexFormat, initCallback);
        managedShaders.add(ret);
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
        managedShaders.remove(shader);
    }

    @Override
    public void dispose(ManagedCoreShader shader) {
        shader.release();
        managedShaders.remove(shader);
    }

    public void reload(ResourceFactory shaderResources) {
        for (ResettableManagedShaderBase<?> ss : managedShaders) {
            ss.initializeOrLog(shaderResources);
        }
    }

    @Override
    public void onResolutionChanged(int newWidth, int newHeight) {
        if (!Satin.areShadersDisabled() && !managedShaders.isEmpty()) {
            for (ResettableManagedShaderBase<?> ss : managedShaders) {
                if (ss.isInitialized()) {
                    ss.setup(newWidth, newHeight);
                }
            }
        }
    }
}
