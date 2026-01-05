package io.github.insomniacteam.skyforgeddreams.worldstate;

import io.github.insomniacteam.skyforgeddreams.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class EpochManager {
    private final ServerLevel level;

    public EpochManager(ServerLevel level) {
        this.level = level;
    }

    /**
     * Called every server tick to check if epoch should transition.
     * Uses dayTime which accounts for sleep, /time commands, and natural progression.
     */
    public void tick() {
        EpochSavedData data = getData();
        long currentTime = level.getDayTime();
        long lastCheckedTime = data.getLastCheckedTime();

        // Ensure next epoch is selected
        if (data.getNextEpoch() == null) {
            selectNextEpoch();
        }

        // Calculate time difference (handles both forward progression and time manipulation)
        long timeDifference = currentTime - lastCheckedTime;

        // Update last checked time
        data.setLastCheckedTime(currentTime);

        // Handle time going backwards (e.g., /time set to earlier value)
        if (timeDifference < 0) {
            // Time went backwards, don't progress epochs
            return;
        }

        // Handle time skip forward (e.g., sleeping, /time add)
        if (timeDifference > 0) {
            data.addTicks(timeDifference);

            // Check if we need to transition
            if (data.getTicksInCurrentEpoch() >= Config.epochDurationTicks) {
                transitionToNextEpoch();
            }
        }
    }

    private void transitionToNextEpoch() {
        EpochSavedData data = getData();
        WorldEpoch previousEpoch = data.getCurrentEpoch();
        WorldEpoch nextEpoch = data.getNextEpoch();

        // Transition to the pre-selected next epoch
        data.setCurrentEpoch(nextEpoch);
        data.resetTicks();

        // Select the next epoch for the following transition
        selectNextEpoch();

        data.setDirty();

        onEpochChanged(previousEpoch, nextEpoch);
    }

    /**
     * Selects and stores the next epoch that will come after the current one
     */
    private void selectNextEpoch() {
        EpochSavedData data = getData();
        WorldEpoch currentEpoch = data.getCurrentEpoch();
        WorldEpoch nextEpoch = WorldEpoch.getRandomDifferent(currentEpoch, level.random);
        data.setNextEpoch(nextEpoch);
        data.setDirty();
    }

    /**
     * Called when epoch changes
     */
    private void onEpochChanged(WorldEpoch from, WorldEpoch to) {
        MinecraftServer server = level.getServer();
        server.getPlayerList().getPlayers().forEach(player -> {
            // TODO: Send notification to players
        });
    }

    /**
     * Gets the current epoch
     */
    public WorldEpoch getCurrentEpoch() {
        return getData().getCurrentEpoch();
    }

    /**
     * Gets the detailed time remaining in the current epoch
     * @return array of [days, hours, minutes]
     */
    public int[] getDetailedTimeRemaining() {
        long ticksRemaining = Config.epochDurationTicks - getData().getTicksInCurrentEpoch();

        int days = (int) (ticksRemaining / 24000L);
        long remainingAfterDays = ticksRemaining % 24000L;

        int hours = (int) (remainingAfterDays / 1000L);
        long remainingAfterHours = remainingAfterDays % 1000L;

        int minutes = (int) ((remainingAfterHours * 60L) / 1000L);

        return new int[]{days, hours, minutes};
    }

    /**
     * Gets the progress percentage of the current epoch (0-100)
     */
    public int getProgressPercentage() {
        long ticksInEpoch = getData().getTicksInCurrentEpoch();
        return (int) ((ticksInEpoch * 100L) / Config.epochDurationTicks);
    }

    /**
     * Gets the next epoch that will occur after the current one
     */
    public WorldEpoch getNextEpoch() {
        return getData().getNextEpoch();
    }

    /**
     * Forces a transition to a specific epoch (for commands/testing)
     */
    public void forceEpoch(WorldEpoch epoch) {
        EpochSavedData data = getData();
        WorldEpoch previousEpoch = data.getCurrentEpoch();

        data.setCurrentEpoch(epoch);
        data.resetTicks();
        data.setLastCheckedTime(level.getDayTime());

        // Select a new next epoch after forcing
        selectNextEpoch();

        data.setDirty();

        onEpochChanged(previousEpoch, epoch);
    }

    /**
     * Forces an immediate transition to the pre-selected next epoch
     * @return true if transition was successful, false if no next epoch was set
     */
    public boolean forceNextEpoch() {
        EpochSavedData data = getData();
        WorldEpoch nextEpoch = data.getNextEpoch();

        if (nextEpoch == null) {
            return false;
        }

        transitionToNextEpoch();
        return true;
    }

    /**
     * Gets or creates the saved data for this level
     */
    private EpochSavedData getData() {
        return EpochSavedData.get(level);
    }

    /**
     * Gets the EpochManager for a given server level
     */
    public static EpochManager get(ServerLevel level) {
        return new EpochManager(level);
    }
}
