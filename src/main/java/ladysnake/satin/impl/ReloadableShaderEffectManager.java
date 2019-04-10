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
    private static Set<ResettableManagedShaderEffect> managedShaderEffects = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * Manages a post processing shader loaded from a json definition file
     *
     * @param location the location of the json within your mod's assets
     * @return a lazily initialized shader effect
     */
    @Override
    public ManagedShaderEffect manage(Identifier location) {
        return manage(location, s -> {
        });
    }

    /**
     * Manages a post processing shader loaded from a json definition file
     *
     * @param location            the location of the json within your mod's assets
     * @param uniformInitCallback a block ran once to initialize uniforms
     * @return a lazily initialized screen shader
     */
    @Override
    public ManagedShaderEffect manage(Identifier location, Consumer<ManagedShaderEffect> uniformInitCallback) {
        ResettableManagedShaderEffect ret = new ResettableManagedShaderEffect(location, uniformInitCallback);
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
        for (ManagedShaderEffect ss : managedShaderEffects) {
            ss.release();
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
