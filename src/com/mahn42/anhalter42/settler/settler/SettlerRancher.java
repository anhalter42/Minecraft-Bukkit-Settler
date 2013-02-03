/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerRancher extends Settler {
    
    public static final String typeName = "Rancher";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerRancher.class;
        profession.name = typeName;
        profession.frameMaterial = Material.FENCE;
        profession.armor.add(new SettlerProfession.Item(Material.IRON_HOE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_HELMET, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, true));
        profession.output.add(new ItemStack(Material.APPLE));
        register(profession);
    }

    public SettlerRancher() {
        super(typeName);
        fItemsToCollect.add(Material.APPLE);
        fPutInChestItems.add(new PutInChestItem(Material.APPLE, 0));
    }

    @Override
    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            addActivityForNow(
                    new SettlerActivityFindRandomPath(getWorkPosition(), 23, 10, PositionCondition.None));
        }
        super.runInternal(aTask, aAccess);
    }
}
