package io.github.insomniacteam.skyforgeddreams.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LargeConeStalactiteFeature extends Feature<NoneFeatureConfiguration> {

    public LargeConeStalactiteFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        // Check if we can place (need solid block above and air below)
        if (!level.getBlockState(pos).isSolid() || !level.getBlockState(pos.below()).isAir()) {
            return false;
        }

        // Much larger height between 12-20 blocks
        int height = 12 + random.nextInt(9);

        // Larger base width (radius) at the top (ceiling) between 4-6 blocks
        int baseRadius = 4 + random.nextInt(3); // 4, 5, or 6, giving 9x9 to 13x13 base at ceiling

        // Determine block type based on Y level
        // Below Y=0 is deepslate zone (with gradient up to Y=8)
        // Use deepslate if we're at or below Y=0
        BlockState blockToPlace = pos.getY() <= 0 ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState();

        // Build cone from top (wide) to bottom (narrow)
        for (int y = 0; y < height; y++) {
            // Calculate radius at this height (gets smaller as we go down)
            // At y=0 (top at ceiling): radius = baseRadius (widest)
            // At y=height-1 (bottom tip): radius = 0 (single block point)
            float progress = (float) y / (height - 1);
            int currentRadius = (int) (baseRadius * (1.0f - progress));

            BlockPos layerPos = pos.below(y);

            // Place blocks in a circle at this height
            for (int x = -currentRadius; x <= currentRadius; x++) {
                for (int z = -currentRadius; z <= currentRadius; z++) {
                    // Use circle formula to create round cone
                    if (x * x + z * z <= currentRadius * currentRadius) {
                        BlockPos placePos = layerPos.offset(x, 0, z);
                        if (level.getBlockState(placePos).isAir()) {
                            level.setBlock(placePos, blockToPlace, 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}
