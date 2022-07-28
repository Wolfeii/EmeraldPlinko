package com.wolfeiii.emeraldplinko.inventory;

import com.wolfeiii.emeraldplinko.inventory.util.MenuUtils;
import com.wolfeiii.emeraldplinko.inventory.util.PlinkoInventory;
import com.wolfeiii.emeraldplinko.inventory.util.PlinkoInventoryHolder;
import com.wolfeiii.emeraldplinko.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlinkoMainMenu implements PlinkoInventory {
    @Override
    public String getInventoryId() {
        return "main-menu";
    }

    public static void open(Player player) {
        PlinkoMainMenu plinkoMainMenu = new PlinkoMainMenu();
        Inventory menu = Bukkit.createInventory(new PlinkoInventoryHolder(plinkoMainMenu), 36, "Plinko");
        MenuUtils.setBorders(menu);

        ItemStack infoItemStack = new ItemStack(Material.ENCHANTED_BOOK);

        menu.setItem(14, infoItemStack);

        player.openInventory(menu);
    }

    @Override
    public void selectMenuPoint(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
