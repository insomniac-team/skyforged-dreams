package io.github.insomniacteam.skyforgeddreams.worldstate;

import net.minecraft.util.RandomSource;

/**
 * Represents the different epochs (eras) that the world can be in.
 * Each epoch brings different gameplay mechanics and atmosphere.
 */
public enum WorldEpoch {
    /**
     * Age of Wonders - An epoch of magic and miracles
     */
    WONDERS("wonders"),

    /**
     * Age of Nightmares - An epoch of darkness and horror
     */
    NIGHTMARES("nightmares"),

    /**
     * Age of Myths - An epoch of legends and ancient powers
     */
    MYTHS("myths");

    private final String name;

    WorldEpoch(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets a random epoch that is different from the current one
     * @param current The current epoch to exclude
     * @param random Random instance for generation
     * @return A random epoch different from current
     */
    public static WorldEpoch getRandomDifferent(WorldEpoch current, RandomSource random) {
        WorldEpoch[] values = values();
        WorldEpoch next;
        do {
            next = values[random.nextInt(values.length)];
        } while (next == current && values.length > 1);
        return next;
    }

    /**
     * Gets a random epoch
     * @param random Random instance for generation
     * @return A random epoch
     */
    public static WorldEpoch getRandom(RandomSource random) {
        WorldEpoch[] values = values();
        return values[random.nextInt(values.length)];
    }
}
