package com.railwaycraft.simplycasinogames.handlers;

import com.railwaycraft.simplycasinogames.SimplyCasinoGames;
import com.railwaycraft.simplycasinogames.util.SCGMessageFormatting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.LinkedList;

import static com.railwaycraft.simplycasinogames.util.GUIUtility.*;

public class BlackjackPregame {

    public LinkedList<Player> players = new LinkedList<>();
    public int table = -1;
    public double wager = -1;

    public BlackjackPregame(Player player, int tableID, double price) {
        this.players.add(player);
        this.table = tableID;
        this.wager = price;
        openGuiToPlayers();
        SimplyCasinoGames.blackjackPregames.add(this);
    }

    public int getTableNum() {
        return table;
    }

    public double getBet() {
        return wager;
    }

    public void addPlayer(Player p) {
        if (numPlayers() < 7) {
            for (Player p1 : players) {
                p1.setMetadata("refreshing", new FixedMetadataValue(SimplyCasinoGames.getInstance(), true));
            }
            players.add(p);
            openGuiToPlayers();
        }
        else {
            p.sendMessage(SCGMessageFormatting.errorMessagePrefix + "The table you have tried to join is full.");
        }
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    public int numPlayers() {
        return players.size();
    }

    public void openGuiToPlayers() {
        for(Player p : players) {
            p.openInventory(Bukkit.createInventory(null, 54, ChatColor.YELLOW + "" + ChatColor.BOLD + "$" + wager + " blackjack lobby " + table));
            Inventory inventory = p.getOpenInventory().getTopInventory();
            ItemStack stack;
            //fill the background
            fillBackground(inventory);
            fillPlayerBorder(inventory);

            for (int i=0;i<players.size();i++) {
                int slot = (int)(31D - (int)(players.size() / 2D)) + i; //center the player list in the 4th row
                stack = getHead(players.get(i));
                inventory.setItem(slot, stack);
            }
            if (p.getName().equals(players.get(0).getName())) {
                setItem(Material.STAINED_GLASS_PANE.getId(), 4, ChatColor.GOLD + "Start game",40, inventory);
            }
        }
    }

    public static void fillPlayerBorder(Inventory inventory) {
        for (int i=18; i<45;i++) {
            setItem(Material.STAINED_GLASS_PANE.getId(), 3, " ", i, inventory);
        }
        for (int i=28;i<35;i++) {
            inventory.setItem(i,null);
        }
    }

}
