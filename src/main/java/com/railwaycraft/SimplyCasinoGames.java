package com.railwaycraft;

import java.io.*;
import java.util.logging.Logger;

import com.railwaycraft.listeners.SCGEventListener;
import com.railwaycraft.registry.CommandRegistry;
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

    @Override
    public void onEnable() {
        plugin = this;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        chatPrefix = config.getString("Chat Prefix");
        plugin.saveConfig();
        CommandRegistry.register();
        LOGGER.setParent(plugin.getLogger());
        economy = (RWCEconomy)Bukkit.getPluginManager().getPlugin("RWCEconomy");
        getServer().getPluginManager().registerEvents(new SCGEventListener(), plugin);
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

}
