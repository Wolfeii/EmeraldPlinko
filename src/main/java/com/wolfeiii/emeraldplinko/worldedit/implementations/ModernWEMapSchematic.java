package com.wolfeiii.emeraldplinko.worldedit.implementations;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.wolfeiii.emeraldplinko.data.MapSchematic;
import com.wolfeiii.emeraldplinko.game.objects.GameDifficulty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ModernWEMapSchematic extends MapSchematic {

    protected ModernWEMapSchematic(String name, File file, GameDifficulty difficulty) {
        super(name, difficulty, file);
    }

    @Override
    public Clipboard getSchematic() {
        final ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format != null) {
            try (final ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                return reader.read();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}