package ladysnake.satin.mixin.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayer.MultiPhaseParameters.class)
public interface MultiPhaseParametersAccessor {
    @Accessor
    RenderPhase.Texture getTexture();
    @Accessor
    RenderPhase.Transparency getTransparency();
    @Accessor
    RenderPhase.DiffuseLighting getDiffuseLighting();
    @Accessor
    RenderPhase.ShadeModel getShadeModel();
    @Accessor
    RenderPhase.Alpha getAlpha();
    @Accessor
    RenderPhase.DepthTest getDepthTest();
    @Accessor
    RenderPhase.Cull getCull();
    @Accessor
    RenderPhase.Lightmap getLightmap();
    @Accessor
    RenderPhase.Overlay getOverlay();
    @Accessor
    RenderPhase.Fog getFog();
    @Accessor
    RenderPhase.Layering getLayering();
    @Accessor
    RenderPhase.Target getTarget();
    @Accessor
    RenderPhase.Texturing getTexturing();
    @Accessor
    RenderPhase.WriteMaskState getWriteMaskState();
    @Accessor
    RenderPhase.LineWidth getLineWidth();
}
