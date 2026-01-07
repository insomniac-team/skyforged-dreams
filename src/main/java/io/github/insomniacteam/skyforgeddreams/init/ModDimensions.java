package io.github.insomniacteam.skyforgeddreams.init;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ModDimensions {
    // Dimension keys
    public static final ResourceKey<Level> REVERIE_LANDS = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(SkyforgedDreams.MOD_ID, "reverie_lands")
    );

    public static final ResourceKey<DimensionType> REVERIE_LANDS_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(SkyforgedDreams.MOD_ID, "reverie_lands_type")
    );

    public static void register() {
        SkyforgedDreams.LOG.info("Registering Dimensions for " + SkyforgedDreams.MOD_ID);
    }
}
