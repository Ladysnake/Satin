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
package org.ladysnake.satin.mixin.client.blockrenderlayer;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Handle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.ladysnake.satin.impl.BlockRenderLayerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow protected abstract void renderLayer(RenderLayer renderLayer, double x, double y, double z, Matrix4f viewMatrix, Matrix4f positionMatrix);

    //TODO
    @Inject(
        method = "method_62214", // injecting to the lambda
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            ordinal = 2,
            shift = At.Shift.AFTER
        )
    )
    private void renderCustom(
            Fog fog,
            RenderTickCounter renderTickCounter,
            Camera camera,
            Profiler profiler,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            Handle<Framebuffer> itemEntityFramebuffer,
            Handle<Framebuffer> mainFramebuffer,
            Handle<Framebuffer> weatherFramebuffer,
            Handle<Framebuffer> entityOutlineFramebuffer,
            boolean renderBlockOutline,
            Frustum frustum,
            Handle<Framebuffer> translucentFramebuffer,
            CallbackInfo ci
    ) {
        // Render all the custom ones
        for(RenderLayer layer : BlockRenderLayerRegistry.INSTANCE.getLayers()) {
            // I think yarn might have positionMatrix backwards here?
            renderLayer(layer, camera.getPos().x, camera.getPos().y, camera.getPos().z, positionMatrix, projectionMatrix);
        }
    }


}
