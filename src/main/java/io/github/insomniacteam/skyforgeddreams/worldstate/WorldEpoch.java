package io.github.insomniacteam.skyforgeddreams.worldstate;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the different epochs (eras) that the world can be in.
 * Each epoch brings different gameplay mechanics and atmosphere.
 */
public enum WorldEpoch {
    WONDERS("wonders"),
    SHADOWS("shadows"),
    MYTHS("myths"),
    ETERNITY("eternity"),
    ASHES("ashes");

    private final String name;

    WorldEpoch(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static WorldEpoch getRandomDifferent(WorldEpoch current, RandomSource random) {
        WorldEpoch[] values = values();
        WorldEpoch next;
        do {
            next = values[random.nextInt(values.length)];
        } while (next == current && values.length > 1);
        return next;
    }

    public static WorldEpoch getRandom(RandomSource random) {
        WorldEpoch[] values = values();
        return values[random.nextInt(values.length)];
    }

    /**
     * Selects the next epoch based on recent history.
     * Excludes the current epoch and any epochs in the recent history (last N-3 epochs).
     * This ensures variety by preventing repetition while maintaining randomness.
     * With N-3 history, there are always at least 2 available epochs to choose from randomly.
     *
     * @param current The current epoch (to exclude it from selection)
     * @param epochHistory Array of recent epoch ordinals (size N-3, -1 means empty slot)
     * @param random Random source for selection
     * @return The selected next epoch
     */
    public static WorldEpoch getNextByHistory(WorldEpoch current, int[] epochHistory, RandomSource random) {
        WorldEpoch[] allEpochs = values();

        // Build list of available epochs (not current, not in history)
        List<WorldEpoch> available = new ArrayList<>();
        for (WorldEpoch epoch : allEpochs) {
            if (epoch == current) {
                continue; // Skip current epoch
            }

            // Check if this epoch is in recent history
            boolean inHistory = false;
            for (int historyOrdinal : epochHistory) {
                if (historyOrdinal == epoch.ordinal()) {
                    inHistory = true;
                    break;
                }
            }

            if (!inHistory) {
                available.add(epoch);
            }
        }

        // If no available epochs (shouldn't happen with N-2 history size), fallback
        if (available.isEmpty()) {
            return getRandomDifferent(current, random);
        }

        // Always pick randomly from available epochs to ensure unpredictability
        if (available.size() == 1) {
            return available.get(0);
        } else {
            // Pick randomly from all available epochs
            return available.get(random.nextInt(available.size()));
        }
    }
}
