package ladysnake.satintestcore.block;

import ladysnake.satintestcore.SatinTestCore;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SatinTestBlocks {
    public static final Block DEBUG_BLOCK = new Block(Block.Settings.of(Material.STONE));
    
    public static void init() {
        Registry.register(Registry.BLOCK, new Identifier(SatinTestCore.MOD_ID, "debug_block"), DEBUG_BLOCK);
    }
}
