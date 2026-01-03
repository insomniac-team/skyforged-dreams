package io.github.insomniacteam.skyforgeddreams;

import com.mojang.logging.LogUtils;
import io.github.insomniacteam.skyforgeddreams.command.EpochCommand;
import io.github.insomniacteam.skyforgeddreams.init.ModCreativeTabs;
import io.github.insomniacteam.skyforgeddreams.init.ModEntityTypes;
import io.github.insomniacteam.skyforgeddreams.init.ModItems;
import io.github.insomniacteam.skyforgeddreams.worldstate.EpochManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;

@Mod(SkyforgedDreams.MOD_ID)
public class SkyforgedDreams {
    public static final String MOD_ID = "skyforged_dreams";
    private static final Logger LOG = LogUtils.getLogger();

    public SkyforgedDreams(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "skyforged-dreams-common.toml");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOG.info("Skyforged Dreams mod initialized - World epochs system active");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        EpochCommand.register(event.getDispatcher());
        LOG.debug("Registered epoch commands");
    }

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            // Only tick on the overworld to avoid multiple ticks
            if (serverLevel.dimension() == ServerLevel.OVERWORLD) {
                EpochManager.get(serverLevel).tick();
            }
        }
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
