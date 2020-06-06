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

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.Satin;
import ladysnake.satin.api.managed.ManagedFramebuffer;
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
public final class ResettableManagedShaderEffect extends ResettableManagedShaderBase<ShaderEffect> implements ManagedShaderEffect {

    /**Callback to run once each time the shader effect is initialized*/
    private final Consumer<ManagedShaderEffect> initCallback;
    private final Map<String, FramebufferWrapper> managedTargets;

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
        super(location);
        this.initCallback = initCallback;
        this.managedTargets = new HashMap<>();
    }

    @Nullable
    @Override
    public ShaderEffect getShaderEffect() {
        return getShaderOrLog();
    }

    @Override
    protected ShaderEffect parseShader(MinecraftClient mc, Identifier location) throws IOException {
        return new ShaderEffect(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), location);
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();
        for (FramebufferWrapper buf : this.managedTargets.values()) {
            buf.findTarget(this.shader);
        }
    }

    @Override
    public void setup(int windowWidth, int windowHeight) {
        Preconditions.checkNotNull(shader);
        this.shader.setupDimensions(windowWidth, windowHeight);
        for (ManagedUniformBase uniform : this.getManagedUniforms()) {
            setupUniform(uniform, shader);
        }
        this.initCallback.accept(this);
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

    @Override
    public ManagedFramebuffer getTarget(String name) {
        return this.managedTargets.computeIfAbsent(name, n -> {
            FramebufferWrapper ret = new FramebufferWrapper(n);
            ret.findTarget(this.getShaderEffect());
            return ret;
        });
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
    public void setSamplerUniform(String samplerName, int textureName) {
        setSamplerUniform(samplerName, Integer.valueOf(textureName));
    }

    public void setupDynamicUniforms(Runnable dynamicSetBlock) {
        this.setupDynamicUniforms(0, dynamicSetBlock);
    }

    public void setupDynamicUniforms(int index, Runnable dynamicSetBlock) {
        AccessiblePassesShaderEffect sg = (AccessiblePassesShaderEffect) this.getShaderEffect();
        if (sg != null) {
            JsonGlProgram sm = sg.satin$getPasses().get(index).getProgram();
            ShaderPrograms.useShader(sm.getProgramRef());
            dynamicSetBlock.run();
            ShaderPrograms.useShader(0);
        }
    }

    @Override
    protected boolean setupUniform(ManagedUniformBase uniform, ShaderEffect shader) {
        return uniform.findUniformTargets(((AccessiblePassesShaderEffect) shader).satin$getPasses());
    }

    @Override
    protected void logInitError(IOException e) {
        Satin.LOGGER.error("Could not create screen shader {}", this.getLocation(), e);
    }
}