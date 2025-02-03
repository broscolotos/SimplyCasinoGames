package com.railwaycraft.handlers;

import com.railwaycraft.SimplyCasinoGames;
import com.railwaycraft.util.SCGMessageFormatting;
import com.railwaycraft.util.SlotGamestate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class SlotRuntime {

    public SlotGamestate gamestate;

    public SlotRuntime(Player player) {
        Inventory inv = player.getOpenInventory().getTopInventory();
        double cost = Double.parseDouble(inv.getName().substring(inv.getName().indexOf("$")+1, inv.getName().indexOf(" ")));
        gamestate = new SlotGamestate(cost);
        //populate our gamestate
        int slot = 12;
        for (int i=0; i<3;i++) {
            for (int j=0;j<3;j++) {
                ItemStack stack = inv.getItem(slot);
                gamestate.addItem(stack);
                slot++;
            }
            slot += 6;
        }

        new BukkitRunnable() { //get baltop every 5 minutes
            int delay = 2;
            int waited = 0;
            int counter = 0;

            int column1 = (SimplyCasinoGames.rng.nextInt(7))+8;
            int column2 = (SimplyCasinoGames.rng.nextInt(7))+13;
            int column3 = (SimplyCasinoGames.rng.nextInt(7))+23;

            public void run() {
                if (player.getOpenInventory().getTopInventory() != inv) {
                    this.cancel();
                    player.sendMessage(SCGMessageFormatting.errorMessagePrefix + "You have closed your inventory thus forfeiting your bet!");
                    player.playSound(player.getLocation(),Sound.NOTE_PLING,0.2f,0.5f);
                }
                else if (waited == delay) {
                    int index;
                    if (counter == column3) { //TODO: proper end game.
                        if (gamestate.getItem(3).getType() == gamestate.getItem(4).getType() &&
                            gamestate.getItem(3).getType() == gamestate.getItem(5).getType()) {
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.2f, 1);
                            double payout = 0;
                            if (gamestate.getItem(3).getType() == new ItemStack(SimplyCasinoGames.getInstance().slotItemIDs[0]).getType()) {
                                payout = gamestate.buyIn;
                                //worst item
                            }
                            else if (gamestate.getItem(3).getType() == new ItemStack(SimplyCasinoGames.getInstance().slotItemIDs[1]).getType()) {
                                payout = gamestate.buyIn * 2;
                                //bad item
                            }
                            else if (gamestate.getItem(3).getType() == new ItemStack(SimplyCasinoGames.getInstance().slotItemIDs[2]).getType()) {
                                payout = gamestate.buyIn * 4;
                                //good item
                            }
                            if (gamestate.getItem(3).getType() == new ItemStack(SimplyCasinoGames.getInstance().slotItemIDs[3]).getType()) {
                                payout = gamestate.buyIn * 10;
                                //best item
                            }
                            player.sendMessage(SCGMessageFormatting.messagePrefix + "You've won " + SimplyCasinoGames.economy.format(payout));
                            double balance = SimplyCasinoGames.economy.getBalance(player.getName());
                            SimplyCasinoGames.economy.setBalance(player.getName(), balance + payout);
                        } else {
                            player.playSound(player.getLocation(),Sound.HURT_FLESH, 0.2f, 1);
                            player.sendMessage(SCGMessageFormatting.errorMessagePrefix + "Better luck next time!");
                        }

                        ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)4); //background
                        ItemMeta im = stack.getItemMeta();
                        im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Spin for $" + inv.getName().substring(inv.getName().indexOf("$")+1, inv.getName().indexOf(" ")));
                        stack.setItemMeta(im);

                        inv.setItem(24, stack);
                        this.cancel();
                    }
                    else {
                        if (counter < column1) {
                            index = SimplyCasinoGames.rng.nextInt(SimplyCasinoGames.getInstance().slotItemIDs.length);
                            gamestate.setItem(6,gamestate.getItem(3));
                            gamestate.setItem(3,gamestate.getItem(0));
                            gamestate.setItem(0, new ItemStack(SimplyCasinoGames.getInstance().slotItemIDs[index]));
                        }

                        if (counter < column2) {
                            index = SimplyCasinoGames.rng.nextInt(SimplyCasinoGames.getInstance().slotItemIDs.length);
                            gamestate.setItem(7,gamestate.getItem(4));
                            gamestate.setItem(4,gamestate.getItem(1));
                            gamestate.setItem(1, new ItemStack(SimplyCasinoGames.getInstance().slotItemIDs[index]));
                        }

                        index = SimplyCasinoGames.rng.nextInt(SimplyCasinoGames.getInstance().slotItemIDs.length);
                        gamestate.setItem(8, gamestate.getItem(5));
                        gamestate.setItem(5, gamestate.getItem(2));
                        gamestate.setItem(2, new ItemStack(SimplyCasinoGames.getInstance().slotItemIDs[index]));
                        if (counter < column1) {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.2f, 1.1f);
                        }
                        else if (counter < column2) {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.2f, 1.15f);
                        }
                        else {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.2f, 1.2f);
                        }
                    }

                    //display update to player
                    int slot = 12;
                    int itemIndex = 0;
                    for (int i=0; i<3;i++) {
                        for (int j=0;j<3;j++) {
                            inv.setItem(slot, gamestate.getItem(itemIndex));
                            slot++;
                            itemIndex++;
                        }
                        slot += 6;
                    }
                    counter++;
                    if (counter % 5 == 0) {
                        delay++;
                    }
                    waited = 0;
                }
                waited++;
            }
        }.runTaskTimerAsynchronously(SimplyCasinoGames.getInstance(), 0L, 1);
    }
}