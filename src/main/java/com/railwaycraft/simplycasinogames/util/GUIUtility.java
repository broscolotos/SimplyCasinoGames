package com.railwaycraft.simplycasinogames.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for formatting GUIs
 * @author broscolotos
 */
public class GUIUtility {

    /**
     * Returns an ItemStack for the specified player, including the name being adjusted and color formatted.
     * @param player
     * @return ItemStack
     * @author broscolotos
     */
    public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(ChatColor.DARK_AQUA + player.getName());
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }

    /**
     * Fills the entire specified inventory with green stained-glass pane
     * @param inventory
     * @author broscolotos
     */
    public static void fillBackground(Inventory inventory) {
        ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)13); //background
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName(" ");
        stack.setItemMeta(im);
        for (int j=0; j<inventory.getSize();j++) {
            inventory.setItem(j, stack);
        }
    }

    /**
     * Sets the specified slot in the specified inventory to a named item.
     * @param id
     * @param damage
     * @param name
     * @param slot
     * @param inventory
     * @author broscolotos
     */
    public static void setItem(int id, int damage, String name, int slot, Inventory inventory) {
        ItemStack stack = new ItemStack(id, 1, (short)damage);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        inventory.setItem(slot, stack);
    }

    /**
     * Sets the specified slot in the specified inventory to a named item with lore.
     * @param id
     * @param damage
     * @param name
     * @param slot
     * @param inventory
     * @param lore
     * @author broscolotos
     */
    public static  void setItem(int id, int damage, String name, int slot, Inventory inventory, List<String> lore) {
        ItemStack stack = new ItemStack(id, 1, (short)damage);
        ItemMeta meta = stack.getItemMeta();
        LinkedList<String> loreActual = new LinkedList<>(lore);
        meta.setLore(loreActual);
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        inventory.setItem(slot, stack);
    }
}
