/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerProfession;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
        fCollectItemRadius = 12;
        fItemsToCollect.add(Material.SAPLING);
        fItemsToCollect.add(Material.LEAVES);
        fItemsToCollect.add(Material.RED_ROSE);
        fItemsToCollect.add(Material.YELLOW_FLOWER);
        fItemsToCollect.add(Material.LONG_GRASS);
    }
    protected int getSaplingsCount = 0;
    protected int goWalkingCount = 0;

    @Override
    public void runInternal(SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            if (!hasAtleastItems(Material.SAPLING, 1)) {
                if (goWalkingCount > 0) {
                    goWalkingCount--;
                    addActivityForNow(new SettlerActivityFindRandomPath(23, 10, PositionCondition.None));
                } else if (getSaplingsCount > 0) {
                    goWalkingCount = 5;
                    getSaplingsCount = 0;
                } else {
                    getSaplingsCount++;
                    addActivityForNow(
                            new SettlerActivityWalkToTarget(getBedPosition()),
                            new SettlerActivityGetItemsFromChest(Material.SAPLING, 10, 0));
                }
            } else {
                ItemStack lItem = getFirstItem(Material.SAPLING);
                addActivityForNow(
                        new SettlerActivityFindRandomPath(23, 10, PositionCondition.GrassOrDirtAround),
                        new SettlerActivityTakeInHand(lItem.getType(), lItem.getData().getData()),
                        new SettlerActivityPlaceBlock());
            }
        }
        super.runInternal(aAccess);
    }
}
