/*
 * Satin
 * Copyright (C) 2019-2024 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package org.ladysnake.satin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apiguardian.api.API;
import org.ladysnake.satin.api.event.ResolutionChangeCallback;
import org.ladysnake.satin.api.event.WorldRendererReloadCallback;
import org.ladysnake.satin.impl.ReloadableShaderEffectManager;
import org.ladysnake.satin.impl.WorldRenderEventsDelegator;

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
        return false;
    }

    @Override
    public void onInitializeClient() {
        ResolutionChangeCallback.EVENT.register(ReloadableShaderEffectManager.INSTANCE);
        WorldRendererReloadCallback.EVENT.register(ReloadableShaderEffectManager.INSTANCE);
        if (FabricLoader.getInstance().isModLoaded("optifabric")) {
            LOGGER.warn("[Satin] Optifine present in the instance, custom entity post process shaders will not work");
        }
        if (FabricLoader.getInstance().isModLoaded("vivecraft")) {
            LOGGER.warn("[Satin] Vivecraft present in the instance, you may experience degraded performance - try turning eye stencil off in VR settings");
        }

        WorldRenderEvents.BEFORE_ENTITIES.register(WorldRenderEventsDelegator.INSTANCE);
        WorldRenderEvents.AFTER_ENTITIES.register(WorldRenderEventsDelegator.INSTANCE);
        WorldRenderEvents.LAST.register(WorldRenderEventsDelegator.INSTANCE);
    }
}
