package io.github.insomniacteam.skyforgeddreams.init;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.item.TapestryItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, SkyforgedDreams.MOD_ID);

    public static final DeferredHolder<Item, TapestryItem> TAPESTRY_OF_THE_AGES =
            ITEMS.register("tapestry",
                    () -> new TapestryItem(new Item.Properties()));
}
