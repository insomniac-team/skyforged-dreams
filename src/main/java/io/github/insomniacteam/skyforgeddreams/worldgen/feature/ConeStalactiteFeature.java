package io.github.insomniacteam.skyforgeddreams.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class ConeStalactiteFeature extends Feature<ConeStalactiteConfiguration> {

    public ConeStalactiteFeature(Codec<ConeStalactiteConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ConeStalactiteConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        ConeStalactiteConfiguration config = context.config();

        // Get height and base radius from configuration
        int height = config.height().sample(random);
        int baseRadius = config.baseRadius().sample(random);

        // Find ceiling: search upward for solid block with air below
        // Start from current position and search up to find a proper ceiling
        BlockPos ceilingPos = null;
        for (int yOffset = 0; yOffset <= 12; yOffset++) {
            BlockPos checkPos = pos.offset(0, yOffset, 0);
            BlockPos belowCheckPos = checkPos.below();

            // Check if this is a ceiling: solid block with air below it
            if (level.getBlockState(checkPos).isSolid() && level.getBlockState(belowCheckPos).isAir()) {
                ceilingPos = checkPos; // Save the ceiling block position
                break;
            }
        }

        // If no ceiling found, cannot place
        if (ceilingPos == null) {
            return false;
        }

        // Check if there's at least 12 blocks of empty space below ceiling
        for (int i = 1; i <= 12; i++) {
            BlockPos checkBelow = ceilingPos.below(i);
            if (!level.getBlockState(checkBelow).isAir()) {
                return false; // Not enough empty space below
            }
        }

        // Check if ALL blocks in radius around ceiling are solid
        for (int x = -baseRadius; x <= baseRadius; x++) {
            for (int z = -baseRadius; z <= baseRadius; z++) {
                if (x * x + z * z <= baseRadius * baseRadius) {
                    BlockPos checkPos = ceilingPos.offset(x, 0, z);
                    if (!level.getBlockState(checkPos).isSolid()) {
                        return false; // At least one block is not solid, cannot place
                    }
                }
            }
        }

        // Determine block type based on Y level
        BlockState blockToPlace = ceilingPos.getY() <= 0 ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState();

        // Build cone from top (wide) to bottom (narrow), starting below ceiling
        for (int y = 0; y < height; y++) {
            // Calculate radius at this height (gets smaller as we go down)
            float progress = (float) y / (height - 1);
            int currentRadius = (int) (baseRadius * (1.0f - progress));

            BlockPos layerPos = ceilingPos.below(y + 1); // +1 to start below the ceiling block

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
