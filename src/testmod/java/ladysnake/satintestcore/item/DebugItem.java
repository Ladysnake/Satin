/*
 * Satin
 * Copyright (C) 2019-2023 Ladysnake
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
package ladysnake.satintestcore.item;

import com.ibm.icu.impl.ICUResourceBundle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugItem extends Item {
    private int debugMode;
    private final List<DebugCallback> callbacks = new ArrayList<>();
    private Map<DebugCallback, String> callbackNames = new HashMap<>();

    public DebugItem(Settings settings) {
        super(settings);
    }

    public void registerDebugCallback(DebugCallback callback) {
        this.callbacks.add(callback);
    }

    public void registerDebugCallback(String name, DebugCallback callback) {
        this.callbacks.add(callback);
        this.callbackNames.put(callback, name);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (player.isSneaking() && !world.isClient) {
            if (this.callbacks.size() > 1) {
                debugMode = (debugMode + 1) % this.callbacks.size();
                player.sendMessage(Text.translatable("Switched mode to %s", this.callbackNames.getOrDefault(this.callbacks.get(debugMode), ""+debugMode)), true);
            }
        } else if (!player.isSneaking()) {
            this.callbacks.get(debugMode).use(world, player, hand);
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }

    @Override
    public boolean hasGlint(ItemStack itemStack_1) {
        return true;
    }
}
