/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerProfession {
    public String name;
    public Material frameMaterial;
    public Class settlerClass;
    
    public static class Item {
        public ItemStack item;
        public boolean needed;
        
        public Item(ItemStack aStack, boolean aNeeded) {
            item = aStack;
            needed = aNeeded;
        }

        public Item(Material aMaterial, boolean aNeeded) {
            item = new ItemStack(aMaterial);
            needed = aNeeded;
        }
    }
    
    public ArrayList<Item> armor = new ArrayList<SettlerProfession.Item>();
    public ArrayList<Item> inventory = new ArrayList<SettlerProfession.Item>();
    
    public ArrayList<ItemStack> input = new ArrayList<ItemStack>();
    public ArrayList<ItemStack> output = new ArrayList<ItemStack>();
    
    public boolean equals(Object aObject) {
        if (aObject instanceof SettlerProfession) {
            return name.equalsIgnoreCase(((SettlerProfession)aObject).name);
        }
        return false;
    }
}
