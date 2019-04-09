package ladysnake.satintest;

import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.config.SatinFeatures;
import ladysnake.satintest.item.SatinTestItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

public class SatinTest implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "satintest";
    @Override
    public void onInitializeClient() {
        SatinFeatures.getInstance().readableDepthFramebuffers.use();
        ClientTickCallback.EVENT.register(DepthFx.INSTANCE);
        PostWorldRenderCallback.EVENT.register(DepthFx.INSTANCE);
    }

    @Override
    public void onInitialize() {
        SatinTestItems.init();
    }
}
