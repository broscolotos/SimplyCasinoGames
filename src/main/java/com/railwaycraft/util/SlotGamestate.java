package com.railwaycraft.util;

import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class SlotGamestate {

    private LinkedList<ItemStack> gameItems = new LinkedList<>();
    public double buyIn = 0;

    public SlotGamestate(LinkedList<ItemStack> gameItems, double buyIn) {
        this.gameItems = gameItems;
        this.buyIn = buyIn;
    }

    public SlotGamestate(double buyIn) {
        this.buyIn = buyIn;
    }

    public ItemStack getItem(int i) { return i<9 ? gameItems.get(i) : gameItems.get(0); }

    public void setItem(int i, ItemStack stack) {
        if (i < 9) {
            if (i < gameItems.size())
                gameItems.set(i, stack);
            else
                gameItems.add(stack);
        } else
            gameItems.set(0, stack);
    }

    public void addItem(ItemStack stack) { gameItems.add(stack); }
}
