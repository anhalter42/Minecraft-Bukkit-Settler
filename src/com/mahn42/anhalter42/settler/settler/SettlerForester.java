/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import com.mahn42.framework.BlockPosition;
import java.util.Random;
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
        profession.input.add(new ItemStack(Material.RED_ROSE));
        profession.input.add(new ItemStack(Material.YELLOW_FLOWER));
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
        fPutInChestItems.add(new PutInChestItem(Material.SAPLING, 10));
        fPutInChestItems.add(new PutInChestItem(Material.LEAVES, 10));
        fPutInChestItems.add(new PutInChestItem(Material.RED_ROSE, 10));
        fPutInChestItems.add(new PutInChestItem(Material.YELLOW_FLOWER, 10));
        fPutInChestItems.add(new PutInChestItem(Material.LONG_GRASS, 10));
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
            boolean lDone = false;
            int lCount = 0;
            do {
                int lRnd = aAccess.random.nextInt(3);
                if (lRnd == 0 && hasAtleastItems(Material.SAPLING, 1)) {
                    ItemStack lItem = getFirstItem(Material.SAPLING);
                    addActivityForNow(
                            new SettlerActivityFindRandomPath(lPos, 23, 10, PositionCondition.GrassOrDirtAround),
                            new SettlerActivityTakeInHand(lItem.getType(), lItem.getData().getData()),
                            new SettlerActivityPlaceBlock());
                    lDone = true;
                } else if (lRnd == 1 && hasAtleastItems(Material.RED_ROSE, 1)) {
                    ItemStack lItem = getFirstItem(Material.RED_ROSE);
                    addActivityForNow(
                            new SettlerActivityFindRandomPath(lPos, 23, 10, PositionCondition.GrassOrDirtAround),
                            new SettlerActivityTakeInHand(lItem.getType(), lItem.getData().getData()),
                            new SettlerActivityPlaceBlock());
                    lDone = true;
                } else if (lRnd == 2 && hasAtleastItems(Material.YELLOW_FLOWER, 1)) {
                    ItemStack lItem = getFirstItem(Material.YELLOW_FLOWER);
                    addActivityForNow(
                            new SettlerActivityFindRandomPath(lPos, 23, 10, PositionCondition.GrassOrDirtAround),
                            new SettlerActivityTakeInHand(lItem.getType(), lItem.getData().getData()),
                            new SettlerActivityPlaceBlock());
                    lDone = true;
                }
                lCount++;
            } while (!lDone && lCount <= 6);
            if (!lDone) {
                if (goWalkingCount > 0) {
                    goWalkingCount--;
                    addActivityForNow(new SettlerActivityFindRandomPath(23, 10, PositionCondition.None));
                } else if (getSaplingsCount > 0) {
                    goWalkingCount = 10;
                    getSaplingsCount = 0;
                } else {
                    getSaplingsCount++;
                    addActivityForNow(
                            new SettlerActivityWalkToTarget(getBedPosition()),
                            new SettlerActivityGetItemsFromChest(Material.SAPLING, 10, 0),
                            new SettlerActivityGetItemsFromChest(Material.RED_ROSE, 10, 0),
                            new SettlerActivityGetItemsFromChest(Material.YELLOW_FLOWER, 10, 0));
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
