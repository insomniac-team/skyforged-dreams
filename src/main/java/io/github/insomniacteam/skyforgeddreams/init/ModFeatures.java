package io.github.insomniacteam.skyforgeddreams.init;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.worldgen.feature.ConeStalactiteConfiguration;
import io.github.insomniacteam.skyforgeddreams.worldgen.feature.ConeStalactiteFeature;
import io.github.insomniacteam.skyforgeddreams.worldgen.feature.GlowBerriesOnStalactiteFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, SkyforgedDreams.MOD_ID);

    public static final DeferredHolder<Feature<?>, Feature<ConeStalactiteConfiguration>> CONE_STALACTITE =
            FEATURES.register("cone_stalactite", () -> new ConeStalactiteFeature(ConeStalactiteConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> GLOW_BERRIES_ON_STALACTITE =
            FEATURES.register("glow_berries_on_stalactite", () -> new GlowBerriesOnStalactiteFeature(NoneFeatureConfiguration.CODEC));
}
