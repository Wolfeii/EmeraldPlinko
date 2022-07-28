package com.wolfeiii.emeraldplinko.game.ball;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.economy.EconomyHandler;
import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import com.wolfeiii.emeraldplinko.game.win.WinPool;
import com.wolfeiii.emeraldplinko.map.MapAxis;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class TestBallMovementHandler {

    private final PlinkoGame plinkoGame;
    private final BallEntity entity;
    private final ArmorStand stand;

    private boolean hasCollided;
    private boolean colliding;
    private int currentTick;

    private Location previousStep;


    public TestBallMovementHandler(@NotNull BallEntity ballEntity) {
        this.plinkoGame = ballEntity.getGame();
        this.stand = ballEntity.getArmorStand();
        this.entity = ballEntity;
        this.colliding = false;
    }

    public void onTick() {
        if (!shouldTick() || hasReachedBottom()) {
            return;
        }

        if (isColliding() && !isActuallyColliding()) {
            setColliding(false);
        }

        if (isFirstCollision()) {
            setColliding(true);
            setHasCollided(true);
        }

        if (!hasCollided) {
            stand.teleport(stand.getLocation().clone().subtract(0, 0.2, 0));
            return;
        }

        this.previousStep = stand.getLocation();

        Location randomLocation = null;
        while (randomLocation == null || isOutsideBoundaries(randomLocation)) {
            randomLocation = getNextRandomLocation();
        }

        moveTo(randomLocation);
    }

    public boolean isOutsideBoundaries(@NotNull Location location) {
        return !plinkoGame.getPlinkoMap().getMapCuboid().contains(location);
    }

    public boolean isFirstCollision() {
        return isActuallyColliding() && !isColliding() && !hasCollided;
    }

    public boolean shouldTick() {
        currentTick++;

        if (currentTick % 10 == 0) {
            currentTick = 0;
            return true;
        }

        return false;
    }

    public void moveTo(@NotNull Location location) {
        // TODO: Implement method to move smoothly.
        if (isLastStep(location)) {
            int moveAmount = ThreadLocalRandom.current().nextInt(1, 3);

            location = switch (plinkoGame.getPlinkoMap().getAxis()) {
                case EAST, WEST ->
                        previousStep.clone().subtract(0, 2.5, -moveAmount);
                case NORTH, SOUTH ->
                        previousStep.clone().subtract(-moveAmount, 2.5, 0);
            };

            onFinish(location);
        }

        location.getWorld().playSound(location, Sound.BLOCK_AMETHYST_BLOCK_HIT, 3f, 1f);
        teleport(location);
    }

    public void onFinish(@NotNull Location location) {
        Material material = location.clone().getBlock().getType();
        WinPool winPool = EmeraldPlinko.getInstance()
                .getWinPoolHandler()
                .getWinPool(plinkoGame.getSettings().getDifficulty(), material);

        EconomyHandler economyHandler = EmeraldPlinko.getInstance().getEconomyHandler();
        Player player = plinkoGame.getPlayer();

        if (winPool == null) {
            return;
        }

        WinPool highestWinPool = EmeraldPlinko.getInstance()
                        .getWinPoolHandler()
                        .getHighestPayingWinPool(plinkoGame.getSettings().getDifficulty());

        if (highestWinPool.equals(winPool)) {
            Location middleLocation = plinkoGame.getPlinkoMap().getMapCuboid().getCenter();
            Location behindLocation = switch (plinkoGame.getPlinkoMap().getAxis()) {
                case WEST ->
                        middleLocation.clone().add(-3, 0, 0);
                case NORTH ->
                        middleLocation.clone().add(0, 0, -3);
                case SOUTH ->
                        middleLocation.clone().add(0, 0, 3);
                case EAST ->
                        middleLocation.clone().add(3, 0, 0);
            };

            behindLocation.setY(plinkoGame.getPlinkoMap().getMapCuboid().getUpperY());
            spawnFireworks(behindLocation, 3);
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
        economyHandler.addMoney(player.getUniqueId(), winPool.getMoneyForBet(plinkoGame.getSettings().getBetPerBall()));
    }

    public static void spawnFireworks(Location location, int amount) {
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    public @NotNull Location getNextRandomLocation() {
        MapAxis mapAxis = plinkoGame.getPlinkoMap().getAxis();
        Location middleLocation = plinkoGame.getPlinkoMap().getMapCuboid().getCenter().clone();

        double pullTowardsMiddle = plinkoGame.getSettings().getDifficulty().getMiddlePull();

        boolean isOnRightSide = switch(mapAxis) {
            case WEST -> stand.getLocation().getZ() < middleLocation.getZ();
            case NORTH -> stand.getLocation().getX() < middleLocation.getX();
            case SOUTH -> stand.getLocation().getX() > middleLocation.getX();
            case EAST -> stand.getLocation().getZ() > middleLocation.getZ();
        };

        boolean inMiddle = switch(mapAxis) {
            case WEST, EAST -> stand.getLocation().getBlockZ() < middleLocation.getBlockZ();
            case NORTH, SOUTH -> stand.getLocation().getBlockX() < middleLocation.getBlockX();
        };

        boolean moveTowardsMiddle = ThreadLocalRandom.current().nextInt(0, 100) <= pullTowardsMiddle;

        if (inMiddle) {
            return switch (mapAxis) {
                case EAST, WEST ->
                        stand.getLocation().clone().add(0, -2, ThreadLocalRandom.current().nextBoolean() ? 2 : -2);
                case NORTH, SOUTH ->
                        stand.getLocation().clone().add(ThreadLocalRandom.current().nextBoolean() ? 2 : -2, -2, 0);
            };
        }

        if (moveTowardsMiddle) {
            return switch (mapAxis) {
                case WEST ->
                        stand.getLocation().clone().add(0, -2, isOnRightSide ? 2 : -2);
                case EAST ->
                        stand.getLocation().clone().add(0, -2, isOnRightSide ? -2 : 2);
                case NORTH ->
                        stand.getLocation().clone().add(isOnRightSide ? -2 : 2, -2, 0);
                case SOUTH ->
                        stand.getLocation().clone().add(isOnRightSide ? 2 : -2, -2, 0);
            };
        }

        return switch (mapAxis) {
            case EAST, WEST ->
                    stand.getLocation().subtract(0, 2, -2);
            case NORTH, SOUTH ->
                    stand.getLocation().subtract(-2, 2, 0);
        };
    }

    public boolean isLastStep(@NotNull Location location) {
        return location.getBlockY() == plinkoGame.getPlinkoMap()
                                        .getMapCuboid()
                                        .getLowerY();
    }

    public void setHasCollided(boolean hasCollided) {
        this.hasCollided = hasCollided;
    }

    // Utility Methods
    public void teleport(@NotNull Location location) {
        stand.teleport(location);
    }

    public void setColliding(boolean colliding) {
        this.colliding = colliding;
    }

    public boolean isColliding() {
        return colliding;
    }

    public boolean isActuallyColliding() {
        return stand.getEyeLocation().add(0, 0.1, 0).getBlock().getType() != Material.AIR;
    }

    public boolean isBlockInvalid(@NotNull Block block) {
        return block.getLocation().getBlockY() <= plinkoGame.getPlinkoMap().getMapCuboid().getLowerY();
    }

    public boolean hasReachedBottom() {
        return stand.getEyeLocation().getBlockY() <= plinkoGame.getPlinkoMap().getMapCuboid().getLowerY();
    }
}
