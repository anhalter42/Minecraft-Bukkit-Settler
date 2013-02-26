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
        profession.input.add(new ItemStack(Material.WHEAT));
        profession.input.add(new ItemStack(Material.CARROT_ITEM));
        profession.output.add(new ItemStack(Material.APPLE));
        profession.output.add(new ItemStack(Material.RAW_BEEF));
        profession.output.add(new ItemStack(Material.LEATHER));
        register(profession);
    }

    public SettlerRancher() {
        super(typeName);
        fItemsToCollect.add(Material.WHEAT);
        fItemsToCollect.add(Material.CARROT_ITEM);
        fItemsToCollect.add(Material.APPLE);
        fItemsToCollect.add(Material.RAW_BEEF);
        fItemsToCollect.add(Material.LEATHER);
        fPutInChestItems.add(new PutInChestItem(Material.APPLE, 0));
        fPutInChestItems.add(new PutInChestItem(Material.RAW_BEEF, 0));
        fPutInChestItems.add(new PutInChestItem(Material.LEATHER, 0));
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
