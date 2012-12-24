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
public class SettlerForester extends Settler {

    public static final String typeName = "Forester";

    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerForester.class;
        profession.name = typeName;
        profession.frameMaterial = Material.SAPLING;
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        register(profession);
    }
    
    public SettlerForester() {
        super(typeName);
    }
}
