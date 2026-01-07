package io.github.insomniacteam.skyforgeddreams.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GlowBerriesOnStalactiteFeature extends Feature<NoneFeatureConfiguration> {

    public GlowBerriesOnStalactiteFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin(); // This is the solid block position (ceiling)
        RandomSource random = context.random();

        // Check that position is solid (should be, from placement modifiers)
        if (!level.getBlockState(pos).isSolid()) {
            return false;
        }

        // Check that block below is air
        BlockPos vineStart = pos.below();
        if (!level.getBlockState(vineStart).isAir()) {
            return false;
        }

        // Determine vine length: 2-8 blocks
        int vineLength = 2 + random.nextInt(7); // 2 to 8

        // Check if there's enough space below for the vine
        for (int i = 0; i < vineLength; i++) {
            BlockPos checkPos = vineStart.below(i);
            if (!level.getBlockState(checkPos).isAir()) {
                // Not enough space, reduce vine length to fit
                vineLength = i;
                break;
            }
        }

        // Need at least 2 blocks to place
        if (vineLength < 2) {
            return false;
        }

        // Determine how many berries: 1-3
        int berriesCount = 1 + random.nextInt(3); // 1 to 3
        // Create list of positions that will have berries
        boolean[] hasBerries = new boolean[vineLength];
        for (int i = 0; i < berriesCount && i < vineLength; i++) {
            int berryPos;
            do {
                berryPos = random.nextInt(vineLength);
            } while (hasBerries[berryPos]); // Find unique position
            hasBerries[berryPos] = true;
        }

        // Place the vine blocks
        for (int i = 0; i < vineLength; i++) {
            BlockPos vinePos = vineStart.below(i);

            // Determine if this is the last block of the vine
            boolean isLastBlock = (i == vineLength - 1);

            // Place vine block
            BlockState vineState;
            if (isLastBlock) {
                // Last block: use CAVE_VINES (tip block)
                vineState = Blocks.CAVE_VINES.defaultBlockState()
                        .setValue(CaveVinesBlock.BERRIES, hasBerries[i]);
            } else {
                // Middle blocks: use CAVE_VINES_PLANT (body block)
                vineState = Blocks.CAVE_VINES_PLANT.defaultBlockState()
                        .setValue(CaveVinesBlock.BERRIES, hasBerries[i]);
            }

            level.setBlock(vinePos, vineState, 2);
        }

        return true;
    }
}
