package ladysnake.satin.mixin.client.event;

import ladysnake.satin.api.event.EntitiesPostRenderCallback;
import ladysnake.satin.api.event.EntitiesPreRenderCallback;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "renderEntities", at = @At(value = "CONSTANT", args = "stringValue=entities"))
    private void firePreRenderEntities(Camera camera, VisibleRegion frustum, float tickDelta, CallbackInfo info) {
        EntitiesPreRenderCallback.EVENT.invoker().beforeEntitiesRender(camera, frustum, tickDelta);
    }

    @Inject(method = "renderEntities", at = @At(value = "CONSTANT", args = "stringValue=blockentities"))
    private void firePostRenderEntities(Camera camera, VisibleRegion frustum, float tickDelta, CallbackInfo info) {
        EntitiesPostRenderCallback.EVENT.invoker().onEntitiesRendered(camera, frustum, tickDelta);
    }

}
