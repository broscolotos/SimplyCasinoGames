package com.railwaycraft.listeners;


import com.railwaycraft.SimplyCasinoGames;
import com.railwaycraft.handlers.SlotRuntime;
import com.railwaycraft.util.SCGMessageFormatting;
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

        //if this inventory isn't one of our GUI's, leave early.
        if (!(inv.getName().contains(ChatColor.YELLOW.toString()) &&
            inv.getName().contains(ChatColor.BOLD.toString()) &&
            inv.getName().contains(" slots"))) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        //if we're clicking an empty slot then it doesn't matter.
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        event.setCancelled(true);
        ItemMeta meta = item.getItemMeta();
        String name = meta.getDisplayName();

        if (name.contains("Spin for $")) { //TODO: sfx, balance take
            double balance = SimplyCasinoGames.economy.getBalance(player.getName());
            double cost = Double.parseDouble(inv.getName().substring(inv.getName().indexOf("$")+1, inv.getName().indexOf(" ")));
            if (balance >= cost) {
                SimplyCasinoGames.economy.withdraw(player.getName(), cost);
                ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 13); //background
                ItemMeta im = stack.getItemMeta();
                im.setDisplayName(" ");
                stack.setItemMeta(im);
                inv.setItem(24, stack);

                new SlotRuntime(player);
            } else {
                player.sendMessage(SCGMessageFormatting.errorMessagePrefix + "Insufficient funds");
                player.playSound(player.getLocation(),Sound.NOTE_PLING,0.2f,0.5f);
            }
        }
    }
}
