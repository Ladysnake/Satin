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
package org.ladysnake.satintestcore.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.ladysnake.satintestcore.SatinTestCore;
import org.ladysnake.satintestcore.block.SatinTestBlocks;

public class SatinTestItems {
    private static final RegistryKey<Item> DEBUG_ITEM_ID = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SatinTestCore.MOD_ID, "debug_item"));
    public static final DebugItem DEBUG_ITEM = new DebugItem(new Item.Settings().registryKey(DEBUG_ITEM_ID));
    private static final RegistryKey<Item> DEBUG_BLOCK_ID = RegistryKey.of(RegistryKeys.ITEM, SatinTestBlocks.DEBUG_BLOCK_ID.getValue());
    public static final BlockItem DEBUG_BLOCK = new BlockItem(SatinTestBlocks.DEBUG_BLOCK, new Item.Settings().registryKey(DEBUG_BLOCK_ID));

    public static void init() {
        Registry.register(Registries.ITEM, DEBUG_ITEM_ID, DEBUG_ITEM);
        Registry.register(Registries.ITEM, DEBUG_BLOCK_ID, DEBUG_BLOCK);
    }
}