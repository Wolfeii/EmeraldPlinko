package com.wolfeiii.emeraldplinko.game.ball;

import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import com.wolfeiii.emeraldplinko.map.MapAxis;
import com.wolfeiii.emeraldplinko.utils.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

import java.util.List;

public class BallMovementHandler {

    private final BallEntity ballEntity;
    private final ArmorStand armorStand;

    public Double desiredX;
    public Double desiredZ;

    private boolean colliding;

    // Current Movement
    private MoveDirection direction;
    private int ticksSinceCollision;
    public Double stepSize;

    public BallMovementHandler(BallEntity entity) {
        this.armorStand = entity.getArmorStand();
        this.ballEntity = entity;
    }

    public void onTick() {

        // * Titta vart Armorstandet träffar blocket
        // * Kalkylera vart vi vill beroende på vart vi träffar
        // * Räkna ut hur mycket vi behöver flytta oss varje tick för att komma dit på 10 ticks.
        // * Teleportera den mängden varje tick tills vi är framme.

        // We have reached the bottom, no need to keep moving.
        if (ballEntity.hasReachedBottom()) {
            return;
        }

        // We've encountered a block that we didn't collide with last tick.
        if (isFirstCollision()) {
            Location armorStandLocation = armorStand.getLocation().clone();
            double collideAmount = switch (ballEntity.getGame().getPlinkoMap().getAxis()) {
                case WEST, EAST ->
                        Math.abs(Math.abs(armorStandLocation.getZ()) - Math.abs(armorStandLocation.getBlockZ()));
                case NORTH, SOUTH ->
                        Math.abs(Math.abs(armorStandLocation.getX()) - Math.abs(armorStandLocation.getBlockX()));
            };

            MoveDirection direction = getDirectionByCollision(armorStandLocation); // WEST > LEFT
            Location newLocation = direction.applyTo(ballEntity.getGame(), armorStandLocation, armorStandLocation.getZ() + (collideAmount * 2));

            double stepSizePerTick = newLocation.distance(armorStandLocation) / 10;

            this.direction = direction;
            this.stepSize = stepSizePerTick;
            this.ticksSinceCollision = 0;
        }

        if (!isColliding()) {
            if (stepSize != null) ticksSinceCollision++;
            if (colliding) setColliding(false);
        }

        if (ticksSinceCollision == 8) {
            this.stepSize = null;
            this.ticksSinceCollision = 0;
            this.direction = null;
        }



        Location armorStandLocation;
        if (direction != null && stepSize != null) armorStandLocation = direction.applyTo(ballEntity.getGame(), armorStand.getLocation(), stepSize).subtract(0, 0.2, 0);
        else armorStandLocation = armorStand.getLocation().subtract(0, 0.2, 0);
        armorStand.teleport(armorStandLocation);

        /*
        Bukkit.broadcastMessage("DesiredX is: " + (desiredX == null ? "NULL" : desiredX.doubleValue()));
        Bukkit.broadcastMessage("DesiredZ is: " + (desiredZ == null ? "NULL" : desiredZ.doubleValue()));

        armorStand.setGlowing(isColliding());

        if (isColliding() && !colliding) {
            setColliding(true);

            // Code when first colliding with an object.

            Location headLocation = armorStand.getLocation()
                    .clone()
                    .subtract(0, 0.2, 0);

            double collidingAmount = headLocation.getBlockX() + 1 + (headLocation.getX() - headLocation.getBlockX() * 2);
            MoveDirection direction = getDirectionByCollision(armorStand.getLocation());
            Location newLocation = direction.applyTo(ballEntity.getGame(), headLocation, collidingAmount);

            this.desiredX = desiredX != null ? desiredX + newLocation.getX() : newLocation.getX();
            this.desiredZ = desiredZ != null ? desiredZ + newLocation.getZ() : newLocation.getZ();
        } else {
            setColliding(false);
        }

        if (isMovingTowardsX() && IntegerUtils.isClose(armorStand.getLocation().getX(), desiredX, .1)) {
            this.desiredX = null;
        }

        if (isMovingTowardsZ() && IntegerUtils.isClose(armorStand.getLocation().getZ(), desiredZ, .1)) {
            this.desiredZ = null;
        }

        if (this.desiredZ != null) {
            if (this.stepSize == null) {
                boolean isNegative = desiredZ < 0;
                this.stepSize = Math.abs(Math.max(armorStand.getLocation().getZ(), desiredZ)) - Math.abs(Math.min(armorStand.getLocation().getZ(), desiredZ)) / 10;
                if (isNegative) stepSize = -stepSize;
            }
        }

        if (this.desiredX != null) {
            if (this.stepSize == null) {
                boolean isNegative = desiredX < 0;
                this.stepSize = Math.abs(Math.max(armorStand.getLocation().getX(), desiredX)) - Math.abs(Math.min(armorStand.getLocation().getX(), desiredX)) / 10;
                if (isNegative) stepSize = -stepSize;
            }
        }

        boolean shouldTeleportX = desiredX != null;
        armorStand.teleport(armorStand.getLocation().clone().add(shouldTeleportX && stepSize != null ? stepSize : 0, -0.2, !shouldTeleportX && stepSize != null ? stepSize : 0));

         */
    }

