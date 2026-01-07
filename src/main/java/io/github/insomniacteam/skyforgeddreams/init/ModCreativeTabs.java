package io.github.insomniacteam.skyforgeddreams.init;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SkyforgedDreams.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SKYFORGED_DREAMS_TAB =
            CREATIVE_MODE_TABS.register("skyforged_dreams_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.skyforged_dreams"))
                    .icon(() -> new ItemStack(ModItems.TAPESTRY_OF_THE_AGES.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.DEEPSLATE_ANADIUM_ORE);
                        output.accept(ModItems.RAW_ANADIUM.get());
                        output.accept(ModItems.ANADIUM_INGOT.get());
                        output.accept(ModBlocks.ANADIUM_CHAIN);
                        output.accept(ModItems.TAPESTRY_OF_THE_AGES.get());
                        output.accept(ModItems.REVERIE_COMPASS.get());
                        output.accept(ModItems.BLUETAIL_SPAWN_EGG.get());
                    })
                    .build());
}
