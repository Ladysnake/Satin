package ladysnake.satinrenderlayer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.entity.passive.IronGolemEntity;

import javax.annotation.Nullable;

public class IllusionGolemEntityRenderer extends IronGolemEntityRenderer {
    public IllusionGolemEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(IronGolemEntity entity, boolean showBody, boolean translucent, boolean glowing) {
        RenderLayer baseLayer = super.getRenderLayer(entity, showBody, translucent, glowing);
        return baseLayer == null ? null : SatinRenderLayer.illusionBuffer.getRenderLayer(baseLayer);
    }
}
