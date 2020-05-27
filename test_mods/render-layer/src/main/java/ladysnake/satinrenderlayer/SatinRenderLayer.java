package ladysnake.satinrenderlayer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class SatinRenderLayer extends RenderLayer {
    public static final EntityType<IronGolemEntity> ILLUSION_GOLEM =
            Registry.register(
                    Registry.ENTITY_TYPE,
                    new Identifier("satinrenderlayer", "illusion_golem"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, IronGolemEntity::new).dimensions(EntityType.IRON_GOLEM.getDimensions()).build()
            );
    public static Framebuffer illusionBuffer;
    public static final ManagedShaderEffect illusionEffect = ShaderEffectManager.getInstance().manage(new Identifier("satinrenderlayer", "shaders/post/illusion.json"),
            effect -> {
                effect.setUniformValue("ColorModulate", 1.2f, 0.7f, 0.2f, 1.0f);
                illusionBuffer = Objects.requireNonNull(effect.getShaderEffect()).getSecondaryTarget("final");
            });
    private static final Target illusionTarget = new Target("satin:outline_target", () -> {
        illusionBuffer.beginWrite(false);
    }, () -> {
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
    });
    private static final Map<Identifier, RenderLayer> renderLayers = new HashMap<>();

    public static RenderLayer getIllusion(Identifier texture) {
        return renderLayers.computeIfAbsent(texture, tex -> RenderLayer.of(
                "satin:illusion",
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                GL11.GL_QUADS,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .texture(new RenderPhase.Texture(tex, false, false))
                        .cull(ENABLE_CULLING)
                        .depthTest(LEQUAL_DEPTH_TEST)
                        .alpha(ONE_TENTH_ALPHA)
                        .texturing(DEFAULT_TEXTURING)
                        .lightmap(ENABLE_LIGHTMAP)
                        .diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
                        .fog(NO_FOG)
                        .target(illusionTarget)
                        .build(true)
        ));
    }

    public static void onInitializeClient() {
//        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.GRASS_BLOCK, getIllusion(new Identifier("textures/block/grass_block_top.png")));
        FabricDefaultAttributeRegistry.register(ILLUSION_GOLEM, IronGolemEntity.createIronGolemAttributes());
        EntityRendererRegistry.INSTANCE.register(ILLUSION_GOLEM, (dispatcher, ctx) -> new IllusionGolemEntityRenderer(dispatcher));
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (illusionEffect != null) {
                        illusionEffect.render(tickDelta);
                        client.getFramebuffer().beginWrite(true);
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                        illusionBuffer.draw(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), false);
                        illusionBuffer.clear(false);
                        client.getFramebuffer().beginWrite(true);
                        RenderSystem.disableBlend();
                    }
                }
        );
    }

    private SatinRenderLayer(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }


}
