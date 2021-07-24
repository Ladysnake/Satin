/*
 * Satin
 * Copyright (C) 2019-2021 Ladysnake
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
package ladysnake.satin.mixin;

import ladysnake.satin.Satin;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class SatinMixinPlugin implements IMixinConfigPlugin {
    private static final boolean ALLOW_RENDER_LAYER_MIXINS;
    private static final boolean ENABLE_SHADER_EFFECT_LOCATION_MIXIN;

    static {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("canvas")) {
            Satin.LOGGER.warn("[Satin] Canvas is present, custom block renders will not work");
            ALLOW_RENDER_LAYER_MIXINS = false;
        } else {
            if (loader.isModLoaded("sodium")) {
                Satin.LOGGER.warn("[Satin] Sodium is present, custom block renders may not work");
            }
            ALLOW_RENDER_LAYER_MIXINS = true;
        }
        // Architectury actually does the same change, and they can't exactly depend on Satin so...
        ENABLE_SHADER_EFFECT_LOCATION_MIXIN = !loader.isModLoaded("architectury");
    }

    @Override
    public void onLoad(String mixinPackage) {
        // NO-OP
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("blockrenderlayer")) {
            return ALLOW_RENDER_LAYER_MIXINS;
        } else if (mixinClassName.endsWith("gl.JsonEffectGlShaderMixin")) {
            return ENABLE_SHADER_EFFECT_LOCATION_MIXIN;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // NO-OP
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // NO-OP
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // NO-OP
    }
}
