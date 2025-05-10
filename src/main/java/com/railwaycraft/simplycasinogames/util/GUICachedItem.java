package com.railwaycraft.simplycasinogames.util;


import org.bukkit.inventory.ItemStack;

public class GUICachedItem {

    public ItemStack stack;

    public String name;

    public GUICachedItem(ItemStack stack, String name) {
        this.stack = stack;
        this.name = name;
    }

    public ItemStack getStack() { return stack; }

    public String getName() { return name; }

}
