package com.wolfeiii.emeraldplinko.inventory.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuUtils {

    public static void setBorders(Inventory menu) {
        ItemStack borderItemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderItemMeta = borderItemStack.getItemMeta();
        borderItemMeta.setDisplayName(" ");
        borderItemStack.setItemMeta(borderItemMeta);

        for(int slot = 0; slot < menu.getSize(); slot++) {
            if(menu.getItem(slot) == null)
                menu.setItem(slot, borderItemStack);
        }
    }
}
