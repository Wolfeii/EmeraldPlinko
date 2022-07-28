package com.wolfeiii.emeraldplinko.map.generator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EmptyWorldGenerator extends ChunkGenerator {

    public byte[][] blockSections;

    @NotNull
    @Override
    public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull ChunkGenerator.BiomeGrid biome) {
        return super.generateChunkData(world, random, x, z, biome);
    }

    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomeGrid) {
        if (blockSections == null) {
            blockSections = new byte[world.getMaxHeight() / 16][];
        }
        return blockSections;
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    /**
     * Tells Bukkit that we never want any block populators.
     *
     * @param world The world where the {@link BlockPopulator}s should be in
     * @return Always an empty list because we don't want any
     */
    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return Collections.emptyList();
    }
}
