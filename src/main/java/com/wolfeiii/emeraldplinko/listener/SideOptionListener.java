package com.wolfeiii.emeraldplinko.listener;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import com.wolfeiii.emeraldplinko.game.options.objects.ActiveSideOption;
import com.wolfeiii.emeraldplinko.utils.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SideOptionListener implements Listener {

    private static final float MAX_LOOK_DEGREES = 70;

    private final EmeraldPlinko plinkoCore;

    public SideOptionListener(EmeraldPlinko plinkoCore) {
        this.plinkoCore = plinkoCore;

        plinkoCore.getServer().getScheduler().runTaskTimer(plinkoCore, () -> {
            List<? extends Player> applicablePlayers = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(player -> plinkoCore.getGameHandler().isPlayerInGame(player.getUniqueId()))
                    .toList();

            for (Player player : applicablePlayers) {
                PlinkoGame game = plinkoCore
                        .getGameHandler()
                        .getGame(player.getUniqueId());

                List<ActiveSideOption> sideOptions = game.getPlinkoMap().getSideOptions();
                double playerYaw = Math.abs(player.getLocation().getYaw());
                double yawDifference = game.getSettings().getDifficulty().getYawDifference();
                double yawRange = game.getSettings().getDifficulty().getYawRange();
                double forwardDegrees = Math.abs(game.getPlinkoMap().getAxis().getForwardAxis());

                if (NumberUtils.isClose((forwardDegrees + MAX_LOOK_DEGREES) - yawDifference, playerYaw, yawRange)) {
                    sideOptions.get(1).setForward(game, true);
                } else if (NumberUtils.isClose((forwardDegrees - MAX_LOOK_DEGREES) + yawDifference, playerYaw, yawRange)) {
                    sideOptions.get(0).setForward(game, true);
                } else {
                    for (ActiveSideOption sideOption : sideOptions) {
                        sideOption.setForward(game, false);
                    }
                }
            }
        }, 0, 1L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerInteractEvent event) {
        PlinkoGame plinkoGame = plinkoCore.getGameHandler().getGame(event.getPlayer().getUniqueId());

        // If player is currently in a game, and has currently selected one of the side options.
        if (plinkoGame != null && plinkoGame.getSelected() != null
            && (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.LEFT_CLICK_AIR))) {

            // Execute the on click task for this SideOption.
            plinkoCore.getServer().getScheduler().runTask(plinkoCore,
                    plinkoGame.getSelected().onClick(event.getPlayer(),
                            plinkoGame));
        }
    }
}
