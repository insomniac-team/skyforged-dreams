package io.github.insomniacteam.skyforgeddreams.worldstate;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

/**
 * Saved data for storing the current world epoch and day counter.
 * This persists between game sessions.
 */
public class EpochSavedData extends SavedData {
    private static final String DATA_NAME = "skyforged_dreams_epoch";
    private static final String TAG_CURRENT_EPOCH = "CurrentEpoch";
    private static final String TAG_DAYS = "DaysInEpoch";
    private static final String TAG_LAST_CHECKED_DAY = "LastCheckedDay";

    private WorldEpoch currentEpoch;
    private int daysInCurrentEpoch;
    private long lastCheckedDay;

    /**
     * Creates new saved data with default values
     */
    public EpochSavedData() {
        this.currentEpoch = WorldEpoch.WONDERS;
        this.daysInCurrentEpoch = 0;
        this.lastCheckedDay = 0;
    }

    /**
     * Creates saved data from NBT
     */
    public EpochSavedData(CompoundTag tag, HolderLookup.Provider provider) {
        this.currentEpoch = WorldEpoch.valueOf(tag.getString(TAG_CURRENT_EPOCH));
        this.daysInCurrentEpoch = tag.getInt(TAG_DAYS);
        this.lastCheckedDay = tag.getLong(TAG_LAST_CHECKED_DAY);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        tag.putString(TAG_CURRENT_EPOCH, currentEpoch.name());
        tag.putInt(TAG_DAYS, daysInCurrentEpoch);
        tag.putLong(TAG_LAST_CHECKED_DAY, lastCheckedDay);
        return tag;
    }

    /**
     * Gets or creates the saved data for the given level
     */
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

    public int getDaysInCurrentEpoch() {
        return daysInCurrentEpoch;
    }

    public void incrementDaysInEpoch() {
        this.daysInCurrentEpoch++;
        setDirty();
    }

    public void resetDays() {
        this.daysInCurrentEpoch = 0;
        setDirty();
    }

    public long getLastCheckedDay() {
        return lastCheckedDay;
    }

    public void setLastCheckedDay(long day) {
        this.lastCheckedDay = day;
        setDirty();
    }
}
