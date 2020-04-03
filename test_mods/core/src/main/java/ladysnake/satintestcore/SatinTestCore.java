package ladysnake.satintestcore;

import ladysnake.satintestcore.item.SatinTestItems;
import net.fabricmc.api.ModInitializer;

public class SatinTestCore implements ModInitializer {
    public static final String MOD_ID = "satintestcore";

    @Override
    public void onInitialize() {
        SatinTestItems.init();
    }
}
