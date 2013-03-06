/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.framework.Building;
import com.mahn42.framework.BuildingBlock;
import com.mahn42.framework.BuildingDB;
import com.mahn42.framework.BuildingHandlerBase;
import com.mahn42.framework.InventoryHelper;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author andre
 */
public class SettlerBuildingHandler extends BuildingHandlerBase {

    @Override
    public JavaPlugin getPlugin() {
        return SettlerPlugin.plugin;
    }

    @Override
    public Building insert(Building aBuilding) {
        SettlerBuildingDB lDB = (SettlerBuildingDB) getDB(aBuilding.world);
        SettlerBuilding lBuilding = new SettlerBuilding();
        lBuilding.cloneFrom(aBuilding);
        BuildingBlock lDBlock = lBuilding.getBlock("frame");
        if (lDBlock != null) {
            SettlerPlugin.plugin.getLogger().info(lDBlock.toString());
            SettlerProfession lProf = SettlerPlugin.plugin.getProfessionFromFrame(lBuilding.description, lDBlock.material);
            if (lProf == null) {
                return null;
            }
            if (lProf.buildingDescriptionPattern != null
                    && !lBuilding.description.name.matches(lProf.buildingDescriptionPattern)) {
                lBuilding.sendToPlayer("&cProfession %s need a building like %s!", lProf.name, lProf.buildingDescriptionPattern);
                return null;
            } else {
                lBuilding.basicProfession = lProf.name;
                lBuilding.name = lBuilding.basicProfession;
                String lParts[] = lBuilding.description.name.split("\\.");
                //Settler.Lodge.1.X1
                if (lParts.length > 2 && !lParts[2].isEmpty() && lParts[2].charAt(0) >= '0' && lParts[2].charAt(0) <= '9') {
                    int lCount = Integer.parseInt(lParts[2]);
                    lBuilding.settlerCount = lCount;
                    BuildingBlock lChestBlock = lBuilding.getBlock("chest");
                    if (lChestBlock != null) {
                        Chest lChest = (Chest) lChestBlock.position.getBlock(lBuilding.world).getState();
                        Inventory lChestInv = lChest.getInventory();
                        for (SettlerProfession.Item lItem : lProf.armor) {
                            if (lItem.needed) {
                                if (!InventoryHelper.hasAtleastItems(lChestInv, lItem.item.getType(), lItem.item.getAmount())) {
                                    lBuilding.sendToPlayer("&cYou need at least %d of %s!", lItem.item.getAmount(), lItem.item.getType().toString());
                                    return null;
                                }
                            }
                        }
                    }
                }
            }
        }
        lDB.addRecord(lBuilding);
        SettlerPlugin.plugin.getServer().getScheduler().runTaskLaterAsynchronously(SettlerPlugin.plugin, new SettlerBuildingTask(SettlerBuildingTask.Kind.Initialize, lBuilding), 10);
        return lBuilding;
    }

    @Override
    public boolean remove(Building aBuilding) {
        SettlerBuilding lBuilding = (SettlerBuilding) aBuilding;
        SettlerPlugin.plugin.getServer().getScheduler().runTaskLaterAsynchronously(SettlerPlugin.plugin, new SettlerBuildingTask(SettlerBuildingTask.Kind.Destroy, lBuilding), 1);
        return super.remove(aBuilding);
    }

    @Override
    public BuildingDB getDB(World aWorld) {
        return SettlerPlugin.plugin.settlerBuildingDB.getDB(aWorld);
    }
}
