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
        profession.output.add(new ItemStack(Material.RAW_FISH));
        profession.output.add(new ItemStack(Material.COOKED_FISH));
        register(profession);
    }
    
    public SettlerFisher() {
        super(typeName);
        fItemsToCollect.add(Material.FISHING_ROD);
        fItemsToCollect.add(Material.RAW_FISH);
        fItemsToCollect.add(Material.COOKED_FISH);
        fItemsToCollect.add(Material.INK_SACK);
        fPutInChestItems.add(new PutInChestItem(Material.RAW_FISH, 0));
        fPutInChestItems.add(new PutInChestItem(Material.COOKED_FISH, 0));
        fPutInChestItems.add(new PutInChestItem(Material.INK_SACK, 0));
    }

    @Override
    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            addActivityForNow(new SettlerActivityFindRandomPath());
        }
        super.runInternal(aTask, aAccess);
    }
}
