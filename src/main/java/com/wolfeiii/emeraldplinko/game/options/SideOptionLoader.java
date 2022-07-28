package com.wolfeiii.emeraldplinko.game.options;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.data.MapSchematic;
import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;
import com.wolfeiii.emeraldplinko.game.objects.PlinkoMap;
import com.wolfeiii.emeraldplinko.game.options.objects.ActiveSideOption;
import com.wolfeiii.emeraldplinko.utils.DoubleInteger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.*;

public class SideOptionLoader {

    private final EmeraldPlinko plinkoCore;
    private final Map<GameDifficulty, List<SideOption>> sideOptions = new HashMap<>();

    public SideOptionLoader(EmeraldPlinko plinkoCore) {
        this.plinkoCore = plinkoCore;
    }

    public void loadSideOptions() {
        sideOptions.clear();

        ConfigurationSection difficultySection = plinkoCore.getConfig().getConfigurationSection("difficulty");
        if (difficultySection != null && difficultySection.getKeys(false).size() != 0) {

            // Loop through all difficulties
            for (String difficultyName : difficultySection.getKeys(false)) {
                ConfigurationSection currentSection = difficultySection.getConfigurationSection(difficultyName);
                if (currentSection == null) {
                    continue;
                }

                List<SideOption> difficultySideOptions = new ArrayList<>();
                GameDifficulty difficulty = GameDifficulty.valueOf(difficultyName.toUpperCase());

                ConfigurationSection sideOptionsSection = currentSection.getConfigurationSection("side-options");
                for (String sideOption : sideOptionsSection.getKeys(false)) {
                    if (sideOption.equalsIgnoreCase("settings")) {
                        continue;
                    }

                    boolean firstIndex = new ArrayList<>(sideOptionsSection.getKeys(false)).get(1).equalsIgnoreCase(sideOption);

                    // Extra Settings
                    SideOption.SideOptionType type = SideOption.SideOptionType.valueOf(sideOption.toUpperCase());
                    String title = sideOptionsSection.getString(sideOption + ".title");
                    List<String> description = sideOptionsSection.getStringList(sideOption + ".description");
                    int distance = sideOptionsSection.getInt(sideOption + ".distance");

                    // Add SideOption
                    difficultySideOptions.add(new SideOption(type, title, description, distance, firstIndex ? 1 : 2));
                }

                sideOptions.put(difficulty, difficultySideOptions);
            }
        }

        plinkoCore.getLogger().info("Loaded " + sideOptions.size() + " side options!");
    }

    public List<SideOption> getSideOptions(GameDifficulty difficulty) {
        return sideOptions.get(difficulty);
    }

    public void createSideOptions(PlinkoGame game, GameDifficulty difficulty) {
        List<SideOption> sideOptions = getSideOptions(difficulty);

        for (SideOption sideOption : sideOptions) {
            ActiveSideOption activeSideOption = new ActiveSideOption(game, sideOption);
            game.getPlinkoMap().addSideOption(activeSideOption, activeSideOption.getLocation());
        }
    }

    public enum SideOptionDistance {
        WEST(-1, 1, -1, -1),
        SOUTH(1, 1, -1, 1),
        EAST(1, -1, 1, 1),
        NORTH(-1, -1, 1, -1);

        private int xDifference;
        private int zDifference;

        private int xDifferenceTwo;
        private int zDifferenceTwo;

        SideOptionDistance(int xDifference, int zDifference, int xDifferenceTwo, int zDifferenceTwo) {
            this.xDifference = xDifference;
            this.zDifference = zDifference;
            this.xDifferenceTwo = xDifferenceTwo;
            this.zDifferenceTwo = zDifferenceTwo;
        }

        public DoubleInteger getCoordinateSet(int index, int distance) {
            return new DoubleInteger(index == 1 ? xDifference * distance : xDifferenceTwo * distance,
                                    index == 1 ? zDifference * distance : zDifferenceTwo * distance);
        }

    }
}
