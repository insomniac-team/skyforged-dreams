package io.github.insomniacteam.skyforgeddreams;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = SkyforgedDreams.MOD_ID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Epoch System Configuration
    private static final ModConfigSpec.IntValue EPOCH_DURATION_DAYS;

    static {
        BUILDER.comment("World Epoch System Configuration").push("epochs");

        EPOCH_DURATION_DAYS = BUILDER
                .comment("Duration of each epoch in in-game days (default: 20)")
                .defineInRange("epochDurationDays", 20, 1, 1000);

        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

    // Cached config values
    public static int epochDurationDays;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        epochDurationDays = EPOCH_DURATION_DAYS.get();
    }
}
