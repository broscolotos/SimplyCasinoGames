package com.railwaycraft.registry;


import com.railwaycraft.SimplyCasinoGames;
import com.railwaycraft.commands.CommandSlots;

public class CommandRegistry {
    public static void register() {
        SimplyCasinoGames instance = SimplyCasinoGames.getInstance();
        CommandSlots commandSlots = new CommandSlots();
        instance.getCommand("slots").setExecutor(commandSlots);

    }
}
