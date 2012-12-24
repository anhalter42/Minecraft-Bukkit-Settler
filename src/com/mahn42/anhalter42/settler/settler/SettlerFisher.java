/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerProfession;
import org.bukkit.Material;

/**
 *
 * @author andre
 */
public class SettlerFisher extends Settler {

    public static final String typeName = "Fisher";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerFisher.class;
        profession.name = typeName;
        profession.frameMaterial = Material.FISHING_ROD;
        profession.armor.add(new SettlerProfession.Item(Material.FISHING_ROD, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        profession.inventory.add(new SettlerProfession.Item(Material.BOAT, true));
        register(profession);
    }
    
    public SettlerFisher() {
        super(typeName);
    }
}
