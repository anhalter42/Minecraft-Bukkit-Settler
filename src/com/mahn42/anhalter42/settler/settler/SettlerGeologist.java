/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Framework;
import com.mahn42.framework.SyncBlockList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

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
        profession.input.add(new ItemStack(Material.SIGN));
        register(profession);
        SettlerActivity.registerActivity(SettlerActivityGeologistThinking.TYPE, SettlerActivityGeologistThinking.class);
        SettlerActivity.registerActivity(SettlerActivityGeologistPlaceSign.TYPE, SettlerActivityGeologistPlaceSign.class);
        //SettlerActivity.registerActivity(SettlerActivityGeologistGetSigns.TYPE, SettlerActivityGeologistGetSigns.class);
    }
    public static int chanceToWalk = 60; // 70%
    public static int chanceForSign = 40; // 20%

    public SettlerGeologist() {
        super(typeName);
        fItemsToCollect.add(Material.SIGN);
        fItemsToCollect.add(Material.SIGN_POST);
    }
    protected int dowalk = 4;
    protected int getSignFromChestCount = 0;

    @Override
    public void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            addActivityForNow(new SettlerActivityGeologistThinking());
        }
        super.runInternal(aTask, aAccess);
    }

    public static class SettlerActivityGeologistPlaceSign extends SettlerActivity {

        public static final String TYPE = "GeologistPlaceSign";
        public int radius = 3;

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
            importantMats.add(Material.EMERALD_ORE);
        }
        protected static ArrayList<Material> vegetationMats;

        {
            vegetationMats = new ArrayList<Material>();
            vegetationMats.add(Material.LONG_GRASS);
            vegetationMats.add(Material.SAPLING);
            vegetationMats.add(Material.YELLOW_FLOWER);
            vegetationMats.add(Material.RED_ROSE);
            vegetationMats.add(Material.CACTUS);
            vegetationMats.add(Material.COCOA);
            vegetationMats.add(Material.LEAVES);
            vegetationMats.add(Material.WATER_LILY);
            vegetationMats.add(Material.VINE);
            vegetationMats.add(Material.SNOW);
        }

        @Override
        public boolean run(SettlerAccess aAccess, Settler aSettler) {
            HashMap<Material, Integer> counts = new HashMap<Material, Integer>();
            BlockPosition lPos = aSettler.getPosition();
            World world = aSettler.getWorld();
            Material blockType = lPos.getBlockType(world);
            if (blockType.equals(Material.AIR) || vegetationMats.contains(blockType)) {
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
                Framework.plugin.log("settler", "counts = " + counts);
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
                if (aSettler.removeItems(Material.SIGN, 1) == 1) {
                    SyncBlockList lBS = new SyncBlockList(world);
                    lBS.add(lPos, Material.SIGN_POST, (byte) aAccess.random.nextInt(16), true, 0, lLine1, lLine2, lLine3, lLine4, null, EntityType.UNKNOWN);
                    lBS.execute();
                } else {
                    Framework.plugin.log("settler", "SettlerGeo: no signs!");
                }
            } else {
                Framework.plugin.log("settler", "SettlerGeo: placesign on " + blockType + " at " + lPos + " failed!");
            }
            return true;
        }
    }

    public static class SettlerActivityGeologistThinking extends SettlerActivity {

        public static final String TYPE = "GeologistThinking";

        public SettlerActivityGeologistThinking() {
            type = TYPE;
        }
        public int walkRadius = SettlerPlugin.plugin.configDefaultPathRadius;

        @Override
        public boolean run(SettlerAccess aAccess, Settler aSettler) {
            boolean lFound = false;
            if (aSettler.hasAtleastItems(Material.SIGN, 1)) {
                if (((SettlerGeologist) aSettler).dowalk > 0 || aAccess.random.nextInt(100) < SettlerGeologist.chanceToWalk) {                                       // 50% laufen
                    if (((SettlerGeologist) aSettler).dowalk > 0) {
                        ((SettlerGeologist) aSettler).dowalk--;
                    }
                    aSettler.addActivityForNext(new SettlerActivityFindRandomPath(walkRadius, SettlerPlugin.plugin.configDefaultPathAttempts, PositionCondition.NaturalBlocksAround));
                    lFound = true;
                } else {
                    ((SettlerGeologist) aSettler).dowalk = 2;
                    if (aAccess.random.nextInt(100) < SettlerGeologist.chanceForSign) {
                        aSettler.addActivityForNext(
                                new SettlerActivityGeologistPlaceSign(),
                                new SettlerActivityJump(20));
                    }
                    aSettler.addActivityForNext(
                            new SettlerActivityStartSneaking(),
                            new SettlerActivitySwingArm(40),
                            new SettlerActivityStopSneaking());
                    lFound = true;
                }
            } else {
                if (((SettlerGeologist) aSettler).dowalk > 0) {
                    ((SettlerGeologist) aSettler).dowalk--;
                    aSettler.addActivityForNext(new SettlerActivityFindRandomPath(walkRadius, 10, PositionCondition.None));
                    lFound = true;
                } else {
                    BlockPosition lPos = aSettler.getPosition();
                    BlockPosition lBedPos = aSettler.getBedPosition();
                    if (lBedPos != null) {
                        if (!lBedPos.nearly(lPos, 2)) {
                            aSettler.addActivityForNext(new SettlerActivityWalkToTarget(lBedPos));
                        } else {
                            if (((SettlerGeologist) aSettler).getSignFromChestCount > 0) {
                                ((SettlerGeologist) aSettler).getSignFromChestCount = 0;
                                ((SettlerGeologist) aSettler).dowalk = 5;
                                //aSettler.addActivityForNext(new SettlerActivityNothing(20*60)); //1min
                            } else {
                                ((SettlerGeologist) aSettler).getSignFromChestCount++;
                                aSettler.addActivityForNext(new SettlerActivityGetItemsFromChest(Material.SIGN, 10, 0));
                            }
                        }
                        lFound = true;
                    }
                }
            }
            return lFound;
        }
    }

    @Override
    public String getFrameConfigName() {
        return "searching ores";
    }
}