    public boolean isFirstCollision() {
        boolean isColliding = isColliding() && !this.colliding;
        if (isColliding) setColliding(true);
        return isColliding;
    }

    public void moveTowards(Location location) {
        this.desiredX = NumberUtils.isClose(location.getX(), armorStand.getLocation().getX(), .1) ? null : location.getX();
        this.desiredZ = NumberUtils.isClose(location.getZ(), armorStand.getLocation().getZ(), .1) ? null : location.getZ();
    }

    public boolean isMovingTowardsX() {
        return this.desiredX != null && stepSize != null;
    }

    public boolean isMovingTowardsZ() {
        return this.desiredZ != null && stepSize != null;
    }

    public boolean isSemiColliding() {
        for (Block block : getNearbyBlocks()) {
            if (block.getBoundingBox().overlaps(armorStand.getBoundingBox().expand(0.2, -0.4, 0.2))) {
                return true;
            }
        }

        return false;
    }

    public List<Block> getNearbyBlocks() {
        return switch(ballEntity.getGame().getPlinkoMap().getAxis()) {
            case EAST, WEST ->
                    List.of(armorStand.getLocation().add(0, 0, 1).getBlock(), armorStand.getLocation().add(0, 0, -1).getBlock());
            case SOUTH, NORTH ->
                    List.of(armorStand.getLocation().add(1, 0, 0).getBlock(), armorStand.getLocation().add(-1, 0, 0).getBlock());
        };
    }

    public void setColliding(boolean colliding) {
        this.colliding = colliding;
    }

    public boolean isColliding() {
        Location headLocation = armorStand.getLocation()
                .clone()
                .subtract(0, 0, 0);

        Block block = headLocation.getBlock();
        return block.getType() != Material.AIR;
    }

    public MoveDirection getDirectionByCollision(Location originalLocation) {
        MapAxis axis = ballEntity.getGame().getPlinkoMap().getAxis();
        return switch (axis) {
            case WEST ->
                    originalLocation.getZ() - originalLocation.getBlockZ() >= .500 ? MoveDirection.LEFT : MoveDirection.RIGHT;

            case NORTH ->
                    originalLocation.getX() - originalLocation.getBlockX() >= .500 ? MoveDirection.LEFT : MoveDirection.RIGHT;

            case SOUTH ->
                    originalLocation.getX() - originalLocation.getBlockX() >= .500 ? MoveDirection.RIGHT : MoveDirection.LEFT;

            case EAST ->
                    originalLocation.getZ() - originalLocation.getBlockZ() >= .500 ? MoveDirection.RIGHT : MoveDirection.LEFT;
        };
    }

    public enum MoveDirection {

        RIGHT,
        LEFT;

        public Location applyTo(PlinkoGame plinkoGame, Location location, double amount) {
            MapAxis axis = plinkoGame.getPlinkoMap().getAxis();
            return switch (axis) {
                case WEST ->
                        location.clone().add(0, 0, isRight() ? -amount : amount);
                case NORTH ->
                        location.clone().add(isRight() ? amount : -amount, 0, 0);
                case EAST ->
                        location.clone().add(0, 0, isRight() ? amount : -amount);
                case SOUTH ->
                        location.clone().add(isRight() ? -amount : amount, 0 ,0);
            };
        }

        public boolean isRight() {
            return this.equals(RIGHT);
        }
    }
}
