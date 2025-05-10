package com.railwaycraft.simplycasinogames.util;

import com.railwaycraft.simplycasinogames.SimplyCasinoGames;
import org.bukkit.ChatColor;

public class SCGMessageFormatting {

        public static String messagePrefix = ChatColor.DARK_GREEN + "[" + SimplyCasinoGames.getInstance().chatPrefix + "] " + ChatColor.GREEN;
        public static String errorMessagePrefix = messagePrefix + ChatColor.RED;

}
