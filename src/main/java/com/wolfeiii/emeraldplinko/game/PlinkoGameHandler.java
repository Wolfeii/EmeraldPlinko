package com.wolfeiii.emeraldplinko.game;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.data.MapSchematic;
import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;
import com.wolfeiii.emeraldplinko.game.objects.PlinkoMap;
import com.wolfeiii.emeraldplinko.game.options.SideOption;
import com.wolfeiii.emeraldplinko.game.options.SideOptionLoader;
import com.wolfeiii.emeraldplinko.game.options.objects.ActiveSideOption;
import com.wolfeiii.emeraldplinko.map.MapAxis;
import com.wolfeiii.emeraldplinko.utils.BlockUtils;
import com.wolfeiii.emeraldplinko.utils.Cuboid;
import com.wolfeiii.emeraldplinko.utils.DoubleInteger;
import com.wolfeiii.emeraldplinko.worldedit.MapFactoryCompat;
import com.wolfeiii.emeraldplinko.worldedit.WorldEditRegion;
import com.wolfeiii.emeraldplinko.worldedit.WorldEditVector;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class PlinkoGameHandler {

    private final EmeraldPlinko plinkoCore;
    private final MapFactoryCompat compat;

    private final Map<UUID, Location> previousLocation = new HashMap<>();
    private final Map<UUID, PlinkoGame> storage = new HashMap<>();

    public PlinkoGameHandler(EmeraldPlinko plinkoCore, MapFactoryCompat compat) {
        this.plinkoCore = plinkoCore;
        this.compat = compat;
    }

    public List<PlinkoGame> getGames() {
        return new ArrayList<>(storage.values());
    }

    public void clearGame(UUID player) {
        if (!isPlayerInGame(player)) {
            return;
        }

        Player uuidPlayer = plinkoCore.getServer().getPlayer(player);
        if (uuidPlayer != null && uuidPlayer.isOnline()) {
            teleportPlayerBack(uuidPlayer);
        }

        PlinkoGame plinkoGame = getGame(player);
        plinkoGame.clearBalls(true);

        for (ActiveSideOption sideOption : plinkoGame.getPlinkoMap().getSideOptions()) {
            sideOption.onDisable();
        }

        previousLocation.remove(player);
        storage.remove(player);
    }

    public boolean isPlayerInGame(@NotNull UUID uuid) {
        return storage.containsKey(uuid);
    }

    public PlinkoGame getGame(@NotNull UUID uuid) {
        return storage.get(uuid);
    }

    public PlinkoGame createGame(@NotNull Player player) {
        PlinkoGame plinkoGame = new PlinkoGame(player);
        PlinkoGameSettings settings = plinkoGame.getSettings();
        PlinkoMap plinkoMap = createMap(settings.getDifficulty());
        plinkoGame.setPlinkoMap(plinkoMap);
        storage.put(player.getUniqueId(), plinkoGame);
        player.teleport(plinkoMap.getPlayerLocation());
        plinkoCore.getSideOptionLoader().createSideOptions(plinkoGame, settings.getDifficulty());
        this.previousLocation.put(player.getUniqueId(), player.getLocation());
        return plinkoGame;
    }

    public PlinkoMap createMap(@NotNull GameDifficulty difficulty) {
        @NotNull MapSchematic mapSchematic = plinkoCore.getSchematicHandler().getSchematic(difficulty);
        return createMap(mapSchematic, plinkoCore.getMapLoader().nextFreeLocation(), difficulty);
    }

    public PlinkoMap createMap(@NotNull MapSchematic mapSchematic, Location origin, GameDifficulty difficulty) {
        // Cleanup code?

        WorldEditRegion region = compat.pasteSchematic(mapSchematic.getSchematic(), origin);

        Location spawnLoc = null;
        WorldEditVector min = null, max = null;
        MapAxis axis = null;

        World world = origin.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("World for the Schematic was null.");
        }

        for (WorldEditVector vector : compat.loop(region)) {
            Block currentBlock = world.getBlockAt((int) vector.getX(), (int) vector.getY(), (int) vector.getZ());
            Material blockType = currentBlock.getType();

            // No need to check anything if it's air we're checking.
            if (blockType.equals(Material.AIR)) {
                continue;
            }

            // Spawn Block
            if (blockType.equals(Material.RED_GLAZED_TERRACOTTA)) {
                spawnLoc = new Location(origin.getWorld(), vector.getX() + 0.5, vector.getY() + 0.5, vector.getZ() + 0.5);

                if (currentBlock.getState().getBlockData() instanceof Directional) {
                    spawnLoc.setYaw(BlockUtils.getYaw(((Directional) currentBlock.getState().getBlockData()).getFacing()));
                    axis = MapAxis.valueOf(((Directional) currentBlock.getState().getBlockData()).getFacing().name());
                }

                currentBlock.setType(Material.AIR);
                continue;
            }

            // Corner Blocks
            if (blockType.equals(Material.BLUE_GLAZED_TERRACOTTA)) {
                if (min == null) {
                    currentBlock.setType(Material.AIR);
                    min = vector.copy();
                    continue;
                }

                if (max == null) {
                    currentBlock.setType(Material.AIR);
                    max = vector.copy();
                    continue;
                }

                plinkoCore.getLogger().warning(() -> "Map Schematic (" + mapSchematic.getName() + ") has more than 2 corner blocks.");
                break;
            }
        }

        if (min == null || max == null || min.equals(max)) {
            throw new IllegalArgumentException("Map Schematic could not be created. Two distinct corner blocks could not be found.");
        }

        if (spawnLoc == null && origin.getWorld() != null) {
            spawnLoc = origin.getWorld().getHighestBlockAt(origin).getLocation();
            plinkoCore.getLogger().warning(() -> "No valid spawn location was found, a placeholder is used in the mean time. Plugin is searching for " + Material.RED_GLAZED_TERRACOTTA.name());
        }

        if (axis == null) {
            plinkoCore.getLogger().warning(() -> "No axis was found, using default WEST.");
            axis = MapAxis.WEST;
        }

        WorldEditRegion mainRegion = new WorldEditRegion(min, max, origin.getWorld());
        Cuboid cuboid = convertRegionToCuboid(mainRegion, origin.getWorld());
        return new PlinkoMap(spawnLoc, cuboid, axis);
    }

    public Cuboid convertRegionToCuboid(WorldEditRegion region, World world) {
        return new Cuboid(world, (int) region.getMinimumPoint().getX(), (int) region.getMinimumPoint().getY(), (int) region.getMinimumPoint().getZ(),
                (int) region.getMaximumPoint().getX(), (int) region.getMaximumPoint().getY(), (int) region.getMaximumPoint().getZ());
    }

    public void teleportPlayerBack(@NotNull Player player) {
        Location previous = previousLocation.get(player.getUniqueId());
        if (previous != null) {
            player.teleport(previous);
        }
    }

}
