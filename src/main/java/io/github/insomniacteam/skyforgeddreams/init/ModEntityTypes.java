package io.github.insomniacteam.skyforgeddreams.init;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.entity.BluetailEntity;
import io.github.insomniacteam.skyforgeddreams.entity.TapestryEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SkyforgedDreams.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<TapestryEntity>> TAPESTRY =
        ENTITY_TYPES.register("tapestry", () -> EntityType.Builder.<TapestryEntity>of(
            TapestryEntity::new,
            MobCategory.MISC
        )
        .sized(0.5F, 0.5F) // Hitbox size, actual visual size is determined by the model
        .clientTrackingRange(10)
        .updateInterval(Integer.MAX_VALUE) // Static entity, no need for frequent updates
        .build("tapestry"));

    public static final DeferredHolder<EntityType<?>, EntityType<BluetailEntity>> BLUETAIL =
        ENTITY_TYPES.register("bluetail", () -> EntityType.Builder.of(
            BluetailEntity::new,
            MobCategory.CREATURE
        )
        .sized(0.4F, 0.7F) // Similar to chicken
        .clientTrackingRange(10)
        .build("bluetail"));
}
