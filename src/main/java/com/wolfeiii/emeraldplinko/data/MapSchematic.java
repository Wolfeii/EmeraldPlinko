package com.wolfeiii.emeraldplinko.data;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Objects;

public abstract class MapSchematic {

    private final String name;
    private final GameDifficulty difficulty;
    protected final File file;

    protected MapSchematic(String name, GameDifficulty difficulty, File file) {
        this.name = name;
        this.difficulty = difficulty;
        this.file = file;
    }

    public abstract Clipboard getSchematic();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapSchematic)) return false;
        MapSchematic that = (MapSchematic) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getFile(), that.getFile()) &&
                Objects.equals(getDifficulty(), that.getDifficulty());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getFile());
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public GameDifficulty getDifficulty() {
        return difficulty;
    }
}