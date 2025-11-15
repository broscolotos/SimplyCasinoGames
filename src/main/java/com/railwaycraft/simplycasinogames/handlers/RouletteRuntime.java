package com.railwaycraft.simplycasinogames.handlers;

import com.railwaycraft.simplycasinogames.SimplyCasinoGames;
import com.railwaycraft.simplycasinogames.util.SCGMessageFormatting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import static com.railwaycraft.simplycasinogames.util.GUIUtility.fillBackground;
import static com.railwaycraft.simplycasinogames.util.GUIUtility.setItem;

public class RouletteRuntime {
    public Inventory inventory = null;
    public double cost = -1;
    public Player player = null;
    public short color = -1; //15 = black, 14 = red, 13 = green
    public static ItemStack[] wheel;
    public int loopCount = 20 + SimplyCasinoGames.rng.nextInt(40);
    public int speed = 2;
    int colorSelector = 1;
    int timer = 0;

    public RouletteRuntime(double cost, Player player) {
        this.cost = cost;
        this.player = player;
        ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 2, (short)15);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "BLACK");
        stack.setItemMeta(meta);
        ItemStack stack1 = new ItemStack(Material.STAINED_GLASS_PANE, 2, (short)14);
        meta = stack1.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "RED");
        stack1.setItemMeta(meta);
        ItemStack stack2 = new ItemStack(Material.STAINED_GLASS_PANE, 14, (short)5);
        meta = stack2.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "GREEN");
        stack2.setItemMeta(meta);
        wheel = new ItemStack[]{stack, stack1, stack2};
        SimplyCasinoGames.rouletteGames.put(player, this);
        openLobbyGUI();
    }

    public void openLobbyGUI() {
        player.openInventory(Bukkit.createInventory(null, 9, org.bukkit.ChatColor.YELLOW + "" + ChatColor.BOLD + "$" + cost + " Roulette select"));
        Inventory inventory = player.getOpenInventory().getTopInventory();
        setItem(Material.STAINED_GLASS_PANE.getId(), 15, ChatColor.GRAY + "" + ChatColor.BOLD + "Select Color", 2, inventory);
        setItem(Material.STAINED_GLASS_PANE.getId(), 14, ChatColor.RED + "" + ChatColor.BOLD + "Select Color", 4, inventory);
        setItem(Material.STAINED_GLASS_PANE.getId(), 5, ChatColor.GREEN + "" + ChatColor.BOLD + "Select Color", 6, inventory);
    }

    public void setColor(short dmg) {
        this.color = dmg;
    }

    public void openGUI() {
        player.openInventory(Bukkit.createInventory(null, 27, org.bukkit.ChatColor.YELLOW + "" + ChatColor.BOLD + "$" + cost + " Roulette table"));
        Inventory inventory = player.getOpenInventory().getTopInventory();
        fillBackground(inventory);
        setItem(Material.STAINED_GLASS_PANE.getId(), 4, ChatColor.YELLOW  + "" + ChatColor.BOLD + "Selector", 4, inventory);
        setItem(Material.STAINED_GLASS_PANE.getId(), 4, ChatColor.YELLOW  + "" + ChatColor.BOLD + "Selector", 22, inventory);
        this.inventory = inventory;
        runTheGame(0,40);
    }

    public void runTheGame(int loopCounter, int delay) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (inventory == null || !inventory.getName().contains("Roulette")) {
                    return;
                }
                if (colorSelector == 17) {
                    colorSelector = 1;
                }
                int inventoryShiftIndex = 17;
                for (int j=0;j<8;j++) {
                    int shiftedIndex = inventoryShiftIndex - 1;
                    ItemStack stack = inventory.getItem(shiftedIndex);
                    inventory.setItem(17-j, stack);
                    inventoryShiftIndex--;
                }

                if (colorSelector % 2 == 1) {
                    inventory.setItem(9, wheel[0]);
                }
                else {
                    if (colorSelector == 16) {
                        inventory.setItem(9, wheel[2]);
                    }
                    else {
                        inventory.setItem(9, wheel[1]);
                    }
                }
                timer++;
                colorSelector++;
                if (timer == 5) {
                    timer = 0;
                    speed++;
                }
                if (loopCounter == loopCount-1) {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.7f, 1);
                    endGame();
                }
                else {
                    player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 0.2f, 0.85f);
                    runTheGame(loopCounter + 1, speed);
                }
            }
        }.runTaskLater(SimplyCasinoGames.getInstance(), delay);
    }

    public void endGame() {
        ItemStack stack = inventory.getItem(13);
        short damage = stack.getDurability();
        double bal = SimplyCasinoGames.economy.getBalance(player.getName());
        if (damage == color) {
            if (color == 5) {
                bal += (cost * 14);
                player.sendMessage(SCGMessageFormatting.messagePrefix + "You've won big!!!!");
            }
            else {
                bal += (cost * 2);
                player.sendMessage(SCGMessageFormatting.messagePrefix + "You've won!");
            }
        }
        else {
            player.sendMessage(SCGMessageFormatting.errorMessagePrefix + "Better luck next time!");
        }
        SimplyCasinoGames.economy.setBalance(player.getName(), bal);
        player.performCommand("bal");
    }
}
