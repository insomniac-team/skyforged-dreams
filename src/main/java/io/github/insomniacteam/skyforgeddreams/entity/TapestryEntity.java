package io.github.insomniacteam.skyforgeddreams.entity;

import io.github.insomniacteam.skyforgeddreams.init.ModEntityTypes;
import io.github.insomniacteam.skyforgeddreams.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TapestryEntity extends HangingEntity implements GeoEntity {
    private static final double DEPTH = 0.125;
    private static final double WIDTH = 3.0;
    private static final double HEIGHT = 4.0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public TapestryEntity(EntityType<? extends TapestryEntity> entityType, Level level) {
        super(entityType, level);
    }

    public TapestryEntity(Level level, BlockPos pos, Direction direction) {
        super(ModEntityTypes.TAPESTRY.get(), level, pos);
        this.setDirection(direction);
    }

    @Override
    protected void setDirection(@NotNull Direction facingDirection) {
        this.direction = facingDirection;
        if (facingDirection.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((float)(this.direction.get2DDataValue() * 90));
        } else {
            this.setXRot((float)(-90 * facingDirection.getAxisDirection().getStep()));
            this.setYRot(0.0F);
        }

        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
    }

    @Override
    public @NotNull Direction getDirection() {
        return this.direction;
    }

    @Override
    protected @NotNull AABB calculateBoundingBox(@NotNull BlockPos pos, @NotNull Direction direction) {
        double offset = 0.5 - (DEPTH / 2.0);
        Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -offset);
        vec3 = vec3.add(0, -0.5, 0);

        Direction.Axis direction$axis = direction.getAxis();
        double d0 = direction$axis == Direction.Axis.X ? DEPTH : WIDTH;
        double d1 = direction$axis == Direction.Axis.Y ? DEPTH : HEIGHT;
        double d2 = direction$axis == Direction.Axis.Z ? DEPTH : WIDTH;

        return AABB.ofSize(vec3, d0, d1, d2);
    }

    @Override
    public boolean survives() {
        BlockPos attachedPos = this.pos.relative(this.direction.getOpposite());
        BlockState attachedState = this.level().getBlockState(attachedPos);

        if (!attachedState.isFaceSturdy(this.level(), attachedPos, this.direction)) {
            return false;
        }

        Direction.Axis axis = this.direction.getAxis();
        Direction widthDirection;

        // Определяем направление ширины
        if (axis == Direction.Axis.Z) {
            widthDirection = Direction.EAST;
        } else if (axis == Direction.Axis.X) {
            widthDirection = Direction.SOUTH;
        } else {
            return true;
        }

        // Проверяем все блоки за картиной (3 ширина × 4 высота)
        for (int w = -1; w <= 1; w++) {
            for (int h = -2; h <= 1; h++) {
                BlockPos checkPos = this.pos
                        .relative(widthDirection, w)
                        .relative(Direction.UP, h)
                        .relative(this.direction.getOpposite());

                BlockState state = this.level().getBlockState(checkPos);
                if (!state.isFaceSturdy(this.level(), checkPos, this.direction)) {
                    return false;
                }
            }
        }

        // Проверяем, что картина не пересекается с коллизией других блоков
        AABB boundingBox = this.getBoundingBox();

        // Получаем все позиции блоков, которые может занимать bounding box
        int minX = Mth.floor(boundingBox.minX);
        int minY = Mth.floor(boundingBox.minY);
        int minZ = Mth.floor(boundingBox.minZ);
        int maxX = Mth.floor(boundingBox.maxX);
        int maxY = Mth.floor(boundingBox.maxY);
        int maxZ = Mth.floor(boundingBox.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos checkPos = new BlockPos(x, y, z);
                    BlockState state = this.level().getBlockState(checkPos);

                    // Пропускаем блок, к которому прикреплена картина
                    if (checkPos.equals(attachedPos)) {
                        continue;
                    }

                    // Проверяем пересечение с коллизией блока
                    VoxelShape blockShape = state.getCollisionShape(this.level(), checkPos);
                    if (!blockShape.isEmpty()) {
                        AABB blockAABB = blockShape.bounds().move(checkPos);
                        if (boundingBox.intersects(blockAABB)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putByte("Facing", (byte)this.direction.get3DDataValue());
        tag.putBoolean("Invisible", this.isInvisible());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.setDirection(Direction.from3DDataValue(tag.getByte("Facing")));
        this.setInvisible(tag.getBoolean("Invisible"));
    }


    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        // No synched data needed - we get epochs from EpochManager
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(@NotNull ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
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
