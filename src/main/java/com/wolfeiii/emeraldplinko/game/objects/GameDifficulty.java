package com.wolfeiii.emeraldplinko.game.objects;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public enum GameDifficulty {

    EASY(),
    MEDIUM(),
    HARD();

    public double getWinMultiplier() {
        return getConfigurationSection().getDouble("win-multiplier");
    }

    public double getMiddlePull() {
        return getConfigurationSection().getDouble("pull-middle");
    }

    public Material getDotMaterial() {
        return Material.getMaterial(getConfigurationSection().getString("block"));
    }

    public double getYawDifference() {
        return getConfigurationSection().getDouble("side-options.settings.yaw-difference");
    }

    public double getYawRange() {
        return getConfigurationSection().getDouble("side-options.settings.yaw-range");
    }

    private ConfigurationSection getConfigurationSection() {
        return EmeraldPlinko.getInstance()
                .getConfig()
                .getConfigurationSection("difficulty." + name().toLowerCase());
    }
}
