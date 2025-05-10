package com.railwaycraft.simplycasinogames.commands;

import com.railwaycraft.simplycasinogames.SimplyCasinoGames;
import com.railwaycraft.simplycasinogames.handlers.BlackjackPregame;
import com.railwaycraft.simplycasinogames.util.BlackjackLobbyUtility;
import com.railwaycraft.simplycasinogames.util.SCGMessageFormatting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class CommandBlackjack implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //  /blackjack <player> <table> <buyin>
        if (cmd.getName().equalsIgnoreCase("blackjack")) {
            if (args.length > 0) {
                if (args.length > 1) {
                    Player player = Bukkit.getPlayer(args[0]);
                    if (player == null) {
                        sender.sendMessage(SCGMessageFormatting.errorMessagePrefix + "No player was specified.");
                    }
                    else {
                        if (args.length == 3) {
                            try {
                                joinBlackjack(player, Math.abs(Integer.parseInt(args[1])), Math.abs(Double.parseDouble(args[2])));
                            } catch (NumberFormatException e) {
                                sender.sendMessage(SCGMessageFormatting.errorMessagePrefix + "Argument 2 must be an integer; argument 3 must be a number.");
                            }
                        }
                    }
                }
                else {
                    sender.sendMessage(SCGMessageFormatting.errorMessagePrefix + "A table number must be specified.");
                }
            }
            else {
                sender.sendMessage(SCGMessageFormatting.errorMessagePrefix + "A player must be specified.");
            }
        }
        return true;
    }

    public void joinBlackjack(Player player, int table, double bet) {
        if (SimplyCasinoGames.economy.getBalance(player.getName()) >= bet) {
            SimplyCasinoGames.economy.withdraw(player.getName(), bet);
            BlackjackPregame lobby = BlackjackLobbyUtility.getLobby(bet, table);
            if (lobby == null) {
                lobby = new BlackjackPregame(player, table, bet);
                SimplyCasinoGames.blackjackPregames.add(lobby);
            } else {
                lobby.addPlayer(player);
            }
        }
        else {
            player.sendMessage(SCGMessageFormatting.errorMessagePrefix + "Insufficient funds to play.");
            player.playSound(player.getLocation(), Sound.NOTE_PLING,0.2f,0.5f);
        }
    }
}
