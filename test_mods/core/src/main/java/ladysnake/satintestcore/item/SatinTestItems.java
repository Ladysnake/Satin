package ladysnake.satintestcore.item;

import ladysnake.satintestcore.SatinTestCore;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SatinTestItems {
    public static DebugItem DEBUG_ITEM;

    public static void init() {
        DEBUG_ITEM = Registry.register(Registry.ITEM, new Identifier(SatinTestCore.MOD_ID, "debug_item"), new DebugItem(new Item.Settings()));
    }
}