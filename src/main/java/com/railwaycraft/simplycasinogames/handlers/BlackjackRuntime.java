package com.railwaycraft.simplycasinogames.handlers;

import com.railwaycraft.simplycasinogames.SimplyCasinoGames;
import com.railwaycraft.simplycasinogames.util.SCGMessageFormatting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

import static com.railwaycraft.simplycasinogames.util.GUIUtility.fillBackground;
import static com.railwaycraft.simplycasinogames.util.GUIUtility.setItem;

public class BlackjackRuntime {

    public LinkedList<Player> players = new LinkedList<>();
    public int table = -1;
    public double wager = -1;
    public HashMap<String,LinkedList<String>> cardsPerPlayer = new HashMap<>();
    public HashMap<Player, Boolean> isReady = new HashMap<>();

    public BlackjackRuntime(Player player, int tableID, double price) {
        this.players.add(player);
        this.table = tableID;
        this.wager = price;
        SimplyCasinoGames.runningBlackjackGames.add(this);

        setupTable();
    }


    public BlackjackRuntime(LinkedList<Player> players, int tableID, double price) {
        this.players.addAll(players);
        this.table = tableID;
        this.wager = price;
        SimplyCasinoGames.runningBlackjackGames.add(this);

        setupTable();
    }

    public void setupTable() {
        //generate hands
        generateHand("dealer");
        for (Player p : players) {
            generateHand(p.getName());
        }

        for (Player p : players) {
            p.openInventory(Bukkit.createInventory(null, 54, org.bukkit.ChatColor.YELLOW + "" + ChatColor.BOLD + "$" + wager + " blackjack table " + table));
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 0.2f, 0.9f);
            for (BlackjackRuntime t : SimplyCasinoGames.runningBlackjackGames) {
                if (t == this) {
                    p.setMetadata("lobby", new FixedMetadataValue(SimplyCasinoGames.getInstance(), SimplyCasinoGames.runningBlackjackGames.indexOf(t)));
                    break;
                }
            }
            Inventory inventory = p.getOpenInventory().getTopInventory();
            inventory.clear();
            fillBackground(inventory);
            LinkedList<Player> pTemp = new LinkedList<>(players);
            pTemp.remove(p);
            for (int i = 0; i < pTemp.size(); i++) {
                int slot = (int) (49D - (int) (pTemp.size() / 2D)) + i; //center the player list in the 4th row
                setPlayerItem(pTemp.get(i).getName(), slot, inventory);
            }

            setPlayerItem(p.getName(), 31, inventory);
            setPlayerItem("Dealer", 13, inventory);

            if (calcCardValues(p.getName()) < 21) {
                setItem(160, 4, ChatColor.GOLD + "Hit", 30, inventory);
            }
            setItem(160, 4, ChatColor.GOLD + "Stand", 32, inventory);



            setItem(160, 9, ChatColor.GRAY + "" + isReady.keySet().size() + "/" + players.size() + " done", 8, inventory);
        }
    }


    /*public void blackjackGUI(Player player, int tableNum, double cost) {
        player.openInventory(Bukkit.createInventory(null, 54, org.bukkit.ChatColor.YELLOW + "" + org.bukkit.ChatColor.BOLD + "$" + cost + " blackjack"));
        Inventory inventory = player.getOpenInventory().getTopInventory();

        ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)13); //background
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName(" ");
        stack.setItemMeta(im);

        //background
        for (int i=0; i<inventory.getSize();i++) {
            inventory.setItem(i, stack);
        }

        //Generate Cards
        stack = new ItemStack(Material.CLAY_BALL, 1);
        im = stack.getItemMeta();
        im.setDisplayName(generateCard());
        stack.setItemMeta(im);
        inventory.setItem(8,stack);
    }*/


    public void generateHand(String user) {
        LinkedList<String> cards = new LinkedList<>();
        cards.add(getCard());
        cards.add(getCard());
        cardsPerPlayer.put(user,cards);
    }

    public String getCard() {
        return SimplyCasinoGames.deckOfCards.get(SimplyCasinoGames.rng.nextInt(SimplyCasinoGames.deckOfCards.size()));
    }

    public int calcCardValues(String playerName) {
        LinkedList<String> cards = cardsPerPlayer.get(playerName);
        int total = 0;
        boolean hasAce = false;
        for (String s : cards) {
            if (s.contains("Ace")) {
                total += 11;
                hasAce = true;
            }
            else if (s.contains("Jack") || s.contains("Queen") || s.contains("King")) {
                total += 10;
            }
            else {
                try {
                    total += Integer.parseInt(s.substring(0,s.indexOf(" ")));
                }
                catch (Exception ignored) {}
            }
        }
        if (hasAce && total > 21) {
            total -= 10;
        }
        return total;
    }

    public void setPlayerItem(String name, int slot, Inventory inventory) {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + name);
        if (!name.equals("Dealer")) {
            meta.setOwner(name);
        }
        else {
            name = "dealer";
        }
        LinkedList<String> lore = new LinkedList<>();
        for(int i = 0; i< cardsPerPlayer.get(name).size(); i++) {
            lore.add(ChatColor.GRAY + "Card " + (1+i) + " : " + ChatColor.YELLOW + cardsPerPlayer.get(name).get(i));
        }
        lore.add(ChatColor.GRAY + "Total : " + ChatColor.YELLOW + calcCardValues(name));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        inventory.setItem(slot, stack);
    }

    public void updateReady() {
        for (Player player : players) {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            setItem(160, 9, ChatColor.GRAY + "" + isReady.size() + "/" + players.size() + " done", 8, inventory);
            if (players.size() == isReady.size()) {
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.2f, 0.95f);
            }
        }
        if (players.size() == isReady.size()) {
            dealerDrawCard();
        }
    }

    public void playerDrawCard(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.2f, 1.15f);
        //DRAW A CARD
        drawCard(player.getName());

        //UPDATE PLAYER HEAD
        for (Player p : players) {
            Inventory inventory = p.getOpenInventory().getTopInventory();
            int slot = -1;
            for (int i=27;i<inventory.getSize();i++) {
                ItemStack item = inventory.getItem(i);
                if (!item.hasItemMeta()) {
                    continue;
                }
                ItemMeta meta = item.getItemMeta();
                if (meta.hasDisplayName() && meta.getDisplayName().contains(player.getName())) {
                    slot = i;
                    break;
                }
            }
            setPlayerItem(player.getName(), slot, inventory);
        }

        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (calcCardValues(player.getName()) < 21) {
            setItem(Material.STAINED_GLASS_PANE.getId(), 4, ChatColor.GOLD + "Hit", 30, inventory);
        }
        else {
            isReady.put(player,true);
            updateReady();
            setItem(Material.STAINED_GLASS_PANE.getId(), 13, " ", 32, inventory);
            if (calcCardValues(player.getName()) > 21) {
                loseEvent(player, 0);
            }
        }
    }

    public void dealerDrawCard() {
        if (calcCardValues("dealer") < 17) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    //DRAW A CARD
                    drawCard("dealer");

                    //UPDATE DEALER HEAD
                    for (Player p : players) {
                        p.playSound(p.getLocation(), Sound.NOTE_PLING, 0.2f, 1.15f);
                        Inventory inventory = p.getOpenInventory().getTopInventory();
                        setPlayerItem("Dealer", 13, inventory);
                    }

                    dealerDrawCard();
                }

            }.runTaskLater(SimplyCasinoGames.getInstance(), 20L);
        }
        else {
            endGame();
        }
    }

    public void drawCard(String name) {
        LinkedList<String> cards = new LinkedList<>(cardsPerPlayer.get(name));
        cards.add(getCard());
        cardsPerPlayer.put(name, cards);
    }

    public void loseEvent(Player player, int counter) {
        int timer = counter % 2 == 0 ? 15 : 10;
        setItem(Material.STAINED_GLASS_PANE.getId(), 13, " ", 32, player.getOpenInventory().getTopInventory());
        //FLASH OUTER RING OF GLASS RED 3 TIMES
        new BukkitRunnable() {
            @Override
            public void run() {
                Inventory inv = player.getOpenInventory().getTopInventory();
                if (counter % 2 == 0) {
                    for (int i = 0; i < inv.getSize(); i++) {
                        if ((i < 9 || i % 9 == 0 || i % 9 == 8 || i > 44) && inv.getItem(i).getDurability() == 13) {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.05f, 0.85f);
                            setItem(Material.STAINED_GLASS_PANE.getId(), 14, " ", i, inv);
                        }
                    }
                }
                else {
                    for (int i = 0; i < inv.getSize(); i++) {
                        if ((i < 9 || i % 9 == 0 || i % 9 == 8 || i > 44) && inv.getItem(i).getDurability() == 14) {
                            setItem(Material.STAINED_GLASS_PANE.getId(), 13, " ", i, inv);
                        }
                    }
                }
                if (counter > 4) {
                    return;
                }
                loseEvent(player, counter + 1);
            }
        }.runTaskLater(SimplyCasinoGames.getInstance(), timer);
    }

    public void winEvent(Player player, int counter) {
        int timer = counter % 2 == 0 ? 3 : 8;
        new BukkitRunnable() {
            @Override
            public void run() {
                Inventory inv = player.getOpenInventory().getTopInventory();
                if (counter % 2 == 0) {
                    for (int i = 0; i < inv.getSize(); i++) {
                        if ((i < 9 || i % 9 == 0 || i % 9 == 8 || i > 44) && inv.getItem(i).getDurability() == 13) {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.05f, 1.2f);
                            setItem(Material.STAINED_GLASS_PANE.getId(), 4, " ", i, inv);
                        }
                    }
                }
                else {
                    for (int i = 0; i < inv.getSize(); i++) {
                        if ((i < 9 || i % 9 == 0 || i % 9 == 8 || i > 44) && inv.getItem(i).getDurability() == 4) {
                            setItem(Material.STAINED_GLASS_PANE.getId(), 13, " ", i, inv);
                        }
                    }
                }
                if (counter > 10) {
                    return;
                }
                winEvent(player, counter + 1);
            }
        }.runTaskLater(SimplyCasinoGames.getInstance(), timer);
    }

    public void endGame() {
        for (Player p : players) {
            double bal = SimplyCasinoGames.economy.getBalance(p.getName());
            if ((calcCardValues(p.getName()) < calcCardValues("dealer") && calcCardValues("dealer") < 22) || calcCardValues(p.getName()) > 21) {
                loseEvent(p, 0);
                p.sendMessage(SCGMessageFormatting.errorMessagePrefix + "You've lost! New Balance: " + ChatColor.GOLD + SimplyCasinoGames.economy.format(bal));
            }
            else {
                winEvent(p, 0);
                double winnings = wager;
                if (calcCardValues("dealer") != calcCardValues(p.getName())) {
                    winnings *= 2;
                    if (calcCardValues(p.getName()) == 21) {
                        winnings *= 2;
                        SimplyCasinoGames.economy.setBalance(p.getName(), winnings + bal);
                        p.sendMessage(SCGMessageFormatting.messagePrefix + "You've hit blackjack! New Balance: " + ChatColor.GOLD + SimplyCasinoGames.economy.format(winnings + bal));
                        return;
                    }
                }
                SimplyCasinoGames.economy.setBalance(p.getName(), winnings + bal);
                p.sendMessage(SCGMessageFormatting.messagePrefix + "You've won! New Balance: " + ChatColor.GOLD + SimplyCasinoGames.economy.format(winnings + bal));
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    Inventory inv = p.getOpenInventory().getTopInventory();
                    setItem(Material.STAINED_GLASS_PANE.getId(), 14, ChatColor.GRAY + "Return to lobby", 0, inv);
                }
            }.runTaskLater(SimplyCasinoGames.getInstance(), 100);
        }
    }

    public void updateLeftPlayers() {
        //54
        for (Player player : players) {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            for (int i=45; i<inventory.getSize(); i++) {
                //loop the bottom row of items
                ItemStack stack = inventory.getItem(i);
                if (!stack.hasItemMeta()) {
                    continue;
                }
                if (!stack.getItemMeta().hasDisplayName()) {
                    continue;
                }
                if (stack.getItemMeta().getDisplayName().equals(" ")) {
                    continue;
                }
                String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
                boolean found = false;
                for (Player p : players) {
                    if (name.equals(p.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (stack.getItemMeta().hasLore()) {
                        setItem(Material.STAINED_GLASS_PANE.getId(), 14, ChatColor.RED + name + " (Dropped)", i, inventory, stack.getItemMeta().getLore());
                    }
                    else {
                        setItem(Material.STAINED_GLASS_PANE.getId(), 14, ChatColor.RED + name + " (Dropped)", i, inventory);
                    }
                }
            }
        }
        updateReady();
    }
}
