/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerBuilding;
import com.mahn42.anhalter42.settler.SettlerBuildingDB;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.BuildingBlock;
import com.mahn42.framework.Framework;
import com.mahn42.framework.InventoryHelper;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerWearer extends Settler {

    public static final String typeName = "Wearer";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerWearer.class;
        profession.name = typeName;
        profession.frameMaterial = Material.LEATHER_BOOTS;
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_HELMET, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, true));
        for (Material lMat : Material.values()) {
            profession.output.add(new ItemStack(lMat));
        }
        register(profession);
    }

    public SettlerWearer() {
        super(typeName);
        for (Material lMat : Material.values()) {
            fItemsToCollect.add(lMat);
            fPutInChestItems.add(new PutInChestItem(lMat, 0));
        }
    }
    protected boolean fGetFromLodgeToShelf = true;

    protected static class ChestCollector {

        public SettlerBuilding from;
        public BlockPosition pos;
        public ArrayList<Material> materials = new ArrayList<Material>();

        public ChestCollector(SettlerBuilding aFrom, BlockPosition aPos) {
            from = aFrom;
            pos = aPos;
        }

        public ChestCollector(SettlerBuilding aFrom, BlockPosition aPos, Material aMat) {
            from = aFrom;
            pos = aPos;
            materials.add(aMat);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ChestCollector) {
                return ((ChestCollector) o).pos.equals(pos);
            }
            return super.equals(o);
        }
    }

    @Override
    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            SettlerBuildingDB lDB = SettlerPlugin.plugin.getSettlerBuildingDB(getWorld());
            ArrayList<SettlerBuilding> lShelfs = lDB.getBuildings("Settler.StorageShelf.", getBedPosition(), 42);
            ArrayList<SettlerBuilding> lLodges = lDB.getBuildings("Settler.Lodge.", getWorkPosition(), 80);
            if (lLodges.isEmpty()) {
                Framework.plugin.log("settler", "wearer " + getSettlerName() + " can not find a lodge!");
                addActivityForNow(
                        new SettlerActivityFindRandomPath(getWorkPosition(), 23, 10, PositionCondition.None));
            } else {
                boolean lFound = false;
                do {
                    SettlerBuilding lLodge = lLodges.get(aAccess.random.nextInt(lLodges.size()));
                    lLodges.remove(lLodge);
                    SettlerProfession lProfession = SettlerPlugin.plugin.getProfession(lLodge.basicProfession);
                    if (lProfession != null) {
                        BuildingBlock lBlock = lLodge.getBlock("chest");
                        if (lBlock != null) {
                            BlockState lState = lBlock.position.getBlock(getWorld()).getState();
                            if (lState instanceof Chest) {
                                Chest lChest = (Chest) lState;
                                if (fGetFromLodgeToShelf && !lProfession.output.isEmpty()) {
                                    fGetFromLodgeToShelf = false;
                                    ArrayList<Material> lMats = new ArrayList<Material>();
                                    for (ItemStack lItem : lProfession.output) {
                                        if (InventoryHelper.hasAtleastItems(lChest.getInventory(), lItem.getType(), 1)) {
                                            lFound = true;
                                            lMats.add(lItem.getType());
                                        }
                                    }
                                    if (lFound) {
                                        for (Material lMat : lMats) {
                                            addActivityForNow(
                                                    new SettlerActivityPutItemsInChest(lMat, (byte) 0, 100, 0));
                                        }
                                        if (lShelfs.isEmpty()) {
                                            addActivityForNow(
                                                    new SettlerActivityWalkToTarget(getBedPosition()));
                                        } else {
                                            addActivityForNow(
                                                    new SettlerActivityWalkToTarget(
                                                    lShelfs.get(aAccess.random.nextInt(lShelfs.size())).getBlock("chest1.1").position));
                                        }
                                        for (Material lMat : lMats) {
                                            addActivityForNow(
                                                    new SettlerActivityGetItemsFromChest(lMat, 100, 0));
                                        }
                                        addActivityForNow(new SettlerActivityWalkToTarget(new BlockPosition(lChest.getLocation())));
                                    } else {
                                        Framework.plugin.log("settler_x", "wearer " + getSettlerName() + " lodge " + lLodge.getName() + " profession " + lProfession
                                                + " chest has no output materials!");
                                    }
                                } else if (!lProfession.input.isEmpty()) {
                                    fGetFromLodgeToShelf = true;
                                    ArrayList<Material> lMats = new ArrayList<Material>();
                                    for (ItemStack lItem : lProfession.input) {
                                        if (!InventoryHelper.hasAtleastItems(lChest.getInventory(), lItem.getType(), 20)) {
                                            lFound = true;
                                            lMats.add(lItem.getType());
                                        }
                                    }
                                    if (!lMats.isEmpty()) {
                                        lFound = false;
                                        ArrayList<ChestCollector> lFroms = new ArrayList<ChestCollector>();
                                        if (lShelfs.isEmpty()) {
                                            SettlerBuilding lHome = getHomeBuilding();
                                            ChestCollector lCol = new ChestCollector(lHome, lHome.getBlock("chest").position);
                                            for (Material lMat : lMats) {
                                                if (lHome.hasChestWith(lMat, 1)) {
                                                    lCol.materials.add(lMat);
                                                }
                                            }
                                            if (!lCol.materials.isEmpty()) {
                                                lFroms.add(lCol);
                                            }
                                        } else {
                                            for (SettlerBuilding lShelf : lShelfs) {
                                                ChestCollector lCol = new ChestCollector(lShelf, lShelf.edge1.getMidPoint(lShelf.edge2));
                                                for (Material lMat : lMats) {
                                                    if (lShelf.hasChestWith(lMat, 1)) {
                                                        lCol.materials.add(lMat);
                                                    }
                                                }
                                                if (!lCol.materials.isEmpty()) {
                                                    lFroms.add(lCol);
                                                }
                                            }
                                        }
                                        if (!lFroms.isEmpty()) {
                                            lFound = true;
                                            for (Material lMat : lMats) {
                                                addActivityForNow(
                                                        new SettlerActivityPutItemsInChest(lMat, (byte) 0, 100, 0));
                                            }
                                            addActivityForNow(
                                                    new SettlerActivityWalkToTarget(lLodge.edge1.getMidPoint(lLodge.edge2)));
                                            for (ChestCollector lFrom : lFroms) {
                                                for (Material lMat : lFrom.materials) {
                                                    addActivityForNow(
                                                            new SettlerActivityGetItemsFromChest(lMat, 100, 0));
                                                }
                                                addActivityForNow(
                                                        new SettlerActivityWalkToTarget(lFrom.pos));
                                            }
                                            Framework.plugin.log("settler", "wearer " + getSettlerName() + " lodge " + lLodge.getName() + " profession " + lProfession
                                                    + " will bring input materials.");
                                        } else {
                                            Framework.plugin.log("settler", "wearer " + getSettlerName() + " lodge " + lLodge.getName() + " profession " + lProfession
                                                    + " no shelf chest for input materials!");
                                            Framework.plugin.log("settler", "wearer: shelfs " + lShelfs.size() + " mats " + lMats.size());
                                            if (!lShelfs.isEmpty()) {
                                                Framework.plugin.log("settler", "wearer: shelfs chest " + lShelfs.get(0).getBlocks(Material.CHEST));
                                            }
                                        }
                                    } else {
                                        Framework.plugin.log("settler", "wearer " + getSettlerName() + " lodge " + lLodge.getName() + " profession " + lProfession
                                                + " chest has enough input materials!");
                                    }
                                }
                            } else {
                                Framework.plugin.log("settler", "wearer " + getSettlerName() + " lodge " + lLodge.getName() + " profession " + lProfession
                                        + " at chest is no chest!");
                            }
                        } else {
                            Framework.plugin.log("settler", "wearer " + getSettlerName() + " lodge " + lLodge.getName() + " profession " + lProfession
                                    + " has no chest!");
                        }
                    } else {
                        Framework.plugin.log("settler_x", "wearer " + getSettlerName() + " lodge " + lLodge.getName() + " profession " + lProfession
                                + " has no profession!");
                    }
                } while (!lFound && !lLodges.isEmpty());
                if (!lFound) {
                    Framework.plugin.log("settler", "wearer " + getSettlerName() + " can not find a lodge with in/output!");
                    addActivityForNow(
                            new SettlerActivityFindRandomPath(getWorkPosition(), 23, 10, PositionCondition.None));
                }
            }
        }
        super.runInternal(aTask, aAccess);
    }
}
