package com.wolfeiii.emeraldplinko.data;

import com.wolfeiii.emeraldplinko.EmeraldPlinko;
import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SchematicHandler {

    private final EmeraldPlinko plinkoCore;

    private final Map<GameDifficulty, MapSchematic> schematics = new HashMap<>();

    private final File schematicDirectory = new File(EmeraldPlinko.getInstance().getDataFolder(), "schematics");

    public SchematicHandler(EmeraldPlinko plinkoCore) {
        this.plinkoCore = plinkoCore;
    }

    public void loadAllSchematics() {
        schematics.clear();

        ConfigurationSection difficultySection = plinkoCore.getConfig().getConfigurationSection("difficulty");
        if (difficultySection != null && difficultySection.getKeys(false).size() != 0) {

            // Loop through all difficulties
            for (String difficultyName : difficultySection.getKeys(false)) {
                ConfigurationSection currentSection = difficultySection.getConfigurationSection(difficultyName);
                if (currentSection == null) {
                    continue;
                }

                GameDifficulty difficulty = GameDifficulty.valueOf(difficultyName.toUpperCase());
                String fileName = currentSection.getString("schematic");
                File schematicFile = new File(schematicDirectory, Objects.requireNonNull(fileName));

                MapSchematic schematic = plinkoCore.getWorldEditHook().loadMineSchematic(fileName.split("\\.")[0], schematicFile, difficulty);
                schematics.put(difficulty, schematic);
            }
        }

        plinkoCore.getLogger().info("Loaded " + schematics.size() + " schematics!");
    }

    public MapSchematic getSchematic(@NotNull GameDifficulty difficulty) {
        return this.schematics.get(difficulty);
    }
}
