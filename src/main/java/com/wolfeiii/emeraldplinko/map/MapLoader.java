package com.wolfeiii.emeraldplinko.map;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.configuration.Setting;
import com.wolfeiii.emeraldplinko.map.generator.EmptyWorldGenerator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class MapLoader {

    private final EmeraldPlinko plinkoCore;
    private final Location defaultLocation;
    private final int borderDistance;
    private int distance;
    private Direction direction;
    private World mainWorld;

    public MapLoader(EmeraldPlinko plinkoCore) {
        this.plinkoCore = plinkoCore;
        this.borderDistance = plinkoCore.getIntegerSetting(Setting.BORDER_DISTANCE);
        this.mainWorld = createWorld(World.Environment.NORMAL, plinkoCore.getStringSetting(Setting.MAP_WORLD_NAME));
        this.direction = Direction.NORTH;
        this.defaultLocation = new Location(mainWorld, 0, 0, 0);
    }

    public World createWorld(World.Environment environment, String name) {
        WorldCreator creator = new WorldCreator(name)
                .generator(new EmptyWorldGenerator())
                .environment(environment);
        return Bukkit.createWorld(creator);
    }

    public synchronized Location nextFreeLocation() {
        if (distance == 0) {
            distance++;
            return defaultLocation;
        }

        if (direction == null) direction = Direction.NORTH;
        Location location = direction.addTo(defaultLocation, distance * borderDistance);
        direction = direction.next();
        if (direction == Direction.NORTH) distance++;
        return location;
    }

    public World getMainWorld() {
        return mainWorld;
    }

    public enum Direction {
        NORTH(0, -1), NORTH_EAST(1, -1),
        EAST(1, 0), SOUTH_EAST(1, 1),
        SOUTH(0, 1), SOUTH_WEST(-1, 1),
        WEST(-1, 0), NORTH_WEST(-1, -1);

        private final int xMulti;
        private final int zMulti;

        Direction(int xMulti, int zMulti) {
            this.xMulti = xMulti;
            this.zMulti = zMulti;
        }

        Direction next() {
            return values()[(ordinal() + 1) % (values().length)];
        }

        Location addTo(Location loc, int value) {

            return loc.clone().add(value * (double) xMulti, 0, value * (double) zMulti);
        }
    }

}
