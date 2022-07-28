package com.wolfeiii.emeraldplinko.game.win;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;
import com.wolfeiii.emeraldplinko.game.options.SideOption;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class WinPoolHandler {

    private final EmeraldPlinko plinkoCore;
    private final Map<GameDifficulty, List<WinPool>> winPools = new HashMap<>();

    public WinPoolHandler(EmeraldPlinko plinkoCore) {
        this.plinkoCore = plinkoCore;
    }

    public void loadWinPools() {
        winPools.clear();

        ConfigurationSection difficultySection = plinkoCore.getConfig().getConfigurationSection("difficulty");
        if (difficultySection != null && difficultySection.getKeys(false).size() != 0) {

            // Loop through all difficulties
            for (String difficultyName : difficultySection.getKeys(false)) {
                ConfigurationSection currentSection = difficultySection.getConfigurationSection(difficultyName);
                if (currentSection == null) {
                    continue;
                }

                List<WinPool> difficultyWinPools = new ArrayList<>();
                GameDifficulty difficulty = GameDifficulty.valueOf(difficultyName.toUpperCase());

                ConfigurationSection winPoolsSection = currentSection.getConfigurationSection("win-pools");
                for (String poolMaterial : winPoolsSection.getKeys(false)) {
                    ConfigurationSection poolSection = winPoolsSection.getConfigurationSection(poolMaterial);

                    Material material = Material.getMaterial(poolMaterial);
                    if (material == null) continue;

                    double winMultiplier = poolSection.getDouble("win-multiplier", 1);
                    String particle = poolSection.getString("particle", "");

                    difficultyWinPools.add(new WinPool(material, difficulty, particle, winMultiplier));
                }

                winPools.put(difficulty, difficultyWinPools);
            }
        }

        plinkoCore.getLogger().info("Loaded " + winPools.size() + " Win Pools!");
    }

    @Nullable
    public WinPool getWinPool(@NotNull GameDifficulty difficulty, @NotNull Material material) {
        return winPools.get(difficulty).stream()
                .filter(winPool -> winPool.getMaterial().equals(material))
                .findFirst()
                .orElse(null);
    }

    public WinPool getHighestPayingWinPool(@NotNull GameDifficulty difficulty) {
         List<WinPool> pool = winPools.get(difficulty).stream()
                .sorted(Comparator.comparingDouble(WinPool::getWinMultiplier))
                .toList();
         return pool.get(pool.size() - 1);
    }
}
