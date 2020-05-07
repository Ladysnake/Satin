package ladysnake.satin.mixin.client.event;

import ladysnake.satin.api.event.EntitiesPostRenderCallback;
import ladysnake.satin.api.event.EntitiesPreRenderCallback;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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

}
