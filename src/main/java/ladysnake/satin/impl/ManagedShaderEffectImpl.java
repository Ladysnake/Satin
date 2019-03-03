package ladysnake.satin.impl;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.GlStateManager;
import ladysnake.satin.Satin;
import ladysnake.satin.api.program.ShaderPrograms;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.gl.JsonGlProgram;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.Identifier;
import org.apiguardian.api.API;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * A post processing shader that is applied to the main framebuffer
 * <p>
 * Post shaders loaded through {@link ReloadableShaderEffectManager#manage(Identifier, Consumer)} are self-managed and will be
 * reloaded when shader assets are reloaded (through <tt>F3-T</tt> or <tt>/ladylib_shader_reload</tt>) or the
 * screen resolution changes.
 * <p>
 *
 * @see ReloadableShaderEffectManager
 * @see "<tt>assets/minecraft/shaders</tt> for examples"
 * @since 1.0.0
 */
public final class ManagedShaderEffectImpl implements ManagedShaderEffect {

    /**Location of the shader json definition file*/
    private final Identifier location;
    /**Callback to run once each time the shader effect is initialized*/
    private final Consumer<ManagedShaderEffect> uniformInitCallback;
    @CheckForNull
    private ShaderEffect shaderGroup;
    private boolean errored;

    /**
     * Creates a new shader effect. <br>
     * <b>Users should obtain instanced of this class through {@link ShaderEffectManager}</b>
     *
     * @param location         the location of a shader effect JSON definition file
     * @param uniformInitCallback code to run in {@link #setup(int, int)}
     * @see ReloadableShaderEffectManager#manage(Identifier)
     * @see ReloadableShaderEffectManager#manage(Identifier, Consumer)
     */
    @API(status = INTERNAL)
    public ManagedShaderEffectImpl(Identifier location, Consumer<ManagedShaderEffect> uniformInitCallback) {
        this.location = location;
        this.uniformInitCallback = uniformInitCallback;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public ShaderEffect getShaderEffect() {
        if (!this.isInitialized() && !this.errored) {
            try {
                initialize();
            } catch (Exception e) {
                Satin.LOGGER.error("Could not create screen shader {}", location, e);
                this.errored = true;
            }
        }
        return this.shaderGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws IOException {
        this.release();
        MinecraftClient mc = MinecraftClient.getInstance();
        this.shaderGroup = new ShaderEffect(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), this.location);
        this.setup(mc.window.getWidth(), mc.window.getHeight());
    }

    @API(status = INTERNAL)
    public void setup(int windowWidth, int windowHeight) {
        Preconditions.checkNotNull(shaderGroup);
        this.shaderGroup.setupDimensions(windowWidth, windowHeight);
        this.uniformInitCallback.accept(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInitialized() {
        return this.shaderGroup != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErrored() {
        return this.errored;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {
        if (this.isInitialized()) {
            this.shaderGroup.close();
            this.shaderGroup = null;
        }
        this.errored = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(float tickDelta) {
        ShaderEffect sg = this.getShaderEffect();
        if (sg != null) {
            GlStateManager.matrixMode(GL_TEXTURE);
            GlStateManager.loadIdentity();
            sg.render(tickDelta);
            MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
            GlStateManager.disableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // restore blending
            GlStateManager.enableDepthTest();
            GlStateManager.matrixMode(GL_MODELVIEW);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupDynamicUniforms(Runnable dynamicSetBlock) {
        this.setupDynamicUniforms(0, dynamicSetBlock);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupDynamicUniforms(int index, Runnable dynamicSetBlock) {
        AccessiblePassesShaderEffect sg = (AccessiblePassesShaderEffect) this.getShaderEffect();
        if (sg != null) {
            JsonGlProgram sm = sg.satin$getPasses().get(index).getProgram();
            // method_1270 == getProgramRef
            ShaderPrograms.useShader(sm.method_1270());
            dynamicSetBlock.run();
            ShaderPrograms.useShader(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUniformValue(String uniformName, int value) {
        setUniformValue(uniformName, value, 0, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUniformValue(String uniformName, int value0, int value1) {
        setUniformValue(uniformName, value0, value1, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUniformValue(String uniformName, int value0, int value1, int value2) {
        setUniformValue(uniformName, value0, value1, value2, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUniformValue(String uniformName, int value0, int value1, int value2, int value3) {
        AccessiblePassesShaderEffect sg = (AccessiblePassesShaderEffect) this.getShaderEffect();
        if (sg != null) {
            for (PostProcessShader shader : sg.satin$getPasses()) {
                shader.getProgram().getUniformByNameOrDummy(uniformName).set(value0, value1, value2, value3);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUniformValue(String uniformName, float value) {
        AccessiblePassesShaderEffect sg = (AccessiblePassesShaderEffect) this.getShaderEffect();
        if (sg != null) {
            for (PostProcessShader shader : sg.satin$getPasses()) {
                shader.getProgram().getUniformByNameOrDummy(uniformName).set(value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUniformValue(String uniformName, float value0, float value1) {
        AccessiblePassesShaderEffect sg = (AccessiblePassesShaderEffect) this.getShaderEffect();
        if (sg != null) {
            for (PostProcessShader shader : sg.satin$getPasses()) {
                shader.getProgram().getUniformByNameOrDummy(uniformName).set(value0, value1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUniformValue(String uniformName, float value0, float value1, float value2) {
        AccessiblePassesShaderEffect sg = (AccessiblePassesShaderEffect) this.getShaderEffect();
        if (sg != null) {
            for (PostProcessShader shader : sg.satin$getPasses()) {
                shader.getProgram().getUniformByNameOrDummy(uniformName).set(value0, value1, value2);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUniformValue(String uniformName, float value0, float value1, float value2, float value3) {
        AccessiblePassesShaderEffect sg = (AccessiblePassesShaderEffect) this.getShaderEffect();
        if (sg != null) {
            for (PostProcessShader shader : sg.satin$getPasses()) {
                shader.getProgram().getUniformByNameOrDummy(uniformName).set(value0, value1, value2, value3);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUniformValue(String uniformName, Matrix4f value) {
        AccessiblePassesShaderEffect sg = (AccessiblePassesShaderEffect) this.getShaderEffect();
        if (sg != null) {
            for (PostProcessShader shader : sg.satin$getPasses()) {
                shader.getProgram().getUniformByNameOrDummy(uniformName).set(value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSamplerUniform(String samplerName, Texture texture) {
        setSamplerUniform(samplerName, (Object) texture);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSamplerUniform(String samplerName, GlFramebuffer textureFbo) {
        setSamplerUniform(samplerName, (Object) textureFbo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSamplerUniform(String samplerName, int textureName) {
        setSamplerUniform(samplerName, Integer.valueOf(textureName));
    }

    private void setSamplerUniform(String samplerName, Object texture) {
        AccessiblePassesShaderEffect sg = (AccessiblePassesShaderEffect) this.getShaderEffect();
        if (sg != null) {
            for (PostProcessShader shader : sg.satin$getPasses()) {
                shader.getProgram().bindSampler(samplerName, texture);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finalize() {
        ShaderEffectManager.getInstance().dispose(this);
    }

}