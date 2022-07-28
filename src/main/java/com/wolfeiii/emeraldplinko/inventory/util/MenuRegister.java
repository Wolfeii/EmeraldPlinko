package com.wolfeiii.emeraldplinko.inventory.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuRegister {

    /**
     * A map of all registered inventory menus.
     */
    private static Map<String, PlinkoInventory> inventoryMap = new HashMap<>();

    /**
     * Gets an inventory menu for given id from the map.
     *
     * @param inventoryId The id of the inventory menu.
     *
     * @return The inventory menu or null, if not found.
     */
    public static PlinkoInventory getInventory(String inventoryId) {
        return inventoryMap.get(inventoryId);
    }

    /**
     * @return A list of all inventory menu ids.
     */
    public static List<String> getAllInventoryIds() {
        return new ArrayList<>(inventoryMap.keySet());
    }

    /**
     * Registers an inventory menu.
     *
     * @param inventory The inventory menu to register.
     */
    public static void registerInventory(PlinkoInventory inventory) {
        inventoryMap.put(inventory.getInventoryId().toLowerCase(), inventory);
    }
}
