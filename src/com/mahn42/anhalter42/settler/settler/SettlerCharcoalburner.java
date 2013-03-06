/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerCharcoalburner extends Settler {
    
    public static final String typeName = "Charcoalburner";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerCharcoalburner.class;
        profession.name = typeName;
        profession.frameMaterial = Material.COAL;
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_HELMET, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        profession.input.add(new ItemStack(Material.LOG));
        profession.input.add(new ItemStack(Material.WOOD));
        profession.output.add(new ItemStack(Material.APPLE));
        profession.output.add(new ItemStack(Material.COAL));
        register(profession);
    }

    public SettlerCharcoalburner() {
        super(typeName);
        fItemsToCollect.add(Material.APPLE);
        fItemsToCollect.add(Material.COAL);
        fPutInChestItems.add(new PutInChestItem(Material.APPLE, 0));
        fPutInChestItems.add(new PutInChestItem(Material.COAL, 0));
    }

    @Override
    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            addActivityForNow(
                    new SettlerActivityFindRandomPath(getWorkPosition(), SettlerPlugin.plugin.configDefaultPathRadius, SettlerPlugin.plugin.configDefaultPathAttempts, PositionCondition.None));
        }
        super.runInternal(aTask, aAccess);
    }
}
