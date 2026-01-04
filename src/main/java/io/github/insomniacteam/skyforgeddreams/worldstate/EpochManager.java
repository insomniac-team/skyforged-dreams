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
        WorldEpoch currentEpoch = data.getCurrentEpoch();
        WorldEpoch nextEpoch = WorldEpoch.getRandomDifferent(currentEpoch, level.random);

        data.setCurrentEpoch(nextEpoch);
        data.resetTicks();
        data.setDirty();

        onEpochChanged(currentEpoch, nextEpoch);
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
     * Gets the number of in-game days remaining in the current epoch
     */
    public int getDaysRemaining() {
        long ticksRemaining = Config.epochDurationTicks - getData().getTicksInCurrentEpoch();
        return (int) (ticksRemaining / Config.TICKS_PER_DAY);
    }

    /**
     * Forces a transition to a specific epoch (for commands/testing)
     */
    public void forceEpoch(WorldEpoch epoch) {
        EpochSavedData data = getData();
        data.setCurrentEpoch(epoch);
        data.resetTicks();
        data.setLastCheckedTime(level.getDayTime());
        data.setDirty();
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
