package com.wolfeiii.emeraldplinko.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ItemStackUtils {

    /**
     * Serializes an ItemStack to a Base64 encoded String.
     * Returns an empty String if an error occurs.
     *
     * @param itemStack The ItemStack which should be serialized
     * @return The Base64 encoded String representation of this ItemStack
     */
    public static String serialize(ItemStack itemStack) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);
            bukkitObjectOutputStream.writeObject(itemStack);
            bukkitObjectOutputStream.flush();

            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Deserializes an ItemStack from a Base64 encoded String.
     * Returns AIR if an error occurs.
     *
     * @param string The Base64 encoded string which should be deserialized
     * @return The deserialized ItemStack
     */
    public static ItemStack deserialize(String string) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(string));
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
            return (ItemStack) bukkitObjectInputStream.readObject();
        } catch (Exception exception) {
            return new ItemStack(Material.AIR);
        }
    }
}
