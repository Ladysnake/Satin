package ladysnake.satinrenderlayer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.entity.passive.IronGolemEntity;

import javax.annotation.Nullable;

public class IllusionGolemEntityRenderer extends IronGolemEntityRenderer {
    public IllusionGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(IronGolemEntity entity, boolean showBody, boolean translucent, boolean glowing) {
        return SatinRenderLayer.getIllusion(super.getRenderLayer(entity, showBody, translucent, glowing));
    }
}
