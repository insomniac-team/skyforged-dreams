package io.github.insomniacteam.skyforgeddreams.event;

import io.github.insomniacteam.skyforgeddreams.SkyforgedDreams;
import io.github.insomniacteam.skyforgeddreams.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

@EventBusSubscriber(modid = SkyforgedDreams.MOD_ID)
public class GlowBerryGrowthHandler {
    private static final int MAX_VINE_LENGTH = 3;

    @SubscribeEvent
    public static void onBlockGrow(CropGrowEvent.Pre event) {
        Level level = (Level) event.getLevel();

        if (!level.dimension().equals(ModDimensions.REVERIE_LANDS)) {
            return;
        }

        if (event.getState().getBlock() instanceof CaveVinesBlock) {
            BlockPos pos = event.getPos();

            int length = 0;
            BlockPos checkPos = pos.above();

            while (length < 10 && (level.getBlockState(checkPos).getBlock() instanceof CaveVinesPlantBlock || level.getBlockState(checkPos).getBlock() instanceof CaveVinesBlock)) {
                length++;
                checkPos = checkPos.above();
            }

            if (length >= MAX_VINE_LENGTH) {
                event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
            }
        }
    }
}
