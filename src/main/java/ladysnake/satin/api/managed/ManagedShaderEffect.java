package ladysnake.satin.api.managed;

import ladysnake.satin.api.experimental.managed.UniformFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.*;

/**
 * A post processing shader that is applied to the main framebuffer
 * <p>
 * Post shaders loaded through {@link ShaderEffectManager#manage(Identifier, Consumer)} are self-managed and will be
 * reloaded when shader assets are reloaded (through <tt>F3-T</tt> or <tt>/ladylib_shader_reload</tt>) or the
 * screen resolution changes.
 * <p>
 * Examples of json-defined shader effects are available in <tt>assets/minecraft/shaders</tt>.
 *
 * @see ShaderEffectManager
 * @since 1.0.0
 */
public interface ManagedShaderEffect extends UniformFinder {
    /**
     * Returns this object's managed {@link ShaderEffect}, creating and initializing it if it doesn't exist.
     * <p>
     * This method will return <code>null</code> if an error occurs during initialization.
     * <p>
     * <em>Note: calling this before the graphic context is ready will cause issues.</em>
     *
     * @return the {@link ShaderEffect} managed by this object
     * @see #initialize()
     * @see #isInitialized()
     */
    @Nullable
    @API(status = MAINTAINED, since = "1.0.0")
    ShaderEffect getShaderEffect();

    /**
     * Initializes this shader, allocating required system resources
     * such as framebuffer objects, shaders objects and texture objects.
     * Any exception thrown during initialization is relayed to the caller.
     * <p>
     * If the shader is already initialized, previously allocated
     * resources will be disposed of before initializing new ones.
     *
     * @apiNote Calling this method directly is not required in most cases.
     * @see #getShaderEffect()
     * @see #isInitialized()
     * @see #release()
     */
    @API(status = MAINTAINED, since = "1.0.0")
    void initialize() throws IOException;

    /**
     * Checks whether this shader is initialized. If it is not, next call to {@link #getShaderEffect()}
     * will setup the shader group.
     *
     * @return true if this does not require initialization
     * @see #initialize()
     */
    @API(status = MAINTAINED, since = "1.0.0")
    boolean isInitialized();

    /**
     * @return <code>true</code> if this shader erred during initialization
     */
    @API(status = MAINTAINED, since = "1.0.0")
    boolean isErrored();

    /**
     * Releases this shader's resources.
     * <p>
     * After this method is called, this shader will go back to its uninitialized state.
     * Future calls to {@link #isInitialized()} will return false until {@link #initialize()}
     * is called again, recreating the shader group.
     * <p>
     * Although the finalization process of the garbage collector
     * also disposes of the same system resources, it is preferable
     * to manually free the associated resources by calling this
     * method rather than to rely on a finalization process which
     * may not run to completion for a long period of time.
     * <p>
     * If the caller does not intend to use this shader effect again, they
     * should call {@link ShaderEffectManager#dispose(ManagedShaderEffect)}.
     * </p>
     *
     * @see ShaderEffectManager#dispose(ManagedShaderEffect)
     * @see #isInitialized()
     * @see #getShaderEffect()
     * @see #finalize()
     */
    @API(status = EXPERIMENTAL, since = "1.0.0")
    void release();

    /**
     * Renders this shader.
     *
     * <p>
     * Calling this method first setups the graphic state for rendering,
     * then uploads uniforms to the GPU if they have been changed since last
     * draw, draws the {@link MinecraftClient#getFramebuffer() main framebuffer}'s texture
     * to intermediate {@link Framebuffer framebuffers} as defined by the JSON files
     * and resets part of the graphic state. The shader will be {@link #initialize() initialized}
     * if it has not been before.
     * <p>
     * This method should be called every frame when the shader is active.
     * Uniforms should be set before rendering.
     */
    @API(status = STABLE, since = "1.0.0")
    void render(float tickDelta);

    /**
     * Forwards to {@link #setupDynamicUniforms(int, Runnable)} with an index of 0
     *
     * @param dynamicSetBlock a block in which dynamic uniforms are set
     */
    @API(status = EXPERIMENTAL, since = "1.0.0")
    void setupDynamicUniforms(Runnable dynamicSetBlock);

    /**
     * Runs the given block while the shader at the given index is active
     *
     * @param index           the shader index within the group
     * @param dynamicSetBlock a block in which dynamic name uniforms are set
     */
    @API(status = EXPERIMENTAL, since = "1.0.0")
    void setupDynamicUniforms(int index, Runnable dynamicSetBlock);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     * @param value       int value
     */
    @API(status = STABLE, since = "1.0.0")
    void setUniformValue(String uniformName, int value);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     * @param value0      int value
     * @param value1      int value
     */
    @API(status = STABLE, since = "1.0.0")
    void setUniformValue(String uniformName, int value0, int value1);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     * @param value0      int value
     * @param value1      int value
     * @param value2      int value
     */
    @API(status = STABLE, since = "1.0.0")
    void setUniformValue(String uniformName, int value0, int value1, int value2);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     * @param value0      int value
     * @param value1      int value
     * @param value2      int value
     * @param value3      int value
     */
    @API(status = STABLE, since = "1.0.0")
    void setUniformValue(String uniformName, int value0, int value1, int value2, int value3);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     * @param value       float value
     */
    @API(status = STABLE, since = "1.0.0")
    void setUniformValue(String uniformName, float value);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     * @param value0      float value
     * @param value1      float value
     */
    @API(status = STABLE, since = "1.0.0")
    void setUniformValue(String uniformName, float value0, float value1);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     * @param value0      float value
     * @param value1      float value
     * @param value2      float value
     */
    @API(status = STABLE, since = "1.0.0")
    void setUniformValue(String uniformName, float value0, float value1, float value2);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     * @param value0      float value
     * @param value1      float value
     * @param value2      float value
     * @param value3      float value
     */
    @API(status = STABLE, since = "1.0.0")
    void setUniformValue(String uniformName, float value0, float value1, float value2, float value3);

    /**
     * Sets the value of a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     * @param value       a matrix
     */
    @API(status = STABLE, since = "1.0.0")
    void setUniformValue(String uniformName, Matrix4f value);

    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param samplerName the name of the sampler uniform field in the shader source file and json
     * @param texture     a texture object
     */
    @API(status = STABLE, since = "1.0.0")
    void setSamplerUniform(String samplerName, AbstractTexture texture);

    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param samplerName the name of the sampler uniform field in the shader source file and json
     * @param textureFbo  a framebuffer which main texture will be used
     */
    @API(status = STABLE, since = "1.0.0")
    void setSamplerUniform(String samplerName, Framebuffer textureFbo);

    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param samplerName the name of the sampler uniform field in the shader source file and json
     * @param textureName an opengl texture name
     */
    @API(status = STABLE, since = "1.0.0")
    void setSamplerUniform(String samplerName, int textureName);

    /**
     * Disposes of this shader once it is no longer referenced.
     *
     * @see ShaderEffectManager#dispose(ManagedShaderEffect)
     * @see #release
     */
    void finalize();

}
