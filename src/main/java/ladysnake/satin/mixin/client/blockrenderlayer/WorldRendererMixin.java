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
package ladysnake.satin.mixin.client.blockrenderlayer;

import ladysnake.satin.impl.BlockRenderLayerRegistry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow protected abstract void renderLayer(RenderLayer layer, MatrixStack matrix, double x, double y, double z, Matrix4f frustumMatrix);
    
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDDLnet/minecraft/util/math/Matrix4f;)V",
            ordinal = 2,
            shift = At.Shift.AFTER
        ),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/WorldRenderer;updateChunks(J)V"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/SkyProperties;isDarkened()Z"
            )
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void renderCustom(
        MatrixStack matrix, float delta, long time, boolean renderOutlines, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightManager, Matrix4f frustumMatrix,
        CallbackInfo info,
        Profiler profiler, Vec3d vec3d, double x, double y, double z
    ) {
        // Render all the custom ones
        for(RenderLayer layer : BlockRenderLayerRegistry.INSTANCE.getLayers()) {
            renderLayer(layer, matrix, x, y, z, frustumMatrix);
        }
    }
}
