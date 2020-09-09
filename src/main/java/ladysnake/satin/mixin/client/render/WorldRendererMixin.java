package ladysnake.satin.mixin.client.render;

import ladysnake.satin.impl.BlockRenderLayerRegistryImpl;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloadListener;
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
public abstract class WorldRendererMixin implements SynchronousResourceReloadListener, AutoCloseable {
    @Shadow protected abstract void renderLayer(RenderLayer layer, MatrixStack matrix, double x, double y, double z);
    
    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDD)V",
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
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void renderCustom(
        MatrixStack matrix, float delta, long time, boolean renderOutlines, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightManager, Matrix4f frustumMatrix,
        CallbackInfo info,
        Profiler profiler, Vec3d vec3d, double x, double y, double z
    ) {
        // Render all the custom ones
        for(RenderLayer layer : BlockRenderLayerRegistryImpl.INSTANCE.getLayers()) {
            renderLayer(layer, matrix, x, y, z);
        }
    }
}
