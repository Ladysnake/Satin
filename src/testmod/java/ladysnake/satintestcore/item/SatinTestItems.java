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
package ladysnake.satintestcore.item;

import ladysnake.satintestcore.SatinTestCore;
import ladysnake.satintestcore.block.SatinTestBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SatinTestItems {
    public static final DebugItem DEBUG_ITEM = new DebugItem(new Item.Settings());
    public static final BlockItem DEBUG_BLOCK = new BlockItem(SatinTestBlocks.DEBUG_BLOCK, new Item.Settings());

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier(SatinTestCore.MOD_ID, "debug_item"), DEBUG_ITEM);
        Registry.register(Registry.ITEM, Registry.BLOCK.getId(SatinTestBlocks.DEBUG_BLOCK), DEBUG_BLOCK);
    }
}