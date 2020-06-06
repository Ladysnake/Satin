package ladysnake.satinbasictest;

import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform4f;
import ladysnake.satintestcore.item.SatinTestItems;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

public final class SatinBasicTest implements ClientModInitializer {
    public static final String MOD_ID = "satinbasictest";

    private boolean renderingBlit = false;
    // literally the same as minecraft's blit, we are just checking that custom paths work
    private final ManagedShaderEffect testShader = ShaderEffectManager.getInstance().manage(new Identifier(MOD_ID, "shaders/post/blit.json"));
    private final Uniform4f color = testShader.findUniform4f("ColorModulate");

    @Override
    public void onInitializeClient() {
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
            if (renderingBlit) {
                testShader.render(tickDelta);
            }
        });
        SatinTestItems.DEBUG_ITEM.registerDebugCallback((world, player, hand) -> {
            if (world.isClient) {
                renderingBlit = !renderingBlit;
                color.set((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f);
            }
        });
    }
}

