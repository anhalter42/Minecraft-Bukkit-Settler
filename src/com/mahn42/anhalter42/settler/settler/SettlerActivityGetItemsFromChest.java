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
public class SettlerActivityGetItemsFromChest extends SettlerActivity {

    public static final String TYPE = "GetItemsFromChest";
    public Material material;
    public int amount = -1; // -1 means all
    public int keep = 0; // behalte in chest

    public SettlerActivityGetItemsFromChest() {
        type = TYPE;
    }

    public SettlerActivityGetItemsFromChest(Material aMaterial, int aAmount, int aKeep) {
        type = TYPE;
        material = aMaterial;
        amount = aAmount;
        keep = aKeep;
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        aMap.put("material", material.getId());
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
        final int lAmount = amount;

        runTaskLater(new Runnable() {
            @Override
            public void run() {
                int lCount = lAmount;
                for (BlockPosition lPos : lChestPoss) {
                    BlockState lState = lPos.getBlock(lSettler.getWorld()).getState();
                    if (lState instanceof Chest) {
                        Chest lChest = (Chest) lState;
                        Inventory lInv = lChest.getInventory();
                        List<ItemStack> lRemovedItemsByMaterial = InventoryHelper.removeItemsByMaterial(lInv, lMat, lCount); //removeItems(lInv, lMat, lCount);
                        for(ItemStack lItem : lRemovedItemsByMaterial) {
                            lSettler.insertItems(lItem);
                            lCount -= lItem.getAmount();
                        }
                        if (lCount <= 0) {
                            break;
                        }
                    }
                }
            }
        });
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " " +  material.name();
    }

}
