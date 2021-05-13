package ladysnake.satin.mixin.client.render;

import ladysnake.satin.impl.RenderLayerDuplicator;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(RenderLayer.MultiPhase.class)
public abstract class RenderLayerMultiPhaseMixin extends RenderLayer implements RenderLayerDuplicator.SatinRenderLayer {
    @Shadow
    @Final
    private MultiPhaseParameters phases;

    public RenderLayerMultiPhaseMixin(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    @Override
    public RenderLayer satin$copy(String newName, Consumer<MultiPhaseParameters.Builder> op) {
        return RenderLayerAccessor.satin$of(
                newName,
                this.getVertexFormat(),
                this.getDrawMode(),
                this.getExpectedBufferSize(),
                this.hasCrumbling(),
                ((RenderLayerAccessor) this).isTranslucent(),
                this.satin$copyPhaseParameters(op)
        );
    }

    @Override
    public MultiPhaseParameters satin$copyPhaseParameters(Consumer<MultiPhaseParameters.Builder> op) {
        RenderLayerMixin.MultiPhaseParametersAccessor access = ((RenderLayerMixin.MultiPhaseParametersAccessor) (Object) this.phases);
        MultiPhaseParameters.Builder builder = MultiPhaseParameters.builder()
                .texture(access.getTexture())
                .shader(access.getShader())
                .transparency(access.getTransparency())
                .depthTest(access.getDepthTest())
                .cull(access.getCull())
                .lightmap(access.getLightmap())
                .overlay(access.getOverlay())
                .layering(access.getLayering())
                .target(access.getTarget())
                .texturing(access.getTexturing())
                .writeMaskState(access.getWriteMaskState())
                .lineWidth(access.getLineWidth());
        op.accept(builder);
        return builder.build(access.getOutlineMode());
    }
}
