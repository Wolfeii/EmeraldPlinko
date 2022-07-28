package com.wolfeiii.emeraldplinko.game;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.economy.EconomyHandler;
import com.wolfeiii.emeraldplinko.game.ball.BallEntity;
import com.wolfeiii.emeraldplinko.game.objects.PlinkoMap;
import com.wolfeiii.emeraldplinko.game.options.SideOption;
import com.wolfeiii.emeraldplinko.game.options.objects.ActiveSideOption;
import com.wolfeiii.emeraldplinko.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlinkoGame {

    private final Player player;
    private PlinkoMap plinkoMap;

    private PlinkoGameSettings settings;
    private boolean started;

    private List<BallEntity> ballEntities;

    public PlinkoGame(Player player) {
        this.player = player;
        this.settings = new PlinkoGameSettings();
        this.started = false;
        this.ballEntities = new ArrayList<>();

        Bukkit.getScheduler().runTaskTimer(EmeraldPlinko.getInstance(), () -> {
            for (BallEntity ball : ballEntities) {
                ball.onTick();
            }
        }, 1L, 1L);
    }

    /**
     * Create a ball entity, which spawns the ArmorStand, and adds the entity to the List<BallEntity> list.
     * This makes sure that the player can afford to place the bet as well.
     */
    public void createBall() {
        if (!hasStarted()) {
            start();
        }

        if (!createBallPayment()) return;

        // Calculate Location for the newly spawned ball
        Location spawnLocation = plinkoMap.getMapCuboid().getCenter().clone();
        spawnLocation.setY(plinkoMap.getMapCuboid().getUpperY());


        // Spawn the Ball Entity
        BallEntity plinkoBall = new BallEntity(this, spawnLocation, true);
        ballEntities.add(plinkoBall);
    }

    public boolean createBallPayment() {
        EconomyHandler economyHandler = EmeraldPlinko.getInstance().getEconomyHandler();

        // Check if we have enough money to make this purchase.
        int costPerBall = this.getSettings().getBetPerBall();
        if (economyHandler.getMoney(player.getUniqueId()) < costPerBall) {
            player.sendMessage(StringUtils.translate("&cSorry, you can't afford to place this bet."));
            return false;
        }

        // Remove the cost of the ball.
        economyHandler.buy(player.getUniqueId(), costPerBall);
        return true;
    }

    public void unregister() {
        EmeraldPlinko.getInstance().getGameHandler().clearGame(player.getUniqueId());
    }

    /**
     * Gets the selected SideOption by the player, null if none is found.
     *
     * @return the selected SideOption
     */
    public SideOption getSelected() {
        return plinkoMap.getSideOptions().stream().filter(ActiveSideOption::isForward).findFirst().orElse(null);
    }

    /**
     * Clear the balls from the map, to make sure ArmorStands get removed, and inactive balls don't get ticked.
     *
     * @param shouldPayBack if we should pay back the cost of the balls that didn't make it.
     */
    public void clearBalls(boolean shouldPayBack) {
        for (BallEntity ballEntity : ballEntities) {
            if (shouldPayBack && !ballEntity.hasReachedBottom()) {

                // Get the player
                Player player = ballEntity.getGame().getPlayer();
                if (player == null) {
                    continue;
                }

                // Add the cost for this ball to the player, as it didn't make it down.
                EmeraldPlinko.getInstance().getEconomyHandler()
                        .addMoney(player.getUniqueId(),
                                settings.getBetPerBall());
            }

            ballEntity.getArmorStand().remove();
        }

        ballEntities.clear();
    }

    public void start() {
        this.started = true;
    }

    public boolean hasStarted() {
        return started;
    }

    public void setPlinkoMap(PlinkoMap plinkoMap) {
        this.plinkoMap = plinkoMap;
    }

}
