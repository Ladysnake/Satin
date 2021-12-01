package ladysnake.satindepthtest;

import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satintestcore.item.SatinTestItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class SatinDepthTest implements ClientModInitializer {
    public static final String MOD_ID = "ladysnake/satindepthtest";
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(DepthFx.INSTANCE);
        ShaderEffectRenderCallback.EVENT.register(DepthFx.INSTANCE);
        PostWorldRenderCallback.EVENT.register(DepthFx.INSTANCE);
        SatinTestItems.DEBUG_ITEM.registerDebugCallback((world, player, hand) -> {
            if (world.isClient) {
                DepthFx.INSTANCE.testShader.release();
            }
        });
    }
}
