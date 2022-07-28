package com.wolfeiii.emeraldplinko.worldedit.implementations;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.World;
import com.wolfeiii.emeraldplinko.map.MapLoader;
import com.wolfeiii.emeraldplinko.worldedit.MapFactoryCompat;
import com.wolfeiii.emeraldplinko.worldedit.WorldEditRegion;
import com.wolfeiii.emeraldplinko.worldedit.WorldEditVector;
import org.bukkit.Location;

import java.util.Iterator;

public class ModernMapFactoryCompat implements MapFactoryCompat {

    private final World world;

    public ModernMapFactoryCompat(MapLoader mapLoader) {
        this.world = BukkitAdapter.adapt(mapLoader.getMainWorld());
    }

    @Override
    public WorldEditRegion pasteSchematic(Clipboard mapSchematic, Location location) {
        if (mapSchematic == null || location == null) {
            return null;
        }

        try {
            location.setY(mapSchematic.getOrigin().getBlockY());
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                editSession.setSideEffectApplier(SideEffectSet.none());

                final BlockVector3 centerVector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
                final Operation operation = new ClipboardHolder(mapSchematic)
                        .createPaste(editSession)
                        .to(centerVector)
                        .ignoreAirBlocks(true)
                        .build();
                try {
                    Operations.complete(operation);
                    Region region = mapSchematic.getRegion();
                    region.setWorld(world);
                    region.shift(centerVector.subtract(mapSchematic.getOrigin()));

                    final WorldEditVector min = ModernWEHook.transform(region.getMinimumPoint());
                    final WorldEditVector max = ModernWEHook.transform(region.getMaximumPoint());

                    return new WorldEditRegion(min, max, location.getWorld());
                } catch (WorldEditException exception) {
                    exception.printStackTrace();
                    return null;
                }
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterable<WorldEditVector> loop(WorldEditRegion region) {

        final Iterator<BlockVector3> vectors = ModernWEHook.transform(region).iterator();
        final Iterator<WorldEditVector> weVecIterator = new Iterator<WorldEditVector>() {
            @Override
            public boolean hasNext() {
                return vectors.hasNext();
            }

            @Override
            public WorldEditVector next() {
                return ModernWEHook.transform(vectors.next());
            }
        }; //if only we had map() for iterators :(

        return () -> weVecIterator;
    }
}
