package com.railwaycraft.commands;

import com.railwaycraft.SimplyCasinoGames;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;


public class CommandSlots implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //  /slots broscolotos 200
        if (cmd.getName().equalsIgnoreCase("slots")) {
            if (args.length == 1) {
                Player p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    //TODO: error for no player specified
                } else {
                    slotsGUI(p, SimplyCasinoGames.getInstance().buyIn);
                }
            }
            else if (args.length == 2) {
                Player p = Bukkit.getPlayer(args[0]);
                if (p == null) {
                    //TODO: error for no player specified
                } else {
                    try {
                        double buyIn = Double.parseDouble(args[1]);
                        slotsGUI(p, buyIn);
                    } catch (NumberFormatException e) {
                        //TODO: error for non-int specified for buy-in.
                    }
                }
                //TODO: buy-in specified
            }
            else {
                //TODO: error; incorrect usage
            }
        }
        return true;
    }


    public void slotsGUI(Player player, double cost) {
        player.openInventory(Bukkit.createInventory(null, 45, ChatColor.YELLOW + "" + ChatColor.BOLD + "$" + cost + " slots"));
        Inventory inventory = player.getOpenInventory().getTopInventory();

        ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)13); //background
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName(" ");
        stack.setItemMeta(im);

        //background
        for (int i=0; i<inventory.getSize();i++) {
            inventory.setItem(i, stack);
        }
        //main 9 items
        int slot = 12;
        for (int i=0; i<3;i++) {
            for (int j=0;j<3;j++) {
                int index = SimplyCasinoGames.rng.nextInt(SimplyCasinoGames.getInstance().slotItemIDs.length);
                stack = new ItemStack(SimplyCasinoGames.getInstance().slotItemIDs[index]);
                inventory.setItem(slot, stack);
                slot++;
            }
            slot += 6;
        }

        //spin button
        stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)4); //background
        im = stack.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Spin for $" + cost);
        stack.setItemMeta(im);
        inventory.setItem(24, stack);
    }

}
