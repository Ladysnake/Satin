package ladysnake.satin.impl;

import ladysnake.satin.mixin.client.render.RenderPhaseAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

import java.util.HashMap;
import java.util.Map;

public class RenderLayerSupplier {
    private final RenderPhase.Target target;
    private final Map<RenderLayer, RenderLayer> renderLayerCache = new HashMap<>();
    private final String uniqueName;

    public RenderLayerSupplier(String name, Runnable setupState, Runnable cleanupState) {
        this.uniqueName = name;
        this.target = new RenderPhase.Target(
                uniqueName + "_target",
                setupState,
                cleanupState
        );
    }

    public RenderLayer getRenderLayer(RenderLayer baseLayer) {
        RenderLayer existing = this.renderLayerCache.get(baseLayer);
        if (existing != null) {
            return existing;
        }
        String newName = ((RenderPhaseAccessor) baseLayer).getName() + "_" + this.uniqueName;
        RenderLayer newLayer = RenderLayerDuplicator.copy(baseLayer, newName, builder -> builder.target(this.target));
        this.renderLayerCache.put(baseLayer, newLayer);
        return newLayer;
    }
}
