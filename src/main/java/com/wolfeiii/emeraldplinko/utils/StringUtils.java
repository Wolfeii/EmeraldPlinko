package com.wolfeiii.emeraldplinko.utils;

import org.bukkit.ChatColor;

public class StringUtils {

    public static String translate(String textToTranslate) {
        return ChatColor.translateAlternateColorCodes('&', textToTranslate);
    }


}
