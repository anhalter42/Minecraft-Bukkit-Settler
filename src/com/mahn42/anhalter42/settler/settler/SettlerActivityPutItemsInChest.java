/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.InventoryHelper;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerActivityPutItemsInChest extends SettlerActivity {

    public static final String TYPE = "PutItemsInChest";
    public Material material;
    public byte data = 0;
    public int amount = -1; // -1 means all
    public int keep = 0; // behalte 

    public SettlerActivityPutItemsInChest() {
        type = TYPE;
    }

    public SettlerActivityPutItemsInChest(Material aMaterial, byte aData, int aAmount, int aKeep) {
        type = TYPE;
        material = aMaterial;
        data = aData;
        amount = aAmount;
        keep = aKeep;
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        aMap.put("material", material.getId());
        aMap.put("data", data);
        aMap.put("amount", amount);
        aMap.put("keep", keep);
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        Object lGet = aMap.get("material");
        if (lGet != null) {
            material = Material.getMaterial(Integer.parseInt(lGet.toString()));
        }
        lGet = aMap.get("amount");
        if (lGet != null) {
            amount = Integer.parseInt(lGet.toString());
        }
        lGet = aMap.get("data");
        if (lGet != null) {
            data = Byte.parseByte(lGet.toString());
        }
        lGet = aMap.get("keep");
        if (lGet != null) {
            keep = Integer.parseInt(lGet.toString());
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        final List<BlockPosition> lChestPoss = aSettler.findBlocks(Material.CHEST, 4);
        final Settler lSettler = aSettler;
        final Material lMat = material;
        int lCount = 0;
        for (ItemStack lItem : lSettler.getInventory()) {
            if (lItem != null && lItem.getType().equals(lMat)) {
                lCount += lItem.getAmount();
            }
        }
        if (lCount <= keep) {
            return true; // not enough amount
        }
        lCount -= keep;
        if (amount >= 0) {
            if (lCount > amount) {
                lCount = amount;
            }
        }
        if (lCount > 0) {
            final int lAmount = lCount;

            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    int lCount = lAmount;
                    for (BlockPosition lPos : lChestPoss) {
                        BlockState lState = lPos.getBlock(lSettler.getWorld()).getState();
                        if (lState instanceof Chest) {
                            Chest lChest = (Chest) lState;
                            Inventory lInv = lChest.getInventory();
                            int linserted = InventoryHelper.insertItems(lInv, lMat, lCount);
                            lSettler.removeItems(lMat, linserted);
                            lCount -= linserted;
                            if (lCount <= 0) {
                                break;
                            }
                        }
                    }
                }
            });
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " " + material.toString();
    }
}
