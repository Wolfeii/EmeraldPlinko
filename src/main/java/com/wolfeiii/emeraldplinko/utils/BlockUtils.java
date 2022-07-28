package com.wolfeiii.emeraldplinko.utils;

import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import com.wolfeiii.emeraldplinko.map.MapAxis;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class BlockUtils {

    public static Float getYaw(BlockFace face) {
        switch (face) {
            case WEST:
                return 90f;
            case NORTH:
                return 180f;
            case EAST:
                return -90f;
            case SOUTH:
                return -180f;
            default:
                return 0f;
        }
    }

    public static MapAxis getMostlyDirection(PlinkoGame plinkoGame, Location location) {
        return switch (plinkoGame.getPlinkoMap().getAxis()) {
            case WEST, EAST ->
                Math.abs(location.getZ()) - Math.abs(location.getBlockZ()) >= 0.500 ? MapAxis.SOUTH : MapAxis.NORTH;
            case NORTH, SOUTH ->
                Math.abs(location.getX()) - Math.abs(location.getBlockX()) >= 0.500 ? MapAxis.EAST : MapAxis.WEST;
        };
    }

    public enum Direction {
        LEFT,
        RIGHT
    }
}
