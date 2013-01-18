/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.SyncBlockList;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerActivityPlaceBlock extends SettlerActivity {

    public static final String TYPE = "WalkToTarget";
    public BlockPosition target;

    public SettlerActivityPlaceBlock() {
        type = TYPE;
    }

    public SettlerActivityPlaceBlock(BlockPosition aPos) {
        type = TYPE;
        target = aPos.clone();
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        if (target != null) {
            aMap.put("target", target.toCSV(","));
        }
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        Object lObj = aMap.get("target");
        if (lObj != null) {
            target = new BlockPosition();
            target.fromCSV(lObj.toString(), "\\,");
        } else {
            target = null;
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        BlockPosition lSPos = aSettler.getPosition();
        BlockPosition lPos = target == null ? lSPos : target;
        if (lPos.nearly(lSPos, 4)) {
            Material lType = lPos.getBlockType(aSettler.getWorld());
            if (lType.equals(Material.AIR)
                    || lType.equals(Material.LONG_GRASS)
                    || lType.equals(Material.RED_ROSE)
                    || lType.equals(Material.YELLOW_FLOWER)
                    || lType.equals(Material.DEAD_BUSH)) { // dort darf nix sein
                ItemStack lItemInHand = aSettler.getItemInHand();
                if (lItemInHand != null) {
                    SyncBlockList lList = new SyncBlockList(aSettler.getWorld());
                    lList.add(target == null ? aSettler.getPosition() : target, lItemInHand.getType(), lItemInHand.getData().getData());
                    lList.execute();
                    aSettler.setItemInHand(null);
                    control.success = true;
                } else {
                    control.success = false;
                }
            } else {
                control.success = false;
            }
        } else {
            control.success = false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
