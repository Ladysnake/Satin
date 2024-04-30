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
        SatinTestItems.DEBUG_ITEM.registerDebugMode(MOD_ID, (world, player, hand) -> {
            if (world.isClient) {
                renderingBlit = !renderingBlit;
                color.set((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f);
            }
        });
    }
}

