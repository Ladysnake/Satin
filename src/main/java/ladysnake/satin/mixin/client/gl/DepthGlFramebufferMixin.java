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
package ladysnake.satin.mixin.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(Framebuffer.class)
public abstract class DepthGlFramebufferMixin implements ReadableDepthFramebuffer {
    @Shadow @Final public boolean useDepthAttachment;

    @Shadow public int textureWidth;
    @Shadow public int textureHeight;

    @Shadow public abstract void beginWrite(boolean boolean_1);

    private int satin$stillDepthTexture = -1;

    @Inject(
            method = "initFbo",
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gl/Framebuffer;depthAttachment:I", shift = AFTER)
    )
    private void initFbo(int width, int height, boolean flushErrors, CallbackInfo ci) {
        if (this.useDepthAttachment) {
            this.satin$stillDepthTexture = satin$setupDepthTexture();
        }
    }

    @Unique
    private int satin$setupDepthTexture() {
        int shadowMap = GL11.glGenTextures();
        RenderSystem.bindTexture(shadowMap);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, this.textureWidth, this.textureHeight, 0,GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, null);
        return shadowMap;
    }

    @Inject(method = "delete", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gl/Framebuffer;depthAttachment:I"))
    private void delete(CallbackInfo ci) {
        if (this.satin$stillDepthTexture > -1) {
            // delete texture
            TextureUtil.releaseTextureId(this.satin$stillDepthTexture);
            this.satin$stillDepthTexture = -1;
        }
    }

    @Override
    public int getStillDepthMap() {
        return this.satin$stillDepthTexture;
    }

    @Override
    public void freezeDepthMap() {
        if (this.useDepthAttachment) {
            this.beginWrite(false);
            RenderSystem.bindTexture(this.satin$stillDepthTexture);
            glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, this.textureWidth, this.textureHeight);
        }
    }
}
