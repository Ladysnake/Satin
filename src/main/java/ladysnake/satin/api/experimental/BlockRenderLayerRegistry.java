package ladysnake.satin.api.experimental;

import ladysnake.satin.impl.BlockRenderLayerRegistryImpl;
import net.minecraft.client.render.RenderLayer;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Allows for creation of custom RenderLayers for blocks.
 * */
@API(status = EXPERIMENTAL)
public interface BlockRenderLayerRegistry {
    static BlockRenderLayerRegistry getInstance() {
        return BlockRenderLayerRegistryImpl.INSTANCE;
    }
    
    /**
     * Register a custom RenderLayer for block rendering.
     *
     * This will fail if it is used too late.
     * */
    void registerRenderLayer(RenderLayer layer);
}
