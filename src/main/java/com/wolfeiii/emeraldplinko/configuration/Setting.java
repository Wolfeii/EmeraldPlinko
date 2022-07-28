package com.wolfeiii.emeraldplinko.configuration;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public enum Setting {

    BORDER_DISTANCE("settings.border-distance"),
    MAP_WORLD_NAME("settings.map-world-name");

    @Getter
    private final String path;

    Setting(@NotNull String path) {
        this.path = path;
    }
}
