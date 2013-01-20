/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Building;
import com.mahn42.framework.BuildingBlock;
import com.mahn42.framework.Framework;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Sign;

/**
 *
 * @author andre
 */
public class SettlerBuilding extends Building {

    //Meta
    public int settlerCount = 1;
    public String basicProfession;
    public Rotation frameConfig = Rotation.NONE;

    //Runtime
    public int taskCheckCount = 0;
    
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
                //Framework.plugin.log("settler", "sign pos: " + lPos + " mat: " + lMat);
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
        for (Settler lSettler : lSettlers) {
            lSettler.setFrameConfig(frameConfig);
        }
    }

    public Collection<? extends Settler> getSettlers() {
        SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(world);
        return lAccess.getSettlersForHomeKey(key);
    }

    public void runCheck() {
        BlockPosition lSignPos = getSign();
        Collection<? extends Settler> lSettlers;
        lSettlers = getSettlers();
        if (lSignPos != null) {
            Sign lSign = (Sign) lSignPos.getBlock(world).getState();
            lSign.setLine(0, basicProfession);
            lSign.setLine(1, "");
            lSign.setLine(2, "");
            lSign.setLine(3, "");
            if (lSettlers.isEmpty()) {
                lSign.setLine(1, "no settler!");
            } else {
                if (settlerCount == 1) {
                    for (Settler lSettler : lSettlers) {
                        lSign.setLine(1, lSettler.getSettlerName());
                        break;
                    }
                } else {
                    if (settlerCount == lSettlers.size()) {
                        lSign.setLine(1, "all settlers alive");
                    } else {
                        lSign.setLine(1, lSettlers.size() + " settlers of " + settlerCount);
                    }
                }
            }
            if (!lSettlers.isEmpty()) {
                for (Settler lSettler : lSettlers) {
                    lSign.setLine(2, lSettler.getFrameConfigName());
                    break;
                }
            }
            lSign.update();
        }
        taskCheckCount--;
        if (taskCheckCount <= 0 && lSettlers.size() != settlerCount) {
            taskCheckCount = 10;
            SettlerPlugin.plugin.getServer().getScheduler().runTaskAsynchronously(SettlerPlugin.plugin, new SettlerBuildingTask(SettlerBuildingTask.Kind.Check, this));
        }
    }
}
