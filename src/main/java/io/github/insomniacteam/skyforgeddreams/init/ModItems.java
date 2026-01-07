package io.github.insomniacteam.skyforgeddreams.init;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.item.CustomSpawnEggItem;
import io.github.insomniacteam.skyforgeddreams.item.TapestryItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, SkyforgedDreams.MOD_ID);

    public static final DeferredHolder<Item, TapestryItem> TAPESTRY_OF_THE_AGES =
            ITEMS.register("tapestry",
                    () -> new TapestryItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> ANADIUM_INGOT =
            ITEMS.register("anadium_ingot",
                    () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> RAW_ANADIUM =
            ITEMS.register("raw_anadium",
                    () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, CustomSpawnEggItem> BLUETAIL_SPAWN_EGG =
            ITEMS.register("bluetail_spawn_egg",
                    () -> new CustomSpawnEggItem(ModEntityTypes.BLUETAIL, new Item.Properties()));
}
