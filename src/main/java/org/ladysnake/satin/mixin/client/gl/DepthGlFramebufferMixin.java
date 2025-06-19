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
package org.ladysnake.satin.mixin.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.client.gl.Framebuffer;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.*;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(Framebuffer.class)
public abstract class DepthGlFramebufferMixin implements ReadableDepthFramebuffer {
    @Shadow @Final public boolean useDepthAttachment;

    @Shadow public int textureWidth;
    @Shadow public int textureHeight;

    @Shadow @Final protected String name;
    @Shadow @Nullable protected GpuTexture depthAttachment;
    private @Nullable GpuTexture satin$stillDepthTexture = null;

    @Inject(
            method = "initFbo",
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gl/Framebuffer;depthAttachment:Lcom/mojang/blaze3d/textures/GpuTexture;", shift = AFTER)
    )
    private void initFbo(int width, int height, CallbackInfo ci) {
        if (this.useDepthAttachment) {
            this.satin$stillDepthTexture = satin$setupDepthTexture();
        }
    }

    @Unique
    private GpuTexture satin$setupDepthTexture() {
        GpuTexture stillDepthTexture = RenderSystem.getDevice().createTexture(() -> this.name + " / SatinStillDepth", TextureFormat.DEPTH32, this.textureWidth, this.textureHeight, 1);
        stillDepthTexture.setTextureFilter(FilterMode.NEAREST, false);
        stillDepthTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        return stillDepthTexture;
    }

    @Inject(method = "delete", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gl/Framebuffer;depthAttachment:Lcom/mojang/blaze3d/textures/GpuTexture;"))
    private void delete(CallbackInfo ci) {
        if (this.satin$stillDepthTexture != null) {
            this.satin$stillDepthTexture.close();
            this.satin$stillDepthTexture = null;
        }
    }

    @Override
    public GpuTexture getStillDepthMap() {
        return this.satin$stillDepthTexture;
    }

    @Override
    public void freezeDepthMap() {
        RenderSystem.assertOnRenderThread();
        if (this.useDepthAttachment) {
            RenderSystem.getDevice()
                    .createCommandEncoder()
                    .copyTextureToTexture(this.satin$stillDepthTexture, this.depthAttachment, 0, 0, 0, 0, 0, this.textureWidth, this.textureHeight);
        }
    }
}
