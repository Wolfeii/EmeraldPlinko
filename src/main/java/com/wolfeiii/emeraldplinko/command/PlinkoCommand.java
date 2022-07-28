package com.wolfeiii.emeraldplinko.command;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.inventory.PlinkoMainMenu;
import com.wolfeiii.emeraldplinko.utils.StringUtils;
import org.bukkit.Effect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PlinkoCommand implements CommandExecutor {

    private final EmeraldPlinko plinkoCore;

    public PlinkoCommand(EmeraldPlinko plinkoCore) {
        this.plinkoCore = plinkoCore;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringUtils.translate("&cYou need to be a player to execute this command."));
            return false;
        }

        if (args.length == 0) {
            if (isInGame(player)) {
                player.sendMessage(StringUtils.translate("&cYou are already in a game."));
            } else {
                handleTeleport(player);
            }

            return false;
        }

        String argument = args[0];
        switch (argument) {
            case "quit" -> {
                if (!isInGame(player)) {
                    player.sendMessage(StringUtils.translate("&cYou are not in a game right now."));
                    return false;
                }

                plinkoCore.getGameHandler().clearGame(player.getUniqueId());
                return false;
            }

            case "start" -> {
                if (isInGame(player)) {
                    player.sendMessage(StringUtils.translate("&cYou are already in a game."));
                } else {
                    plinkoCore.getGameHandler().createGame(player);
                }

                return false;
            }

            default -> {
                player.sendMessage(StringUtils.translate("Invalid argument."));
                return false;
            }
        }
    }

    public void handleTeleport(@NotNull Player player) {
        player.playEffect(player.getLocation(), Effect.PORTAL_TRAVEL, null);
        player.sendTitle("", StringUtils.translate("&9Teleporting..."), 10, 20, 10);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 10));
        plinkoCore.getGameHandler().createGame(player);
    }

    public boolean isInGame(@NotNull Player player) {
        return plinkoCore.getGameHandler().isPlayerInGame(player.getUniqueId());
    }
}
