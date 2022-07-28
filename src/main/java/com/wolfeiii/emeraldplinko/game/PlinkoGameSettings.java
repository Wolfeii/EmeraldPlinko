package com.wolfeiii.emeraldplinko.game;

import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class PlinkoGameSettings {

    @Getter @Setter
    private int betPerBall;

    @Getter @Setter
    private GameDifficulty difficulty;

    private final Inventory settingsInventory;

    public PlinkoGameSettings() {
        this.betPerBall = 100;
        this.difficulty = GameDifficulty.EASY;

        this.settingsInventory = Bukkit.createInventory(null, 36, "Change settings");
        populateInventory();
    }

    private void populateInventory() {
        if (!settingsInventory.isEmpty()) {
            settingsInventory.clear();
        }



    }

    public void openSettings(@NotNull Player player) {
        player.openInventory(settingsInventory);
    }

}
