package io.github.insomniacteam.skyforgeddreams.item;

import io.github.insomniacteam.skyforgeddreams.entity.TapestryEntity;
import io.github.insomniacteam.skyforgeddreams.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class TapestryItem extends HangingEntityItem {
    public TapestryItem(Properties properties) {
        super(ModEntityTypes.TAPESTRY.get(), properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos relativePos = blockPos.relative(direction);
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        Level level = context.getLevel();

        // Only allow placement on horizontal faces (not floor or ceiling)
        if (!direction.getAxis().isHorizontal()) {
            return InteractionResult.FAIL;
        }

        if (player != null && !this.mayPlace(player, direction, itemStack, relativePos)) {
            return InteractionResult.FAIL;
        }

        // Create the tapestry entity
        TapestryEntity tapestry = new TapestryEntity(level, relativePos, direction);

        // Check if the tapestry can survive at this position
        if (!tapestry.survives()) {
            return InteractionResult.FAIL;
        }

        tapestry.setDirection(direction);
        // On server side, spawn the entity
        if (!level.isClientSide) {
            tapestry.playPlacementSound();
            level.gameEvent(player, GameEvent.ENTITY_PLACE, tapestry.position());
            level.addFreshEntity(tapestry);
        }

        // Consume the item
        itemStack.shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected boolean mayPlace(@NotNull Player player, Direction direction, @NotNull ItemStack stack, @NotNull BlockPos pos) {
        return !direction.getAxis().isVertical() && player.mayUseItemAt(pos, direction, stack);
    }
}
