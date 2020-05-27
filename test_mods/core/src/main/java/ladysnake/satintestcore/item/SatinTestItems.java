package ladysnake.satintestcore.item;

import ladysnake.satintestcore.SatinTestCore;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SatinTestItems {
    public static final DebugItem DEBUG_ITEM = new DebugItem(new Item.Settings());

    public static void init() {
        Registry.register(Registry.ITEM, new Identifier(SatinTestCore.MOD_ID, "debug_item"), DEBUG_ITEM);
    }
}