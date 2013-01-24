/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import org.bukkit.Material;

/**
 *
 * @author andre
 */
public class SettlerWearer extends Settler {
    
    public static final String typeName = "Wearer";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerFarmer.class;
        profession.name = typeName;
        profession.frameMaterial = Material.LEATHER_BOOTS;
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_HELMET, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, true));
        register(profession);
    }

    public SettlerWearer() {
        super(typeName);
    }

    @Override
    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            addActivityForNow(
                    new SettlerActivityFindRandomPath(getBedPosition(), 23, 10, PositionCondition.None));
        }
        super.runInternal(aTask, aAccess);
    }
    
}
