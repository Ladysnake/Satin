package ladysnake.satin.impl;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.Satin;
import ladysnake.satin.api.experimental.managed.*;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.util.ShaderPrograms;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.JsonGlProgram;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.apiguardian.api.API;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.lwjgl.opengl.GL11.*;

/**
 * A {@link ManagedShaderEffect} that can be {@link #setup} several times in its lifetime,
 * triggering an initialization callback.
 *
 * @see ReloadableShaderEffectManager
 * @see ManagedShaderEffect
 * @since 1.0.0
 */
public final class ResettableManagedShaderEffect implements ManagedShaderEffect {

    /**Location of the shader json definition file*/
    private final Identifier location;
    /**Callback to run once each time the shader effect is initialized*/
    private final Consumer<ManagedShaderEffect> initCallback;
    @CheckForNull
    private ShaderEffect shaderGroup;
    private boolean errored;
    private final Map<String, ManagedUniform> managedUniforms = new HashMap<>();

    /**
     * Creates a new shader effect. <br>
     * <b>Users should obtain instanced of this class through {@link ShaderEffectManager}</b>
     *
     * @param location         the location of a shader effect JSON definition file
     * @param initCallback code to run in {@link #setup(int, int)}
     * @see ReloadableShaderEffectManager#manage(Identifier)
     * @see ReloadableShaderEffectManager#manage(Identifier, Consumer)
     */
    @API(status = INTERNAL)
    public ResettableManagedShaderEffect(Identifier location, Consumer<ManagedShaderEffect> initCallback) {
        this.location = location;
        this.initCallback = initCallback;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public ShaderEffect getShaderEffect() {
        if (!this.isInitialized() && !this.errored) {
            try {
                this.initialize();
            } catch (Exception e) {
                Satin.LOGGER.error("[Satin] Could not create screen shader {}", location, e);
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
        this.setup(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
    }

    @API(status = INTERNAL)
    public void setup(int windowWidth, int windowHeight) {
        Preconditions.checkNotNull(shaderGroup);
        this.shaderGroup.setupDimensions(windowWidth, windowHeight);
        for (ManagedUniform uniform : this.managedUniforms.values()) {
            setupUniform(uniform, shaderGroup);
        }
        this.initCallback.accept(this);
    }

    private void setupUniform(ManagedUniform uniform, ShaderEffect shaderEffect) {
        int found = uniform.findUniformTargets(((AccessiblePassesShaderEffect) shaderEffect).satin$getPasses());
        if (found == 0) {
            Satin.LOGGER.warn("[Satin] No uniform found with name {} in shader {}", uniform.getName(), this.location);
        }
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

    public void setErrored(boolean error) {
        this.errored = error;
    }

    public Identifier getLocation() {
        return location;
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
            RenderSystem.matrixMode(GL_TEXTURE);
            RenderSystem.loadIdentity();
            sg.render(tickDelta);
            MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // restore blending
            RenderSystem.enableDepthTest();
            RenderSystem.matrixMode(GL_MODELVIEW);
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
            ShaderPrograms.useShader(sm.getProgramRef());
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
    public void setSamplerUniform(String samplerName, AbstractTexture texture) {
        setSamplerUniform(samplerName, (Object) texture);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSamplerUniform(String samplerName, Framebuffer textureFbo) {
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

    private ManagedUniform manageUniform(String uniformName) {
        return this.managedUniforms.computeIfAbsent(uniformName, name -> {
            ManagedUniform ret = new ManagedUniform(name);
            if (this.shaderGroup != null) {
                setupUniform(ret, shaderGroup);
            }
            return ret;
        });
    }

    @Override
    public Uniform1i findUniform1i(String uniformName) {
        return manageUniform(uniformName);
    }

    @Override
    public Uniform2i findUniform2i(String uniformName) {
        return manageUniform(uniformName);
    }

    @Override
    public Uniform3i findUniform3i(String uniformName) {
        return manageUniform(uniformName);
    }

    @Override
    public Uniform4i findUniform4i(String uniformName) {
        return manageUniform(uniformName);
    }

    @Override
    public Uniform1f findUniform1f(String uniformName) {
        return manageUniform(uniformName);
    }

    @Override
    public Uniform2f findUniform2f(String uniformName) {
        return manageUniform(uniformName);
    }

    @Override
    public Uniform3f findUniform3f(String uniformName) {
        return manageUniform(uniformName);
    }

    @Override
    public Uniform4f findUniform4f(String uniformName) {
        return manageUniform(uniformName);
    }

    @Override
    public UniformMat4 findUniformMat4(String uniformName) {
        return manageUniform(uniformName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finalize() {
        ShaderEffectManager.getInstance().dispose(this);
    }

}