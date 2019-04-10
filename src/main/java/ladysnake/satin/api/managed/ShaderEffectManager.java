package ladysnake.satin.api.managed;

import ladysnake.satin.impl.ReloadableShaderEffectManager;
import net.minecraft.util.Identifier;
import org.apiguardian.api.API;

import java.util.function.Consumer;

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
     * @param uniformInitCallback a block ran once to initialize uniforms
     * @return a lazily initialized screen shader
     */
    @API(status = STABLE, since = "1.0.0")
    ManagedShaderEffect manage(Identifier location, Consumer<ManagedShaderEffect> uniformInitCallback);

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
}
