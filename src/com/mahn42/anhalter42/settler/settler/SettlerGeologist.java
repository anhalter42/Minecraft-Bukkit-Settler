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
public class SettlerGeologist extends Settler {

    public static final String typeName = "Geologist";

    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerGeologist.class;
        profession.name = typeName;
        profession.frameMaterial = Material.SIGN;
        profession.armor.add(new SettlerProfession.Item(Material.IRON_PICKAXE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        profession.inventory.add(new SettlerProfession.Item(Material.SIGN, false));
        register(profession);
    }
    
    public SettlerGeologist() {
        super(typeName);
    }
}
