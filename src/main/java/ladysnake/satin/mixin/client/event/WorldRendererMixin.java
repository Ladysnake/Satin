package ladysnake.satin.mixin.client.event;

import ladysnake.satin.api.event.EntitiesPostRenderCallback;
import net.minecraft.class_4184;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "renderEntities", at = @At(value = "CONSTANT", args = "stringValue=blockentities"))
    private void firePostRenderEntities(class_4184 camera, VisibleRegion frustum, float tickDelta, CallbackInfo info) {
        EntitiesPostRenderCallback.EVENT.invoker().onEntitiesRendered(camera.method_19331(), frustum, tickDelta);
    }

}
