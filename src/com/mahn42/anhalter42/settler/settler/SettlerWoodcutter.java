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
public class SettlerWoodcutter extends Settler {

    public static final String typeName = "Woodcutter";

    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerWoodcutter.class;
        profession.name = typeName;
        profession.frameMaterial = Material.IRON_AXE;
        profession.armor.add(new SettlerProfession.Item(Material.IRON_AXE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        register(profession);
    }
    
    public SettlerWoodcutter() {
        super(typeName);
    }
}
