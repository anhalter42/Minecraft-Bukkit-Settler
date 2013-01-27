/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import com.mahn42.framework.BlockPosition;
import org.bukkit.Material;
import org.bukkit.Rotation;
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
        profession.input.add(new ItemStack(Material.SAPLING));
        register(profession);
    }

    public SettlerForester() {
        super(typeName);
        fCollectItemRadius = 23;
        fItemsToCollect.add(Material.SAPLING);
        fItemsToCollect.add(Material.LEAVES);
        fItemsToCollect.add(Material.RED_ROSE);
        fItemsToCollect.add(Material.YELLOW_FLOWER);
        fItemsToCollect.add(Material.LONG_GRASS);
    }
    protected int getSaplingsCount = 0;
    protected int goWalkingCount = 0;

    @Override
    public void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            BlockPosition lPos = getPosition();
            if (getFrameConfig() == Rotation.FLIPPED) {
                lPos = getWorkPosition();
            }
            if (hasAtleastItems(Material.SAPLING, 1)) {
                ItemStack lItem = getFirstItem(Material.SAPLING);
                addActivityForNow(
                        new SettlerActivityFindRandomPath(lPos, 23, 10, PositionCondition.GrassOrDirtAround),
                        new SettlerActivityTakeInHand(lItem.getType(), lItem.getData().getData()),
                        new SettlerActivityPlaceBlock());
            } else if (hasAtleastItems(Material.RED_ROSE, 1)) {
                ItemStack lItem = getFirstItem(Material.RED_ROSE);
                addActivityForNow(
                        new SettlerActivityFindRandomPath(lPos, 23, 10, PositionCondition.GrassOrDirtAround),
                        new SettlerActivityTakeInHand(lItem.getType(), lItem.getData().getData()),
                        new SettlerActivityPlaceBlock());
            } else if (hasAtleastItems(Material.YELLOW_FLOWER, 1)) {
                ItemStack lItem = getFirstItem(Material.YELLOW_FLOWER);
                addActivityForNow(
                        new SettlerActivityFindRandomPath(lPos, 23, 10, PositionCondition.GrassOrDirtAround),
                        new SettlerActivityTakeInHand(lItem.getType(), lItem.getData().getData()),
                        new SettlerActivityPlaceBlock());
            } else {
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
            }
        }
        super.runInternal(aTask, aAccess);
    }

    @Override
    public String getFrameConfigName() {
        switch (fFrameConfig) {
            case NONE:
                return "plant everywhere";
            case COUNTER_CLOCKWISE:
                return "plant everywhere";
            case FLIPPED:
                return "plant near home";
            case CLOCKWISE:
                return "plant everywhere";
            default:
                return "";
        }
    }
}
