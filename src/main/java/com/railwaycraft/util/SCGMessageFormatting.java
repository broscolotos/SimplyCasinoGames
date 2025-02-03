package com.railwaycraft.util;

import com.railwaycraft.SimplyCasinoGames;
import org.bukkit.ChatColor;

public class SCGMessageFormatting {

        public static String messagePrefix = ChatColor.DARK_GREEN + "[" + SimplyCasinoGames.getInstance().chatPrefix + "] " + ChatColor.GREEN;
        public static String errorMessagePrefix = messagePrefix + ChatColor.RED;

}
