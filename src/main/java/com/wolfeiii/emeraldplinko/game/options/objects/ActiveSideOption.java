package com.wolfeiii.emeraldplinko.game.options.objects;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import com.wolfeiii.emeraldplinko.game.options.SideOption;
import com.wolfeiii.emeraldplinko.game.options.SideOptionLoader;
import com.wolfeiii.emeraldplinko.utils.DoubleInteger;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.actions.ClickType;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class ActiveSideOption extends SideOption {

    private final SideOption sideOption;
    private final PlinkoGame plinkoGame;

    private boolean moving;
    private boolean forward;
    private String hologramName;
    private Location location;
    private Hologram hologram;

    public ActiveSideOption(@NotNull PlinkoGame plinkoGame, @NonNull SideOption sideOption) {
        super(sideOption.getType(), sideOption.getTitle(), sideOption.getDescription(), sideOption.getDistance(), sideOption.getSideOptionIndex());
        setYawOptions(sideOption.getYawDifference(), sideOption.getYawRange());
        this.sideOption = sideOption;

        this.plinkoGame = plinkoGame;
        this.forward = false;
        this.moving = false;

        DoubleInteger coordinateSet = SideOptionLoader.SideOptionDistance.valueOf(plinkoGame.getPlinkoMap().getAxis().name())
                .getCoordinateSet(getSideOptionIndex(), sideOption.getDistance());

        this.location = plinkoGame.getPlinkoMap().getPlayerLocation().clone()
                .add(coordinateSet.getIntegerOne(), 2, coordinateSet.getIntegerTwo());
        this.location.setY(this.location.getBlockY());
        this.hologramName = "sideoption-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(10000);

        this.hologram = DHAPI.createHologram(hologramName, location);
        DHAPI.addHologramLine(hologram, getTitle());
        getDescription().forEach(s -> DHAPI.addHologramLine(hologram, s));
        Bukkit.getLogger().info(String.format("Creating Hologram at %s, %s, %s", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public void setForward(PlinkoGame plinkoGame, boolean forward) {
        if (forward == this.forward) return;

        DoubleInteger newLocationRaw;
        if (forward) {
            newLocationRaw = SideOptionLoader.SideOptionDistance
                    .valueOf(plinkoGame.getPlinkoMap().getAxis().name())
                    .getCoordinateSet(sideOption.getSideOptionIndex(), 2);

            Location newLocation = new Location(hologram.getLocation().getWorld(),
                    plinkoGame.getPlinkoMap().getPlayerLocation().getX() + newLocationRaw.getIntegerOne(),
                    hologram.getLocation().getBlockY(),
                    plinkoGame.getPlinkoMap().getPlayerLocation().getZ() + newLocationRaw.getIntegerTwo());


            moveTo(newLocation);
        } else {
            newLocationRaw = SideOptionLoader.SideOptionDistance
                    .valueOf(plinkoGame.getPlinkoMap().getAxis().name())
                    .getCoordinateSet(sideOption.getSideOptionIndex(), 3);

            Location newLocation = new Location(hologram.getLocation().getWorld(),
                    plinkoGame.getPlinkoMap().getPlayerLocation().getX() + newLocationRaw.getIntegerOne(),
                    hologram.getLocation().getBlockY(),
                    plinkoGame.getPlinkoMap().getPlayerLocation().getZ() + newLocationRaw.getIntegerTwo());

            moveTo(newLocation);
        }
        hologram.updateAll();

        this.forward = forward;
    }

    public void moveTo(@NotNull Location location) {
        /*

        double xOld = hologram.getLocation().getX();
        double zOld = hologram.getLocation().getZ();

        double xNew = location.getX();
        double zNew = location.getZ();

        double xDifference = xOld - xNew;
        double zDifference = zOld - zNew;

        this.moving = true;

        BukkitTask task = new BukkitRunnable() {

            private int currentTick = 0;

            @Override
            public void run() {
                if (currentTick == 5) {
                    moving = false;
                    cancel();
                    return;
                }

                currentTick++;

                double stepSizeX = xDifference / 5;
                double stepSizeZ = zDifference / 5;

                Bukkit.getLogger().info(String.format("Moving with StepSize X: %s, Z: %s... (%s, %s)", stepSizeX, stepSizeZ, hologram.getLocation().add(stepSizeX, 0, stepSizeZ).getX(), hologram.getLocation().add(stepSizeX, 0, stepSizeZ).getZ()));

                hologram.setLocation(hologram.getLocation().add(stepSizeX, 0, stepSizeZ));
            }
        }.runTaskTimer(EmeraldPlinko.getInstance(), 0L, 2L);

         */

        DHAPI.moveHologram(hologram, location);
    }

    public void onDisable() {
        DHAPI.removeHologram(hologramName);
    }

    public boolean isForward() {
        return forward;
    }

}
