package ladysnake.satindepthtest;

import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satintestcore.item.SatinTestItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

public class SatinDepthTest implements ClientModInitializer {
    public static final String MOD_ID = "satindepthtest";
    @Override
    public void onInitializeClient() {
        ReadableDepthFramebuffer.useFeature();
        ClientTickCallback.EVENT.register(DepthFx.INSTANCE);
        PostWorldRenderCallback.EVENT.register(DepthFx.INSTANCE);
        SatinTestItems.DEBUG_ITEM.registerDebugCallback((world, player, hand) -> {
            if (world.isClient) {
                DepthFx.INSTANCE.testShader.release();
            }
        });
    }

}
