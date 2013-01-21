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
public class SettlerFarmer extends Settler {

    public static final String typeName = "Farmer";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerFarmer.class;
        profession.name = typeName;
        profession.frameMaterial = Material.IRON_HOE;
        profession.armor.add(new SettlerProfession.Item(Material.IRON_HOE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, true));
        profession.output.add(new ItemStack(Material.WHEAT));
        profession.output.add(new ItemStack(Material.SEEDS));
        profession.output.add(new ItemStack(Material.APPLE));
        profession.output.add(new ItemStack(Material.CARROT));
        profession.output.add(new ItemStack(Material.SPECKLED_MELON));
        profession.output.add(new ItemStack(Material.PUMPKIN));
        profession.output.add(new ItemStack(Material.PUMPKIN_SEEDS));
        profession.output.add(new ItemStack(Material.MELON));
        profession.output.add(new ItemStack(Material.MELON_SEEDS));
        profession.output.add(new ItemStack(Material.POTATO));
        register(profession);
    }

    public SettlerFarmer() {
        super(typeName);
        fItemsToCollect.add(Material.WHEAT);
        fItemsToCollect.add(Material.SEEDS);
        fItemsToCollect.add(Material.APPLE);
        fItemsToCollect.add(Material.CARROT);
        fItemsToCollect.add(Material.SPECKLED_MELON);
        fItemsToCollect.add(Material.PUMPKIN);
        fItemsToCollect.add(Material.PUMPKIN_SEEDS);
        fItemsToCollect.add(Material.MELON);
        fItemsToCollect.add(Material.MELON_SEEDS);
        fItemsToCollect.add(Material.POTATO);
        fPutInChestItems.add(new PutInChestItem(Material.WHEAT, 0));
        fPutInChestItems.add(new PutInChestItem(Material.SEEDS, 10));
        fPutInChestItems.add(new PutInChestItem(Material.APPLE, 0));
        fPutInChestItems.add(new PutInChestItem(Material.CARROT, 10));
        fPutInChestItems.add(new PutInChestItem(Material.SPECKLED_MELON, 10));
        fPutInChestItems.add(new PutInChestItem(Material.PUMPKIN, 10));
        fPutInChestItems.add(new PutInChestItem(Material.PUMPKIN_SEEDS, 10));
        fPutInChestItems.add(new PutInChestItem(Material.MELON, 10));
        fPutInChestItems.add(new PutInChestItem(Material.MELON_SEEDS, 10));
        fPutInChestItems.add(new PutInChestItem(Material.POTATO, 10));
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
