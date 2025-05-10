package com.railwaycraft.simplycasinogames.listeners;


import com.railwaycraft.simplycasinogames.SimplyCasinoGames;
import com.railwaycraft.simplycasinogames.handlers.BlackjackPregame;
import com.railwaycraft.simplycasinogames.handlers.BlackjackRuntime;
import com.railwaycraft.simplycasinogames.handlers.SlotRuntime;
import com.railwaycraft.simplycasinogames.util.BlackjackLobbyUtility;
import com.railwaycraft.simplycasinogames.util.SCGMessageFormatting;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import static com.railwaycraft.simplycasinogames.util.GUIUtility.setItem;


public class SCGEventListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player)event.getWhoClicked();

        //if this inventory isn't one of our GUI's, leave early.
        if (!(inv.getName().contains(ChatColor.YELLOW.toString()) &&
            inv.getName().contains(ChatColor.BOLD.toString()) &&
                (inv.getName().contains(" slots") || inv.getName().contains(" blackjack")))) {
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
        if (name == null) {
            return;
        }
        //start slots
        if (name.contains("Spin for $")) { //TODO: sfx, balance take
            double balance = SimplyCasinoGames.economy.getBalance(player.getName());
            double cost = Double.parseDouble(inv.getName().substring(inv.getName().indexOf("$")+1, inv.getName().indexOf(" ")));
            if (balance >= cost) {
                SimplyCasinoGames.economy.withdraw(player.getName(), cost);
                setItem(Material.STAINED_GLASS_PANE.getId(), 13, " ", 24, inv);
                new SlotRuntime(player);
            } else {
                player.sendMessage(SCGMessageFormatting.errorMessagePrefix + "Insufficient funds");
                player.playSound(player.getLocation(),Sound.NOTE_PLING,0.2f,0.5f);
            }
        }

        //handle starting blackjack
        else if (name.contains("Start game")) {
            String n = event.getInventory().getName();
            double buyIn = Double.parseDouble(n.substring(5,n.indexOf(" ")));
            int table = Integer.parseInt(n.substring(n.lastIndexOf(" ")+1));
            BlackjackPregame lobby = BlackjackLobbyUtility.getLobby(buyIn,table);
            SimplyCasinoGames.blackjackPregames.remove(lobby);
            new BlackjackRuntime(lobby.players, lobby.table, lobby.getBet());
        }

        //handle blackjack click events
        else if (name.contains("Hit")) {
            setItem(160, 13, " ", event.getSlot(), inv);
            //CALL DRAW CARD EVENT IN BlackjackRuntime
            BlackjackRuntime game = SimplyCasinoGames.runningBlackjackGames.get(player.getMetadata("lobby").get(0).asInt());
            game.playerDrawCard(player);

        }
        else if (name.contains("Stand")) {
            setItem(160, 13, " ", event.getSlot(), inv);
            setItem(160,13," ",30, inv);
            BlackjackRuntime game = SimplyCasinoGames.runningBlackjackGames.get(player.getMetadata("lobby").get(0).asInt());
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.2f, 1f);
            game.isReady.put(player,true);
            game.updateReady();
        }
        else if (name.contains("Return to lobby")) {
            BlackjackRuntime game = SimplyCasinoGames.runningBlackjackGames.get(player.getMetadata("lobby").get(0).asInt());
            player.performCommand("blackjack " + player.getName() + " " + game.table + " " + game.wager);
        }
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getName().contains("blackjack lobby ")) {
            String n = event.getInventory().getName();
            double buyIn = Double.parseDouble(n.substring(5,n.indexOf(" ")));
            int table = Integer.parseInt(n.substring(n.lastIndexOf(" ")+1));
            BlackjackPregame lobby = BlackjackLobbyUtility.getLobby(buyIn,table);

            if (lobby == null) {
                return;
            }
            if (!event.getPlayer().hasMetadata("refreshing")) {
                lobby.removePlayer((Player) event.getPlayer());
                for (Player p : lobby.players) {
                    p.setMetadata("refreshing",new FixedMetadataValue(SimplyCasinoGames.getInstance(), true));
                }
                lobby.openGuiToPlayers();
            } else {
                event.getPlayer().removeMetadata("refreshing",SimplyCasinoGames.getInstance());
            }
        }
        else if (event.getInventory().getName().contains("blackjack table ")) {
            Player p = (Player)event.getPlayer();
            BlackjackRuntime game = SimplyCasinoGames.runningBlackjackGames.get(p.getMetadata("lobby").get(0).asInt());
            if (game == null) {
                return;
            }
            p.removeMetadata("lobby", SimplyCasinoGames.getInstance());
            game.players.remove(p);
            game.isReady.remove(p);
            game.updateLeftPlayers();
            //TODO: update current player's inventories to remove the player. Make it red stained glass like ChatColor.RED + <player name> (dropped) that shows what they had in their hand.
        }
    }
}
