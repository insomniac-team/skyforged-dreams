package io.github.insomniacteam.skyforgeddreams.worldstate;

import io.github.insomniacteam.skyforgeddreams.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EpochSavedData extends SavedData {
    private static final String DATA_NAME = "skyforged_dreams_epoch";
    private static final String TAG_CURRENT_EPOCH = "CurrentEpoch";
    private static final String TAG_NEXT_EPOCH = "NextEpoch";
    private static final String TAG_TICKS = "TicksInEpoch";
    private static final String TAG_LAST_CHECKED_TIME = "LastCheckedTime";
    private static final String TAG_EPOCH_HISTORY = "EpochHistory";

    private WorldEpoch currentEpoch;
    private WorldEpoch nextEpoch;
    private long ticksInCurrentEpoch;
    private long lastCheckedTime;
    private int[] epochHistory; // Stores recent epochs (circular buffer of last N-3 epochs)

    public EpochSavedData() {
        this.currentEpoch = Config.startingEpoch;
        this.nextEpoch = null; // Will be set when needed
        this.ticksInCurrentEpoch = 0;
        this.lastCheckedTime = 0;
        // History size is N-3 to ensure at least 2 available epochs
        // With N=5: history=2, current=1, excluded=3, available=2
        int historySize = Math.max(0, WorldEpoch.values().length - 3);
        this.epochHistory = new int[historySize];
        Arrays.fill(this.epochHistory, -1);
    }

    public EpochSavedData(CompoundTag tag, HolderLookup.Provider provider) {
        this.currentEpoch = WorldEpoch.valueOf(tag.getString(TAG_CURRENT_EPOCH));
        if (tag.contains(TAG_NEXT_EPOCH)) {
            this.nextEpoch = WorldEpoch.valueOf(tag.getString(TAG_NEXT_EPOCH));
        } else {
            this.nextEpoch = null;
        }
        this.ticksInCurrentEpoch = tag.getLong(TAG_TICKS);
        this.lastCheckedTime = tag.getLong(TAG_LAST_CHECKED_TIME);

        // Load epoch history
        if (tag.contains(TAG_EPOCH_HISTORY)) {
            this.epochHistory = tag.getIntArray(TAG_EPOCH_HISTORY);
        } else {
            int historySize = Math.max(0, WorldEpoch.values().length - 3);
            this.epochHistory = new int[historySize];
            Arrays.fill(this.epochHistory, -1);
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        tag.putString(TAG_CURRENT_EPOCH, currentEpoch.name());
        if (nextEpoch != null) {
            tag.putString(TAG_NEXT_EPOCH, nextEpoch.name());
        }
        tag.putLong(TAG_TICKS, ticksInCurrentEpoch);
        tag.putLong(TAG_LAST_CHECKED_TIME, lastCheckedTime);
        tag.putIntArray(TAG_EPOCH_HISTORY, epochHistory);
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

    public WorldEpoch getNextEpoch() {
        return nextEpoch;
    }

    public void setNextEpoch(WorldEpoch epoch) {
        this.nextEpoch = epoch;
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

    public int[] getEpochHistory() {
        return epochHistory;
    }

    /**
     * Adds an epoch to the history, shifting older entries.
     * Uses circular buffer approach to maintain last N-3 epochs.
     */
    public void addToHistory(WorldEpoch epoch) {
        if (epochHistory.length == 0) {
            return; // No history to maintain (less than 3 total epochs)
        }

        // Shift all elements left by one position
        System.arraycopy(epochHistory, 1, epochHistory, 0, epochHistory.length - 1);

        // Add new epoch at the end
        epochHistory[epochHistory.length - 1] = epoch.ordinal();

        setDirty();
    }
}
