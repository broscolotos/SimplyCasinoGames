package com.railwaycraft.simplycasinogames.commands;

import com.railwaycraft.simplycasinogames.handlers.RouletteRuntime;
import com.railwaycraft.simplycasinogames.util.SCGMessageFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRoulette implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //  /roulette <player> <buyin>
        if (!cmd.getName().equalsIgnoreCase("roulette")) {
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(SCGMessageFormatting.errorMessagePrefix + "Improper command usage. /roulette <player> <cost>");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null || !player.isOnline()) {
            sender.sendMessage(SCGMessageFormatting.errorMessagePrefix + "The player specified is not valid. /roulette <player> <cost>");
            return true;
        }
        try {
            double cost = Math.abs(Double.parseDouble(args[1]));
            //SimplyCasinoGames.economy.withdraw(player.getName(), cost);
            new RouletteRuntime(cost, player);
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage(SCGMessageFormatting.errorMessagePrefix + "The cost specified is not valid. /roulette <player> <cost>");
            return true;
        }
    }
}
