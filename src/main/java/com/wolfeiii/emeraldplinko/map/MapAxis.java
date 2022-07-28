package com.wolfeiii.emeraldplinko.map;

import lombok.Getter;

public enum MapAxis {

    WEST(90.0),
    NORTH(180.0),
    SOUTH(0.0),
    EAST(-90.0);

    @Getter
    private double forwardAxis;

    MapAxis(double forwardAxis) {
        this.forwardAxis = forwardAxis;
    }
}
