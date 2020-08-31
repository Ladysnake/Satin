package ladysnake.satin.impl;

import java.util.HashSet;
import java.util.Set;
import ladysnake.satin.api.experimental.BlockRenderLayerRegistry;
import net.minecraft.client.render.RenderLayer;

public class BlockRenderLayerRegistryImpl implements BlockRenderLayerRegistry {
    public static final BlockRenderLayerRegistryImpl INSTANCE = new BlockRenderLayerRegistryImpl();
    
    private final Set<RenderLayer> renderLayers = new HashSet<>();
    private volatile boolean registryLocked = false;
    
    private BlockRenderLayerRegistryImpl(){}
    
    @Override
    public void registerRenderLayer(RenderLayer layer) {
        if(registryLocked){
            throw new IllegalStateException(String.format(
                "RenderLayer %s was added too late.",
                layer.toString()
            ));
        }
        
        renderLayers.add(layer);
    }
    
    public Set<RenderLayer> getLayers() {
        registryLocked = true;
        return renderLayers;
    }
}
