package io.github.insomniacteam.skyforgeddreams.item;

import io.github.insomniacteam.skyforgeddreams.init.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.NotNull;

public class ReverieTeleportItem extends Item {
    public ReverieTeleportItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = level.getServer();
            if (server == null) {
                return InteractionResultHolder.fail(itemStack);
            }

            // Determine target dimension
            ServerLevel targetLevel;
            if (level.dimension() == ModDimensions.REVERIE_LANDS) {
                // Return to Overworld
                targetLevel = server.getLevel(Level.OVERWORLD);
            } else {
                // Go to Reverie Lands
                targetLevel = server.getLevel(ModDimensions.REVERIE_LANDS);
            }

            if (targetLevel == null) {
                serverPlayer.sendSystemMessage(Component.literal("Dimension not found!"));
                return InteractionResultHolder.fail(itemStack);
            }

            // Play sound
            level.playSound(null, player.blockPosition(), SoundEvents.PORTAL_TRIGGER,
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            // Find safe spawn position
            BlockPos spawnPos = targetLevel.getSharedSpawnPos();

            // Teleport player
            DimensionTransition transition = new DimensionTransition(
                    targetLevel,
                    spawnPos.getCenter(),
                    serverPlayer.getDeltaMovement(),
                    serverPlayer.getYRot(),
                    serverPlayer.getXRot(),
                    DimensionTransition.PLAY_PORTAL_SOUND
            );

            serverPlayer.changeDimension(transition);

            // Message player
            if (level.dimension() == ModDimensions.REVERIE_LANDS) {
                serverPlayer.sendSystemMessage(Component.literal("Returned to the Overworld"));
            } else {
                serverPlayer.sendSystemMessage(Component.literal("Entered the Reverie Lands"));
            }

            // Damage item if not in creative
            if (!serverPlayer.getAbilities().instabuild) {
                itemStack.hurtAndBreak(1, serverPlayer, serverPlayer.getEquipmentSlotForItem(itemStack));
            }

            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.consume(itemStack);
    }
}
