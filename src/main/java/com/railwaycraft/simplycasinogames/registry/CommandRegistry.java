package com.railwaycraft.simplycasinogames.registry;


import com.railwaycraft.simplycasinogames.SimplyCasinoGames;
import com.railwaycraft.simplycasinogames.commands.CommandBlackjack;
import com.railwaycraft.simplycasinogames.commands.CommandSlots;

public class CommandRegistry {
    public static void register() {
        SimplyCasinoGames instance = SimplyCasinoGames.getInstance();
        CommandSlots commandSlots = new CommandSlots();
        instance.getCommand("slots").setExecutor(commandSlots);
        CommandBlackjack commandBlackjack = new CommandBlackjack();
        instance.getCommand("blackjack").setExecutor(commandBlackjack);
    }
}
