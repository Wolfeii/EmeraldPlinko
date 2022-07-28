package com.wolfeiii.emeraldplinko.inventory.util;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PlinkoInventoryHolder implements InventoryHolder {

    /**
     * An inventory that can be used as menu or for other custom interaction mechanics.
     */
    private PlinkoInventory inventory;

    /**
     * Creates a new inventory holder for the given custom inventory.
     *
     * @param inventory The custom inventory.
     */
    public PlinkoInventoryHolder(PlinkoInventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Get the object's inventory.
     *
     * @return The inventory.
     */
    @Override
    @Deprecated
    public Inventory getInventory() {
        return null;
    }

    /**
     * Gets the name of the custom inventory inside this holder.
     *
     * @return The name of the inventory class.
     */
    public String getInventoryName() {
        return inventory.getClass().getName();
    }

    /**
     * Checks if an event in this inventory was triggered by a player click.
     *
     * @param event The inventory click event.
     */
    public void selectMenuPoint(InventoryClickEvent event) {
        inventory.selectMenuPoint(event);
    }

    /**
     * Cleans up everything, so the inventory can be closed.
     *
     * @param event The inventory close event.
     */
    public void destroyInventory(InventoryCloseEvent event) {
        inventory.destroyInventory(event);
    }
}
