package io.github.insomniacteam.skyforgeddreams.entity;

import io.github.insomniacteam.skyforgeddreams.init.ModEntityTypes;
import io.github.insomniacteam.skyforgeddreams.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Tapestry entity that hangs on walls like a painting.
 * Uses GeckoLib for 3D model rendering.
 * Size: 3 blocks wide x 4 blocks tall
 */
public class TapestryEntity extends HangingEntity implements GeoEntity {
    private static final int WIDTH = 3;
    private static final int HEIGHT = 4;

    private static final EntityDataAccessor<Integer> DATA_DIRECTION =
        SynchedEntityData.defineId(TapestryEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public TapestryEntity(EntityType<? extends TapestryEntity> entityType, Level level) {
        super(entityType, level);
    }

    public TapestryEntity(Level level, BlockPos pos, Direction direction) {
        super(ModEntityTypes.TAPESTRY.get(), level, pos);
        // Set direction in parent class first
        this.direction = direction;
        // Then update synched data and rotation
        this.setDirection(direction);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_DIRECTION, Direction.SOUTH.get2DDataValue());
    }

    @Override
    public void setDirection(@NotNull Direction direction) {
        super.setDirection(direction);
        if (direction.getAxis().isHorizontal()) {
            this.setYRot((float)(direction.get2DDataValue() * 90));
            this.yRotO = this.getYRot();
        }
    }

    @Override
    protected @NotNull AABB calculateBoundingBox(BlockPos blockPos, Direction direction) {
        // Center position of the hanging entity
        double centerX = blockPos.getX() + 0.5D;
        double centerY = blockPos.getY();
        double centerZ = blockPos.getZ() + 0.5D;

        // Offset from wall (very thin)
        double wallOffset = 0.0625D / 2.0D; // Half of depth

        // Adjust center position based on wall direction
        centerX -= direction.getStepX() * wallOffset;
        centerZ -= direction.getStepZ() * wallOffset;

        double width = this.getWidth();
        double height = this.getHeight();
        double depth = this.getDepth();

        if (direction.getAxis() == Direction.Axis.Z) {
            return new AABB(
                    centerX - width / 2.0D,
                    centerY - height / 2.0D,
                    centerZ - depth / 2.0D,
                    centerX + width / 2.0D,
                    centerY + height / 2.0D,
                    centerZ + depth / 2.0D
            );
        } else {
            return new AABB(
                    centerX - depth / 2.0D,
                    centerY - height / 2.0D,
                    centerZ - width / 2.0D,
                    centerX + depth / 2.0D,
                    centerY + height / 2.0D,
                    centerZ + width / 2.0D
            );
        }
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    /**
     * Depth of the entity (how far it sticks out from the wall)
     */
    public double getDepth() {
        return 0.0; // 1 pixel
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        if (!this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOENTITYDROPS)) {
            return;
        }

        this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);

        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            if (player.getAbilities().instabuild) {
                return;
            }
        }

        this.spawnAtLocation(ModItems.TAPESTRY_OF_THE_AGES.get());
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public @NotNull ItemStack getPickResult() {
        return new ItemStack(ModItems.TAPESTRY_OF_THE_AGES.get());
    }

    // GeckoLib implementation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // No animations needed for static tapestry
        // Controllers can be added here if you want animations in the future
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
