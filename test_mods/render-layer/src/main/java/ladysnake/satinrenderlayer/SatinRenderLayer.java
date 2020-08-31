package ladysnake.satinrenderlayer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.api.event.EntitiesPreRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.experimental.BlockRenderLayerRegistry;
import ladysnake.satin.api.managed.ManagedFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ManagedShaderProgram;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import ladysnake.satintestcore.block.SatinTestBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public final class SatinRenderLayer {

    /* * * * ManagedShaderEffect-based RenderLayer entity rendering * * * */

    public static final EntityType<IronGolemEntity> ILLUSION_GOLEM =
            Registry.register(
                    Registry.ENTITY_TYPE,
                    new Identifier("satinrenderlayer", "illusion_golem"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, IronGolemEntity::new).dimensions(EntityType.IRON_GOLEM.getDimensions()).build()
            );

    public static final ManagedShaderEffect illusionEffect = ShaderEffectManager.getInstance().manage(new Identifier("satinrenderlayer", "shaders/post/illusion.json"),
            effect -> effect.setUniformValue("ColorModulate", 1.2f, 0.7f, 0.2f, 1.0f));
    public static final ManagedFramebuffer illusionBuffer = illusionEffect.getTarget("final");

    /* * * * ManagedShaderProgram-based RenderLayer entity rendering * * * */

    public static final EntityType<WitherEntity> RAINBOW_WITHER =
            Registry.register(
                    Registry.ENTITY_TYPE,
                    new Identifier("satinrenderlayer", "rainbow_wither"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, (EntityType<WitherEntity> entityType, World world) -> {
                        WitherEntity witherEntity = new WitherEntity(entityType, world);
                        witherEntity.setAiDisabled(true);
                        return witherEntity;
                    }).dimensions(EntityType.WITHER.getDimensions()).build()
            );

    public static final ManagedShaderProgram rainbow = ShaderEffectManager.getInstance().manageProgram(new Identifier("satinrenderlayer", "rainbow"));
    private static final Uniform1f uniformSTime = rainbow.findUniform1f("STime");

    private static int ticks;

    public static void onInitializeClient() {
        RenderLayer blockRenderLayer = illusionBuffer.getRenderLayer(RenderLayer.getTranslucent());
        BlockRenderLayerRegistry.getInstance().registerRenderLayer(blockRenderLayer);
        BlockRenderLayerMap.INSTANCE.putBlock(SatinTestBlocks.DEBUG_BLOCK, blockRenderLayer);
        FabricDefaultAttributeRegistry.register(ILLUSION_GOLEM, IronGolemEntity.createIronGolemAttributes());
        FabricDefaultAttributeRegistry.register(RAINBOW_WITHER, WitherEntity.createWitherAttributes());
        EntityRendererRegistry.INSTANCE.register(ILLUSION_GOLEM, (dispatcher, ctx) -> new IllusionGolemEntityRenderer(dispatcher));
        EntityRendererRegistry.INSTANCE.register(RAINBOW_WITHER, (dispatcher, ctx) -> new RainbowWitherEntityRenderer(dispatcher));
        ClientTickCallback.EVENT.register(client -> ticks++);
        EntitiesPreRenderCallback.EVENT.register((camera, frustum, tickDelta) -> uniformSTime.set((ticks + tickDelta) * 0.05f));
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    illusionEffect.render(tickDelta);
                    client.getFramebuffer().beginWrite(true);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                    illusionBuffer.draw(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), false);
                    illusionBuffer.clear();
                    client.getFramebuffer().beginWrite(true);
                    RenderSystem.disableBlend();
                }
        );
    }
}
