/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Building;
import com.mahn42.framework.BuildingBlock;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ItemFrame;
import org.bukkit.material.MaterialData;

/**
 *
 * @author andre
 */
public class SettlerBuilding extends Building {

    public int settlerCount = 1;
    public String basicProfession;
    public Rotation frameConfig = Rotation.NONE;

    @Override
    protected void toCSVInternal(ArrayList aCols) {
        super.toCSVInternal(aCols);
        aCols.add(basicProfession);
        aCols.add(settlerCount);
        aCols.add(frameConfig);
    }

    @Override
    protected void fromCSVInternal(DBRecordCSVArray aCols) {
        super.fromCSVInternal(aCols);
        basicProfession = aCols.pop();
        settlerCount = Integer.parseInt(aCols.pop());
        String lS = aCols.pop();
        if (lS != null && !lS.isEmpty()) {
            frameConfig = Rotation.valueOf(lS);
        }
    }

    @Override
    public String getIconName() {
        if (basicProfession != null && !basicProfession.isEmpty()) {
            return (super.getIconName() + "." + basicProfession).toLowerCase();
        } else {
            return super.getIconName();
        }
    }

    public BlockPosition getSign() {
        BlockPosition lPos = null;
        BuildingBlock lBlock = getBlock("sign");
        if (lBlock != null) {
            lPos = lBlock.position.clone();
        } else {
            lBlock = getBlock("frame");
            if (lBlock != null) {
                lPos = lBlock.position.clone();
                lPos.y--;
                Material lMat = lPos.getBlockType(world);
                if (!lMat.equals(Material.SIGN) && !lMat.equals(Material.SIGN_POST) && !lMat.equals(Material.WALL_SIGN)) {
                    lPos.y += 2;
                    lMat = lPos.getBlockType(world);
                    if (!lMat.equals(Material.SIGN) && !lMat.equals(Material.SIGN_POST) && !lMat.equals(Material.WALL_SIGN)) {
                        lPos = null;
                    }
                }
            }
        }
        return lPos;
    }

    public void setFrameConfig(Rotation aRotation) {
        frameConfig = aRotation;
        //TODO change config for settlers
        Collection<? extends Settler> lSettlers = getSettlers();
        for(Settler lSettler : lSettlers) {
            lSettler.setFrameConfig(frameConfig);
        }
    }
    
    public Collection<? extends Settler> getSettlers() {
        SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(world);
        return lAccess.getSettlersForHomeKey(key);
    }
}
