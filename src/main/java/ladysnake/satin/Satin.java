package ladysnake.satin;

import com.mojang.blaze3d.platform.GLX;
import ladysnake.satin.api.event.ResolutionChangeCallback;
import ladysnake.satin.impl.FbReloadFix;
import ladysnake.satin.impl.ReloadableShaderEffectManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.STABLE;

public class Satin implements ClientModInitializer {
    public static final String MOD_ID = "satin";
    public static final Logger LOGGER = LogManager.getLogger("Satin");

    /**
     * Checks if OpenGL shaders are disabled in the current game instance.
     * Currently, this only checks if the hardware supports them, however
     * in the future it may check a client option as well.
     */
    @API(status = STABLE)
    public static boolean areShadersDisabled() {
        return !GLX.usePostProcess;
    }

    @Override
    public void onInitializeClient() {
        FbReloadFix.init();
        // Subscribe the shader manager to the resolution change callback to reload shaders when needed
        ResolutionChangeCallback.EVENT.register(ReloadableShaderEffectManager.INSTANCE);
        // Subscribe the shader manager to MinecraftClient's resource manager to reload shaders like normal assets.
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ReloadableShaderEffectManager.INSTANCE);
        if (FabricLoader.getInstance().isModLoaded("optifabric")) {
            LOGGER.warn("[Satin] Optifine present in the instance, custom entity post process shaders will not work");
        }
    }

}
