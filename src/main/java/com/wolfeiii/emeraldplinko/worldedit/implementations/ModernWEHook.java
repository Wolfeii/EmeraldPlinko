package com.wolfeiii.emeraldplinko.worldedit.implementations;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.data.MapSchematic;
import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;
import com.wolfeiii.emeraldplinko.worldedit.MapFactoryCompat;
import com.wolfeiii.emeraldplinko.worldedit.WorldEditHook;
import com.wolfeiii.emeraldplinko.worldedit.WorldEditRegion;
import com.wolfeiii.emeraldplinko.worldedit.WorldEditVector;

import java.io.File;

public class ModernWEHook implements WorldEditHook {

    public static Region transform(WorldEditRegion region) {
        return new CuboidRegion(
                BukkitAdapter.adapt(region.getWorld()),
                transform(region.getMinimumPoint()),
                transform(region.getMaximumPoint())
        );
    }

    public static BlockVector3 transform(WorldEditVector vector) {
        return BlockVector3.at(vector.getX(), vector.getY(), vector.getZ());
    }

    public static WorldEditVector transform(BlockVector3 vector) {
        return new WorldEditVector(vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public MapFactoryCompat createMapFactoryCompat() {
        return new ModernMapFactoryCompat(EmeraldPlinko.getInstance().getMapLoader());
    }

    @Override
    public MapSchematic loadMineSchematic(String name, File file, GameDifficulty difficulty) {
        return new ModernWEMapSchematic(name, file, difficulty);
    }
}
