package ladysnake.satindepthtest;

import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satindepthtest.item.SatinTestItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

public class Main implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "satindepthtest";
    @Override
    public void onInitializeClient() {
        ReadableDepthFramebuffer.useFeature();
        ClientTickCallback.EVENT.register(DepthFx.INSTANCE);
        PostWorldRenderCallback.EVENT.register(DepthFx.INSTANCE);
    }

    @Override
    public void onInitialize() {
        SatinTestItems.init();
    }
}
