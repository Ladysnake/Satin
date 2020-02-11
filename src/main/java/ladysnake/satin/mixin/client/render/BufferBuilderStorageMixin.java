package ladysnake.satin.mixin.client.render;

import ladysnake.satin.api.event.BufferBuildersInitCallback;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedMap;

@Mixin(BufferBuilderStorage.class)
public abstract class BufferBuilderStorageMixin {

    @Shadow @Final private SortedMap<RenderLayer, BufferBuilder> entityBuilders;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fireInitEvent(CallbackInfo ci) {
        BufferBuildersInitCallback.EVENT.invoker().initBufferBuilders((BufferBuilderStorage) (Object) this, this.entityBuilders);
    }

}
