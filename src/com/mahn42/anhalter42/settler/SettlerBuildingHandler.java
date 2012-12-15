/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.framework.Building;
import com.mahn42.framework.BuildingDB;
import com.mahn42.framework.BuildingHandlerBase;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author andre
 */
public class SettlerBuildingHandler extends BuildingHandlerBase{
    
    @Override
    public JavaPlugin getPlugin() {
        return SettlerPlugin.plugin;
    }
    
    @Override
    public Building insert(Building aBuilding) {
        SettlerBuildingDB lDB = (SettlerBuildingDB)getDB(aBuilding.world);
        SettlerBuilding lBuilding = new SettlerBuilding();
        lBuilding.cloneFrom(aBuilding);
        lDB.addRecord(lBuilding);
        return lBuilding;
    }

    @Override
    public boolean remove(Building aBuilding) {
        return super.remove(aBuilding);
    }

    @Override
    public BuildingDB getDB(World aWorld) {
        return SettlerPlugin.plugin.settlerBuildingDB.getDB(aWorld);
    }
}
