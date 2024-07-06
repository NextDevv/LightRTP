package com.nextdevv.lightrtp.configs;

import com.nextdevv.lightrtp.enums.RandomOption;
import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
public class Settings {
    @Comment({
            "The random option to use when selecting a spawn location. RANDOM or SECURE_RANDOM.",
            "SECURE_RANDOM is recommended for production servers.",
            "THREAD_LOCAL_RANDOM is recommended for development servers.",
            "Options: RANDOM, THREAD_LOCAL_RANDOM, SECURE_RANDOM",
            "Default: RANDOM"
    })
    RandomOption randomOption = RandomOption.RANDOM;

    @Comment({
            "The seed to use for random algorithms. Set to 0 for a random seed.",
            "Default: 0"
    })
    long seed = 0;

    @Comment({
            "Allow movement while finding a location.",
            "Default: false"
    })
    boolean allowMovement = false;

    @Comment({
            "The maximum number of attempts to find a location.",
            "Default: 5"
    })
    int maxAttempts = 5;

    @Comment({
            "The timeout for the random location selection.",
            "Default: 10000"
    })
    long timeout = 10000;

    @Comment({
            "The cooldown for the command.",
            "Default: 5000"
    })
    long commandCooldown = 5000;

    @Comment({
            "X Boundary for random location selection.",
            "Default: 100000"
    })
    int xBoundary = 100000;

    @Comment({
            "Z Boundary for random location selection.",
            "Default: 100000"
    })
    int zBoundary = 100000;

    @Comment("Allow tp in the nether.")
    boolean nether = false;

    @Comment("Allow tp to caves.")
    boolean caves = false;

    @Comment("Allow tp to water.")
    boolean water = false;

    @Comment("Debug mode.")
    boolean debug = false;
}
