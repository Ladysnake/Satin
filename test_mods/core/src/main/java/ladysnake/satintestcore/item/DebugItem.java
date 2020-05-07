package ladysnake.satintestcore.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DebugItem extends Item {
    private int debugMode;
    private List<DebugCallback> callbacks = new ArrayList<>();

    public DebugItem(Settings settings) {
        super(settings);
    }

    public void registerDebugCallback(DebugCallback callback) {
        this.callbacks.add(callback);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (player.isSneaking() && !world.isClient) {
            if (this.callbacks.size() > 1) {
                debugMode = (debugMode + 1) % this.callbacks.size();
                player.sendMessage(new TranslatableText("Switched mode to %s", debugMode), true);
            }
        } else {
            this.callbacks.get(debugMode).use(world, player, hand);
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack itemStack_1) {
        return true;
    }
}
