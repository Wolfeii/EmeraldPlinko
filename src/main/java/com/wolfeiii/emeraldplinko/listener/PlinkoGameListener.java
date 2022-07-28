package com.wolfeiii.emeraldplinko.listener;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import com.wolfeiii.emeraldplinko.game.PlinkoGameHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PlinkoGameListener implements Listener {

    private final EmeraldPlinko plinkoCore;
    private static final float MAX_LOOK_DEGREES = 70;

    public PlinkoGameListener(EmeraldPlinko plinkoCore) {
        this.plinkoCore = plinkoCore;

        // Not really a listener, but here it goes:
        plinkoCore.getServer().getScheduler().runTaskTimer(plinkoCore, () -> {
            // Get all players that currently is in a game and put them into a list.
            List<Player> playersInGame = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player -> plinkoCore.getGameHandler().isPlayerInGame(player.getUniqueId()))
                    .collect(Collectors.toList()); // .toList() -> List<? extends Player>

            for (Player player : playersInGame) {
                // Get the game the player is currently in, if not in one, just skip this player.
                PlinkoGame currentGame = plinkoCore.getGameHandler().getGame(player.getUniqueId());
                if (currentGame == null) {
                    continue;
                }

                // Get the current player yaw, and compare them to the yaw that the map is considered as "forwards"
                double playerYaw = Math.abs(player.getLocation().getYaw());
                double forwardDegrees = Math.abs(currentGame.getPlinkoMap().getAxis().getForwardAxis());

                // If the player has reached the MAX_LOOK_DEGREES (configurable?), it should block the player from
                // looking any further, by teleporting the player to the closest "acceptable" location.
                if (playerYaw > forwardDegrees + MAX_LOOK_DEGREES) {
                    Location correctLocation = player.getLocation();
                    correctLocation.setYaw((float) forwardDegrees + MAX_LOOK_DEGREES);

                    player.teleport(correctLocation);
                } else if (playerYaw < forwardDegrees - MAX_LOOK_DEGREES) {
                    Location correctLocation = player.getLocation();
                    correctLocation.setYaw((float) forwardDegrees - MAX_LOOK_DEGREES);

                    player.teleport(correctLocation);
                }
            }
        }, 1L, 1L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        // Check if the player actually moved somewhere, and is currently in a game, otherwise it isn't our business.
        if (!isInGame(event.getPlayer()) || event.getTo() == null) {
            return;
        }

        PlinkoGame game = plinkoCore.getGameHandler().getGame(event.getPlayer().getUniqueId());

        double oldX = event.getFrom().getX();
        double oldZ = event.getFrom().getZ();

        double newX = event.getTo().getX();
        double newZ = event.getTo().getZ();

        // New Y level is higher than previous one, which means the player has most likely jumped.
        if (event.getFrom().getY() < event.getTo().getY()) {
            event.setCancelled(true);
            game.createBall();
        }

        // On normal movement, backwards, forwards, left or right. Just cancel.
        if (oldX != newX || oldZ != newZ) {
            event.setCancelled(true);
        }
    }

    /**
     * Shortcut method to check if the player is currently in a game.
     *
     * @param player the player to check
     * @return true / false if the player is currently in a game.
     */
    private boolean isInGame(Player player) {
        return plinkoCore.getGameHandler().isPlayerInGame(player.getUniqueId());
    }
}
