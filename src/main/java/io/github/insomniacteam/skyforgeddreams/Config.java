package io.github.insomniacteam.skyforgeddreams;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = SkyforgedDreams.MOD_ID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Epoch System Configuration
    private static final ModConfigSpec.EnumValue<io.github.insomniacteam.skyforgeddreams.worldstate.WorldEpoch> STARTING_EPOCH;
    private static final ModConfigSpec.IntValue EPOCH_DURATION_DAYS;

    static {
        BUILDER.comment("World Epoch System Configuration").push("epochs");

        STARTING_EPOCH = BUILDER
                .comment("The epoch that the world starts in when first created (default: myths)")
                .defineEnum("startingEpoch", io.github.insomniacteam.skyforgeddreams.worldstate.WorldEpoch.MYTHS);

        EPOCH_DURATION_DAYS = BUILDER
                .comment("Duration of each epoch in in-game days (default: 20)",
                        "1 day = 24000 ticks, so 20 days = 480000 ticks",
                        "Minimum: 4 days, Maximum: 512 days")
                .defineInRange("epochDurationDays", 20, 4, 512);

        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

    // Cached config values
    public static io.github.insomniacteam.skyforgeddreams.worldstate.WorldEpoch startingEpoch;
    public static int epochDurationDays;
    public static long epochDurationTicks;

    // Constants
    public static final long TICKS_PER_DAY = 24000L;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        startingEpoch = STARTING_EPOCH.get();
        epochDurationDays = EPOCH_DURATION_DAYS.get();
        epochDurationTicks = epochDurationDays * TICKS_PER_DAY;
    }
}
