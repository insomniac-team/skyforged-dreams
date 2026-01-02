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
     */
    public void tick() {
        EpochSavedData data = getData();
        long currentDay = level.getDayTime() / 24000L;
        long lastCheckedDay = data.getLastCheckedDay();

        if (currentDay > lastCheckedDay) {
            long daysPassed = currentDay - lastCheckedDay;

            data.setLastCheckedDay(currentDay);

            for (long i = 0; i < daysPassed; i++) {
                data.incrementDaysInEpoch();

                if (data.getDaysInCurrentEpoch() >= Config.epochDurationDays) {
                    transitionToNextEpoch();
                }
            }
        } else if (currentDay < lastCheckedDay) {
            data.setLastCheckedDay(currentDay);
        }
    }

    /**
     * Transitions to a random new epoch
     */
    private void transitionToNextEpoch() {
        EpochSavedData data = getData();
        WorldEpoch currentEpoch = data.getCurrentEpoch();
        WorldEpoch nextEpoch = WorldEpoch.getRandomDifferent(currentEpoch, level.random);

        data.setCurrentEpoch(nextEpoch);
        data.resetDays();
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
        return Config.epochDurationDays - getData().getDaysInCurrentEpoch();
    }

    /**
     * Forces a transition to a specific epoch (for commands/testing)
     */
    public void forceEpoch(WorldEpoch epoch) {
        EpochSavedData data = getData();
        data.setCurrentEpoch(epoch);
        data.resetDays();
        data.setLastCheckedDay(level.getDayTime() / 24000L);
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
