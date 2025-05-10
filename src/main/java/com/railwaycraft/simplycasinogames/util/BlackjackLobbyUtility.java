package com.railwaycraft.simplycasinogames.util;

import com.railwaycraft.simplycasinogames.SimplyCasinoGames;
import com.railwaycraft.simplycasinogames.handlers.BlackjackPregame;

public class BlackjackLobbyUtility {
    public static BlackjackPregame getLobby(double buyIn, int table) {
        BlackjackPregame lobby = null;
        for (BlackjackPregame g : SimplyCasinoGames.blackjackPregames) {
            if (g.getBet() == buyIn && g.getTableNum() == table) {
                lobby = g;
                break;
            }
        }
        return lobby;
    }

}
