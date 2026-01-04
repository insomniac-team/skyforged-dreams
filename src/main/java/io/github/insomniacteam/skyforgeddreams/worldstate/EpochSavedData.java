package io.github.insomniacteam.skyforgeddreams.worldstate;

import io.github.insomniacteam.skyforgeddreams.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class EpochSavedData extends SavedData {
    private static final String DATA_NAME = "skyforged_dreams_epoch";
    private static final String TAG_CURRENT_EPOCH = "CurrentEpoch";
    private static final String TAG_TICKS = "TicksInEpoch";
    private static final String TAG_LAST_CHECKED_TIME = "LastCheckedTime";

    private WorldEpoch currentEpoch;
    private long ticksInCurrentEpoch;
    private long lastCheckedTime;

    public EpochSavedData() {
        this.currentEpoch = Config.startingEpoch;
        this.ticksInCurrentEpoch = 0;
        this.lastCheckedTime = 0;
    }

    public EpochSavedData(CompoundTag tag, HolderLookup.Provider provider) {
        this.currentEpoch = WorldEpoch.valueOf(tag.getString(TAG_CURRENT_EPOCH));
        this.ticksInCurrentEpoch = tag.getLong(TAG_TICKS);
        this.lastCheckedTime = tag.getLong(TAG_LAST_CHECKED_TIME);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        tag.putString(TAG_CURRENT_EPOCH, currentEpoch.name());
        tag.putLong(TAG_TICKS, ticksInCurrentEpoch);
        tag.putLong(TAG_LAST_CHECKED_TIME, lastCheckedTime);
        return tag;
    }

    public static EpochSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                        EpochSavedData::new,
                        EpochSavedData::new,
                        null
                ),
                DATA_NAME
        );
    }

    public WorldEpoch getCurrentEpoch() {
        return currentEpoch;
    }

    public void setCurrentEpoch(WorldEpoch epoch) {
        this.currentEpoch = epoch;
        setDirty();
    }

    public long getTicksInCurrentEpoch() {
        return ticksInCurrentEpoch;
    }

    public void addTicks(long ticks) {
        this.ticksInCurrentEpoch += ticks;
        setDirty();
    }

    public void resetTicks() {
        this.ticksInCurrentEpoch = 0;
        setDirty();
    }

    public long getLastCheckedTime() {
        return lastCheckedTime;
    }

    public void setLastCheckedTime(long time) {
        this.lastCheckedTime = time;
        setDirty();
    }
}
