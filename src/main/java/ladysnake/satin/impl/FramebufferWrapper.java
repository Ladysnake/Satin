/*
 * Satin
 * Copyright (C) 2019-2023 Ladysnake
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
import ladysnake.satin.api.managed.ManagedFramebuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.Window;

import javax.annotation.Nullable;

public final class FramebufferWrapper implements ManagedFramebuffer {
    private final RenderLayerSupplier renderLayerSupplier;
    private final String name;
    @Nullable
    private Framebuffer wrapped;

    FramebufferWrapper(String name) {
        this.name = name;
        this.renderLayerSupplier = RenderLayerSupplier.framebuffer(
                this.name + System.identityHashCode(this),
                () -> this.beginWrite(false),
                () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false)
        );
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
    public void beginWrite(boolean updateViewport) {
        if (this.wrapped != null) {
            this.wrapped.beginWrite(updateViewport);
        }
    }

    @Override
    public void draw() {
        Window window = MinecraftClient.getInstance().getWindow();
        this.draw(window.getFramebufferWidth(), window.getFramebufferHeight(), true);
    }

    @Override
    public void draw(int width, int height, boolean disableBlend) {
        if (this.wrapped != null) {
            this.wrapped.draw(width, height, disableBlend);
        }
    }

    @Override
    public void clear() {
        clear(MinecraftClient.IS_SYSTEM_MAC);
    }

    @Override
    public void clear(boolean swallowErrors) {
        if (this.wrapped != null) {
            this.wrapped.clear(swallowErrors);
        }
    }

    @Override
    public RenderLayer getRenderLayer(RenderLayer baseLayer) {
        return this.renderLayerSupplier.getRenderLayer(baseLayer);
    }
}
