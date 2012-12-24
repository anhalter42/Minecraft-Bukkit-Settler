/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.BuildingBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerBuildingTask implements Runnable {

    public enum Kind {
        Initialize,
        Check
    }
    
    public Kind kind = Kind.Initialize;
    public SettlerBuilding building;
    
    protected SettlerAccess fAccess;
    
    public SettlerBuildingTask(Kind aKind, SettlerBuilding aBuilding) {
        kind = aKind;
        building = aBuilding;
    }
    
    @Override
    public void run() {
        fAccess = SettlerPlugin.plugin.getSettlerAccess(building.world);
        switch(kind) {
            case Initialize:
                initBuilding();
                break;
            case Check:
                checkBuilding();
                break;
        }
    }
    
    private void initBuilding() {
        //TODO setup settlers
        //building.settlerCount;
        for(int lIndex = 1; lIndex<=building.settlerCount; lIndex++) {
            BuildingBlock lBlock = building.getBlock("bed" + lIndex);
            if (lBlock == null && lIndex == 1) {
                lBlock = building.getBlock("bed");
            }
            if (lBlock != null) {
                Settler lSettler = fAccess.createSettler(building.basicProfession, building.key);
                if (lSettler != null) {
                    lSettler.setPlayerName(building.playerName);
                    lSettler.setSettlerName(SettlerPlugin.plugin.getRandomSettlerName());
                    BlockPosition lPos = lBlock.position.clone();
                    lSettler.setBedPosition(lPos);
                    lPos.add(0, 1, 0);
                    lSettler.setPosition(lPos);
                    lSettler.activate();
                    building.sendToPlayer("Settler %s was born.", lSettler.getDisplayName());
                }
            }
        }
    }

    private void checkBuilding() {
    }

}
