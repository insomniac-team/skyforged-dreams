package io.github.insomniacteam.skyforgeddreams.worldstate;

/**
 * Client-side cache for epoch data received from the server.
 * Used to render epoch-based content on the client without direct server access.
 */
public class ClientEpochCache {
    private static WorldEpoch currentEpoch = WorldEpoch.WONDERS;
    private static WorldEpoch nextEpoch = WorldEpoch.SHADOWS;
    private static int progress = 0;

    public static void update(WorldEpoch current, WorldEpoch next, int progressPercent) {
        currentEpoch = current;
        nextEpoch = next;
        progress = progressPercent;
    }

    public static WorldEpoch getCurrentEpoch() {
        return currentEpoch;
    }

    public static WorldEpoch getNextEpoch() {
        return nextEpoch;
    }

    public static int getProgress() {
        return progress;
    }
}
