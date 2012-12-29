/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.BuildingBlock;
import com.mahn42.framework.Framework;
import com.mahn42.framework.Framework.ItemType;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerBuildingTask implements Runnable {

    public enum Kind {
        Initialize,
        Check,
        SettlerDied
    }
    public Kind kind = Kind.Initialize;
    public SettlerBuilding building;
    public Settler settler;
    protected SettlerAccess fAccess;

    public SettlerBuildingTask(Kind aKind, SettlerBuilding aBuilding) {
        kind = aKind;
        building = aBuilding;
    }

    @Override
    public void run() {
        fAccess = SettlerPlugin.plugin.getSettlerAccess(building.world);
        switch (kind) {
            case Initialize:
                initBuilding();
                break;
            case Check:
                checkBuilding();
                break;
            case SettlerDied:
                rebornSettler();
                break;
        }
    }

    private void initBuilding() {
        //TODO setup settlers
        //building.settlerCount;
        Chest lChest = (Chest) building.getBlock("chest").position.getBlock(building.world).getState();
        Inventory lChestInv = lChest.getInventory();
        for (int lIndex = 1; lIndex <= building.settlerCount; lIndex++) {
            BuildingBlock lBlock = building.getBlock("bed" + lIndex);
            if (lBlock == null && lIndex == 1) {
                lBlock = building.getBlock("bed");
            }
            if (lBlock != null) {
                Settler lSettler = fAccess.createSettler(building.basicProfession, building.key);
                if (lSettler != null) {
                    lSettler.setPlayerName(building.playerName);
                    BlockPosition lPos = lBlock.position.clone();
                    lSettler.setBedPosition(lPos);
                    //lPos.add(0, 1, 0);
                    lSettler.setPosition(lPos);
                    bearSettler(lChestInv, lSettler);
                }
            }
        }
    }

    private void checkBuilding() {
    }

    private void rebornSettler() {
        Chest lChest = (Chest) building.getBlock("chest").position.getBlock(building.world).getState();
        Inventory lChestInv = lChest.getInventory();
        bearSettler(lChestInv, settler);
    }
    
    private void bearSettler(Inventory lChestInv, Settler aSettler) {
        if (aSettler.getSettlerName() == null) {
            aSettler.setSettlerName(SettlerPlugin.plugin.getRandomSettlerName());
        }
        SettlerProfession lProf = aSettler.getProf();
        for (SettlerProfession.Item lItem : lProf.armor) {
            if (lChestInv.containsAtLeast(lItem.item, lItem.item.getAmount())) {
                lChestInv.remove(lItem.item);
                aSettler.setArmor(lItem.item);
            }
        }
        aSettler.activate();
        building.sendToPlayer("Settler %s was born.", aSettler.getDisplayName());
    }

}
