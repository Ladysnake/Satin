package ladysnake.satintestcore.item;

import ladysnake.satintestcore.block.SatinTestBlocks;
import ladysnake.satintestcore.SatinTestCore;
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