package ladysnake.satin.impl;

import ladysnake.satin.Satin;
import ladysnake.satin.api.managed.ManagedFramebuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.util.Window;

import javax.annotation.Nullable;

public final class FramebufferWrapper implements ManagedFramebuffer {
    private final String name;
    @Nullable
    private Framebuffer wrapped;

    FramebufferWrapper(String name) {
        this.name = name;
    }

    void findTarget(@Nullable ShaderEffect shaderEffect) {
        if (shaderEffect == null) {
            this.wrapped = null;
        } else {
            this.wrapped = shaderEffect.getSecondaryTarget(this.name);
            if (this.wrapped == null) {
                Satin.LOGGER.warn("No target framebuffer found with name {} in shader {}", this.name, shaderEffect.getName());
            }
        }
    }

    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public Framebuffer getFramebuffer() {
        return wrapped;
    }

    @Override
    public void copyDepthFrom(Framebuffer buffer) {
        if (this.wrapped != null) {
            this.wrapped.copyDepthFrom(buffer);
        }
    }

    @Override
    public void draw() {
        Window window = MinecraftClient.getInstance().getWindow();
        this.draw(window.getFramebufferWidth(), window.getFramebufferHeight());
    }

    @Override
    public void draw(int width, int height) {
        if (this.wrapped != null) {
            this.wrapped.draw(width, height);
        }
    }
}
