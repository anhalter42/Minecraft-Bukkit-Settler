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
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.BuildingBlock;
import com.mahn42.framework.Framework;
import com.mahn42.framework.InventoryHelper;
import com.mahn42.framework.SyncBlockList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author andre
 */
public class SettlerGeologist extends Settler {

    public static final String typeName = "Geologist";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerGeologist.class;
        profession.name = typeName;
        profession.frameMaterial = Material.SIGN;
        profession.armor.add(new SettlerProfession.Item(Material.IRON_PICKAXE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        profession.inventory.add(new SettlerProfession.Item(Material.SIGN, false));
        register(profession);
        SettlerActivity.registerActivity(SettlerActivityGeologistThinking.TYPE, SettlerActivityGeologistThinking.class);
        SettlerActivity.registerActivity(SettlerActivityGeologistPlaceSign.TYPE, SettlerActivityGeologistPlaceSign.class);
        SettlerActivity.registerActivity(SettlerActivityGeologistGetSigns.TYPE, SettlerActivityGeologistGetSigns.class);
    }

    public SettlerGeologist() {
        super(typeName);
    }
    protected int dowalk = 4;

    @Override
    public void run(SettlerAccess aAccess) {
        if (getCurrentActivity() == null) {
            addActivityForNow(new SettlerActivityGeologistThinking());
        }
        super.run(aAccess);
    }

    public static class SettlerActivityGeologistGetSigns extends SettlerActivity {
        public static final String TYPE = "GeologistGetSigns";

        public SettlerActivityGeologistGetSigns() {
            type = TYPE;
        }

        @Override
        public boolean run(SettlerAccess aAccess, Settler aSettler) {
            SettlerBuildingDB lDB = SettlerPlugin.plugin.getSettlerBuildingDB(aSettler.getWorld());
            SettlerBuilding lBuilding = lDB.getRecord(aSettler.getHomeKey());
            BuildingBlock lChestB = lBuilding.getBlock("chest");
            Chest lChest = (Chest) lChestB.position.getBlock(aSettler.getWorld()).getState();
            Inventory lInv = lChest.getInventory();
            if (InventoryHelper.hasAtleastItems(lInv, Material.SIGN_POST, 1)) {
                int lRemoved = InventoryHelper.removeItems(lInv, Material.SIGN_POST, 10);
                aSettler.removeItems(Material.SIGN_POST, lRemoved);
            }
            return true;
        }
    }

    public static class SettlerActivityGeologistPlaceSign extends SettlerActivity {

        public static final String TYPE = "GeologistPlaceSign";
        public int radius = 16;

        public SettlerActivityGeologistPlaceSign() {
            type = TYPE;
        }
        protected static ArrayList<Material> importantMats;

        {
            importantMats = new ArrayList<Material>();
            importantMats.add(Material.DIAMOND_ORE);
            importantMats.add(Material.GOLD_ORE);
            importantMats.add(Material.IRON_ORE);
            importantMats.add(Material.COAL_ORE);
            importantMats.add(Material.LAPIS_ORE);
        }

        @Override
        public boolean run(SettlerAccess aAccess, Settler aSettler) {
            HashMap<Material, Integer> counts = new HashMap<Material, Integer>();
            BlockPosition lPos = aSettler.getPosition();
            World world = aSettler.getWorld();
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    for (int y = -lPos.y; y < 0; y++) {
                        Block lBlock = lPos.getBlockAt(world, x, y, z);
                        Material lMat = lBlock.getType();
                        if (importantMats.contains(lMat)) {
                            Integer count = counts.get(lMat);
                            if (count == null) {
                                count = new Integer(0);
                                counts.put(lMat, count);
                            }
                            count++;
                            counts.put(lMat, count);
                        }
                    }
                }
            }
            SettlerPlugin.plugin.getLogger().info("counts = " + counts);
            Set<Material> keySet = counts.keySet();
            String lLine1 = null, lLine2 = null, lLine3 = null, lLine4 = null;
            for (Material lMat : keySet) {
                String ls = "" + counts.get(lMat) + " " + Framework.plugin.getText(lMat.name());
                if (lLine1 == null) {
                    lLine1 = ls;
                } else if (lLine2 == null) {
                    lLine2 = ls;
                } else if (lLine3 == null) {
                    lLine3 = ls;
                } else if (lLine4 == null) {
                    lLine4 = ls;
                }
            }
            if (aSettler.removeItems(Material.SIGN_POST, 1) == 0) {
                SyncBlockList lBS = new SyncBlockList(world);
                lBS.add(lPos, Material.SIGN_POST, (byte) (new Random()).nextInt(16), true, 0, lLine1, lLine2, lLine3, lLine4, null, EntityType.UNKNOWN);
                lBS.execute();
            }
            return true;
        }
    }

    public static class SettlerActivityGeologistThinking extends SettlerActivity {

        public static final String TYPE = "GeologistThinking";

        public SettlerActivityGeologistThinking() {
            type = TYPE;
        }

        @Override
        public boolean run(SettlerAccess aAccess, Settler aSettler) {
            Random lRnd = new Random();
            boolean lFound = false;
            if (aSettler.hasAtleastItems(Material.SIGN_POST, 1)) {
                if (((SettlerGeologist) aSettler).dowalk > 0 || lRnd.nextInt(100) < 50) {                                       // 50% laufen
                    ((SettlerGeologist) aSettler).dowalk--;
                    BlockPosition lPos = aSettler.getPosition();
                    lPos.add(lRnd.nextInt(20) - 10, 0, lRnd.nextInt(20) - 10);
                    lPos.y = aSettler.getWorld().getHighestBlockYAt(lPos.x, lPos.z);
                    Block lBlock = lPos.getBlock(aSettler.getWorld());
                    if (!lBlock.isLiquid()) {
                        while (lPos.y > 0 && !lBlock.getType().isSolid()) {
                            lPos.y--;
                            lBlock = lPos.getBlock(aSettler.getWorld());
                        }
                        lFound = aSettler.canWalkTo(lPos);
                        if (lFound) {
                            aSettler.addActivityForNext(new SettlerActivityWalkToTarget(lPos));
                        }
                    }
                } else {                                                            // 30% klopfen
                    ((SettlerGeologist) aSettler).dowalk = 2;
                    if (lRnd.nextInt(100) < 100) {                                   // 20% Schild setzen
                        aSettler.addActivityForNext(
                                new SettlerActivityGeologistPlaceSign(),
                                new SettlerActivityJump(20)
                                );
                    }
                    aSettler.addActivityForNext(
                            new SettlerActivityStartSneaking(),
                            new SettlerActivitySwingArm(40),
                            new SettlerActivityStopSneaking());
                    lFound = true;
                }
            } else {
                BlockPosition lPos = aSettler.getPosition();
                BlockPosition lBedPos = aSettler.getBedPosition();
                if (lBedPos != null) {
                    if (!lBedPos.nearly(lPos)) {
                        aSettler.addActivityForNext(new SettlerActivityWalkToTarget(lBedPos));
                    } else {
                        aSettler.addActivityForNext(new SettlerActivityGeologistGetSigns());
                    }
                    lFound = true;
                }
            }
            return lFound;
        }
    }
}
