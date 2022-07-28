package com.wolfeiii.emeraldplinko.worldedit;

import com.wolfeiii.emeraldplinko.data.MapSchematic;
import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collections;
import java.util.List;

public interface WorldEditHook {

    MapFactoryCompat createMapFactoryCompat();

    MapSchematic loadMineSchematic(String name, File file, GameDifficulty difficulty);
}