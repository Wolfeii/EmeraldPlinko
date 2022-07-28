package com.wolfeiii.emeraldplinko.listener;

import com.wolfeiii.emeraldplinko.inventory.util.PlinkoInventoryHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof PlinkoInventoryHolder holder) {
            holder.selectMenuPoint(event);
        }
    }
}
