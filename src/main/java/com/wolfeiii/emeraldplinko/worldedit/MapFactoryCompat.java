package com.wolfeiii.emeraldplinko.worldedit;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.wolfeiii.emeraldplinko.data.MapSchematic;
import org.bukkit.Location;

public interface MapFactoryCompat {

    WorldEditRegion pasteSchematic(Clipboard mapSchematic, Location location);

    Iterable<WorldEditVector> loop(WorldEditRegion region);
}
