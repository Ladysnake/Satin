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
package ladysnake.satin.mixin.client.event;

import ladysnake.satin.api.event.EntitiesPostRenderCallback;
import ladysnake.satin.api.event.EntitiesPreRenderCallback;
import ladysnake.satin.api.event.PostWorldRenderCallbackV2;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Unique
    private Frustum frustum;

    @ModifyVariable(
            method = "render",
            at = @At(value = "CONSTANT", args = "stringValue=entities", ordinal = 0, shift = At.Shift.BEFORE)
    )
    private Frustum captureFrustum(Frustum frustum) {
        this.frustum = frustum;
        return frustum;
    }

    @Inject(
            method = "render",
            at = @At(value = "CONSTANT", args = "stringValue=entities", ordinal = 0)
    )
    private void firePreRenderEntities(MatrixStack matrix, float tickDelta, long time, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lmTexManager, Matrix4f matrix4f, CallbackInfo ci) {
        EntitiesPreRenderCallback.EVENT.invoker().beforeEntitiesRender(camera, frustum, tickDelta);
    }

    @Inject(
            method = "render",
            at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0)
    )
    private void firePostRenderEntities(MatrixStack matrix, float tickDelta, long time, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lmTexManager, Matrix4f matrix4f, CallbackInfo ci) {
        EntitiesPostRenderCallback.EVENT.invoker().onEntitiesRendered(camera, frustum, tickDelta);
    }

    @Inject(
            method = "render",
            slice = @Slice(from = @At(value = "FIELD:LAST", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/render/WorldRenderer;transparencyShader:Lnet/minecraft/client/gl/ShaderEffect;")),
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/ShaderEffect;render(F)V"),
                    @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;depthMask(Z)V", ordinal = 1, shift = At.Shift.AFTER)
            }
    )
    private void hookPostWorldRender(MatrixStack matrices, float tickDelta, long nanoTime, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightmapTextureManager lmTexManager, Matrix4f matrix4f, CallbackInfo ci) {
        ((ReadableDepthFramebuffer) MinecraftClient.getInstance().getFramebuffer()).freezeDepthMap();
        PostWorldRenderCallbackV2.EVENT.invoker().onWorldRendered(matrices, camera, tickDelta, nanoTime);
    }
}
