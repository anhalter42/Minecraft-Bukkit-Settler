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
public class SettlerCarpenter extends Settler {
    
    public static final String typeName = "Charcoalburner";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerCharcoalburner.class;
        profession.name = typeName;
        profession.frameMaterial = Material.WOOD;
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_HELMET, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        profession.input.add(new ItemStack(Material.LOG));
        profession.output.add(new ItemStack(Material.APPLE));
        profession.output.add(new ItemStack(Material.WOOD));
        profession.output.add(new ItemStack(Material.WOOD_STEP));
        profession.output.add(new ItemStack(Material.WOOD_STAIRS));
        profession.output.add(new ItemStack(Material.SPRUCE_WOOD_STAIRS));
        profession.output.add(new ItemStack(Material.BIRCH_WOOD_STAIRS));
        profession.output.add(new ItemStack(Material.JUNGLE_WOOD_STAIRS));
        register(profession);
    }

    public SettlerCarpenter() {
        super(typeName);
        fItemsToCollect.add(Material.APPLE);
        fItemsToCollect.add(Material.WOOD);
        fItemsToCollect.add(Material.LOG);
        fPutInChestItems.add(new PutInChestItem(Material.APPLE, 0));
        fPutInChestItems.add(new PutInChestItem(Material.WOOD, 0));
        fPutInChestItems.add(new PutInChestItem(Material.LOG, 0));
    }

    @Override
    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            addActivityForNow(
                    new SettlerActivityFindRandomPath(getWorkPosition(), 2, SettlerPlugin.plugin.configDefaultPathAttempts, PositionCondition.None));
        }
        super.runInternal(aTask, aAccess);
    }
}
