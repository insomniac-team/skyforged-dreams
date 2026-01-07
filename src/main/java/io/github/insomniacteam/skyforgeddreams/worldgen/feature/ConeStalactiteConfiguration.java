package io.github.insomniacteam.skyforgeddreams.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record ConeStalactiteConfiguration(
        IntProvider height,
        IntProvider baseRadius
) implements FeatureConfiguration {
    public static final Codec<ConeStalactiteConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    IntProvider.CODEC.fieldOf("height").forGetter(ConeStalactiteConfiguration::height),
                    IntProvider.CODEC.fieldOf("base_radius").forGetter(ConeStalactiteConfiguration::baseRadius)
            ).apply(instance, ConeStalactiteConfiguration::new)
    );
}
