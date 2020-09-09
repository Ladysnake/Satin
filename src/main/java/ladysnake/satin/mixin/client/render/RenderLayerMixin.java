package ladysnake.satin.mixin.client.render;

import com.google.common.collect.ImmutableList;
import ladysnake.satin.impl.BlockRenderLayerRegistryImpl;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin extends RenderPhase {
    private RenderLayerMixin() {
        super(null, null, null);
    }
    
    @Inject(
        method = "getBlockLayers",
        at = @At("RETURN"),
        cancellable = true
    )
    private static void getBlockLayers(CallbackInfoReturnable<ImmutableList<Object>> info) {
        info.setReturnValue(
            ImmutableList.builder()
            .addAll(info.getReturnValue())
            .addAll(BlockRenderLayerRegistryImpl.INSTANCE.getLayers()
        ).build());
    }
}
