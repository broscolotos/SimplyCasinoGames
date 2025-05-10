package com.railwaycraft.simplycasinogames;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;

import com.railwaycraft.simplycasinogames.handlers.BlackjackPregame;
import com.railwaycraft.simplycasinogames.handlers.BlackjackRuntime;
import com.railwaycraft.simplycasinogames.listeners.SCGEventListener;
import com.railwaycraft.simplycasinogames.registry.CommandRegistry;
import com.railwaycraft.rwceconomy.RWCEconomy;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimplyCasinoGames extends JavaPlugin {
    private static final Logger LOGGER = Logger.getLogger("SimplyCasinoGames");

    private static FileConfiguration config;
    public static SimplyCasinoGames plugin;
    public static RWCEconomy economy;
    public String chatPrefix;
    public int buyIn;
    public int[] slotItemIDs = new int[4];
    public static Random rng = new Random();
    public static ArrayList<String> deckOfCards = new ArrayList<>();
    public static LinkedList<BlackjackRuntime> runningBlackjackGames = new LinkedList<>();
    public static ArrayList<BlackjackPregame> blackjackPregames = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        chatPrefix = config.getString("Chat Prefix");
        slotItemIDs[0] = config.getInt("Worst Item");
        slotItemIDs[1] = config.getInt("Bad Item");
        slotItemIDs[2] = config.getInt("Good Item");
        slotItemIDs[3] = config.getInt("Best Item");
        buyIn = config.getInt("Buy-in");
        plugin.saveConfig();
        CommandRegistry.register();
        LOGGER.setParent(plugin.getLogger());
        economy = (RWCEconomy)Bukkit.getPluginManager().getPlugin("RWCEconomy");
        getServer().getPluginManager().registerEvents(new SCGEventListener(), plugin);
        populateDeck();
    }

    public static Logger logger() { return LOGGER; }
    public static SimplyCasinoGames getInstance() { return plugin; }
    public static FileConfiguration getSCGConfig() { return config; }

    public static boolean reloadSCGConfig() {
        Plugin p = plugin;
        p.reloadConfig();
        config = p.getConfig();
        return true;
    }

    public static File getDataFolder1() { return plugin.getDataFolder(); }

    @Override
    public void onDisable() { HandlerList.unregisterAll(); }


    public void populateDeck() {
        String[] suits = new String[]{"Hearts","Diamonds","Clubs","Spades"};
        String[] individuals = new String[]{"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "Jack", "Queen", "King"};
        for (String suit : suits) {
            for (String indi : individuals) {
                deckOfCards.add(indi + " of " + suit);
            }
        }
    }
}
