package ladysnake.satintestcore.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@FunctionalInterface
public interface DebugCallback {
    void use(World world, PlayerEntity player, Hand hand);
}
