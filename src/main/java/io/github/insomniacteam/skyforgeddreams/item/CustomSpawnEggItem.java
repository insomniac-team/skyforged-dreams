package io.github.insomniacteam.skyforgeddreams.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.function.Supplier;

/**
 * A spawn egg that uses a custom texture without color tinting.
 * Unlike DeferredSpawnEggItem, this class doesn't apply colors to the texture.
 */
public class CustomSpawnEggItem extends DeferredSpawnEggItem {

    public CustomSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> entityType, Item.Properties properties) {
        super(entityType, 0xFFFFFF, 0xFFFFFF, properties);
    }

    @Override
    public int getColor(int layer) {
        // Return white color (no tint) for both layers
        return 0xFFFFFF;
    }
}
