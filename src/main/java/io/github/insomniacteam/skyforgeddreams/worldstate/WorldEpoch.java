package io.github.insomniacteam.skyforgeddreams.worldstate;

import net.minecraft.util.RandomSource;

/**
 * Represents the different epochs (eras) that the world can be in.
 * Each epoch brings different gameplay mechanics and atmosphere.
 */
public enum WorldEpoch {
    WONDERS("wonders"),
    NIGHTMARES("nightmares"),
    MYTHS("myths");

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
}
