package com.railwaycraft.listeners;


import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SCGEventListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player)event.getWhoClicked();

        ItemStack item = event.getCurrentItem();
        if(item != null && item.getType() != Material.AIR) {
            ItemMeta meta = item.getItemMeta();
            String name = meta.getDisplayName();
        }
    }
}
