package com.wolfeiii.emeraldplinko.game.ball;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

public class BallEntity {

    // Movement Handling (not beautiful)
    private final static double DECELERATION_RATE = 0.98D;
    private final static double GRAVITY_CONSTANT = 0.08D;
    private final static double TELEPORTS_AMOUNT = 10;

    private TestBallMovementHandler handler;
    // private BallMovementHandler handler;

    @Getter
    private final PlinkoGame game;

    @Getter
    private ArmorStand armorStand;
    private Location location;

    public BallEntity(@NotNull PlinkoGame game, Location location) {
        this(game, location, false);
    }

    public BallEntity(@NotNull PlinkoGame game, Location location, boolean spawnNow) {
        this.game = game;
        this.location = location;
        if (spawnNow) this.armorStand = spawnEntity();
        this.handler = new TestBallMovementHandler(this);
    }

    public void onTick() {
        if (!isSpawned()) {
            return;
        }

        this.handler.onTick();
    }

    public boolean hasReachedBottom() {
        return armorStand.getEyeLocation().getBlockY() <= game.getPlinkoMap().getMapCuboid().getLowerY() - 1;
    }

    public boolean isSpawned() {
        return armorStand != null && armorStand.isValid();
    }

    @CanIgnoreReturnValue
    public ArmorStand spawnEntity() {
        if (isSpawned() || location.getWorld() == null) {
            return null;
        }

        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        return applySettings(armorStand);
    }

    public ArmorStand applySettings(ArmorStand armorStand) {
        armorStand.getEquipment().setHelmet(new ItemStack(Material.LIGHT_GRAY_CONCRETE));
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        return armorStand;
    }

    public void setOrientation(double newDegrees) {
        if (!isSpawned()) {
            throw new IllegalStateException("Tried to set orientation, but ArmorStand hasn't spawned yet.");
        }

        EulerAngle headPoseAngle = armorStand.getHeadPose();
        headPoseAngle = headPoseAngle.setX(Math.toRadians(newDegrees));
        armorStand.setHeadPose(headPoseAngle);
    }

    public double getOrientation() {
        if (!isSpawned()) {
            throw new IllegalStateException("Tried to set orientation, but ArmorStand hasn't spawned yet.");
        }

        EulerAngle headPoseAngle = armorStand.getHeadPose();
        return Math.toDegrees(headPoseAngle.getX());
    }
}
