/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.SyncBlockList;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerFarmer extends Settler {

    public static final String typeName = "Farmer";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerFarmer.class;
        profession.name = typeName;
        profession.frameMaterial = Material.IRON_HOE;
        profession.armor.add(new SettlerProfession.Item(Material.IRON_HOE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, true));
        profession.output.add(new ItemStack(Material.WHEAT));
        profession.output.add(new ItemStack(Material.SEEDS));
        profession.output.add(new ItemStack(Material.APPLE));
        profession.output.add(new ItemStack(Material.CARROT));
        profession.output.add(new ItemStack(Material.SPECKLED_MELON));
        profession.output.add(new ItemStack(Material.PUMPKIN));
        profession.output.add(new ItemStack(Material.PUMPKIN_SEEDS));
        profession.output.add(new ItemStack(Material.MELON));
        profession.output.add(new ItemStack(Material.MELON_SEEDS));
        profession.output.add(new ItemStack(Material.POTATO));
        register(profession);
        SettlerActivity.registerActivity(SettlerActivityFarmerBreakBlock.TYPE, SettlerActivityFarmerBreakBlock.class);
        SettlerActivity.registerActivity(SettlerActivityFarmerSow.TYPE, SettlerActivityFarmerSow.class);

    }
    public static ArrayList<Material> farmingFruits = new ArrayList<Material>();

    {
        farmingFruits.add(Material.SPECKLED_MELON);
        farmingFruits.add(Material.PUMPKIN);
        farmingFruits.add(Material.MELON_BLOCK);
    }
    public static ArrayList<Material> farmingFruitsWithSeed = new ArrayList<Material>();

    {
        farmingFruitsWithSeed.add(Material.CROPS);
        farmingFruitsWithSeed.add(Material.CARROT);
        farmingFruitsWithSeed.add(Material.POTATO);
        farmingFruitsWithSeed.add(Material.COCOA);
    }

    public SettlerFarmer() {
        super(typeName);
        fItemsToCollect.add(Material.WHEAT);
        fItemsToCollect.add(Material.SEEDS);
        fItemsToCollect.add(Material.APPLE);
        fItemsToCollect.add(Material.CARROT);
        fItemsToCollect.add(Material.CARROT_ITEM);
        fItemsToCollect.add(Material.SPECKLED_MELON);
        fItemsToCollect.add(Material.PUMPKIN);
        fItemsToCollect.add(Material.PUMPKIN_SEEDS);
        fItemsToCollect.add(Material.MELON);
        fItemsToCollect.add(Material.MELON_SEEDS);
        fItemsToCollect.add(Material.POTATO);
        fItemsToCollect.add(Material.POTATO_ITEM);
        fItemsToCollect.add(Material.COCOA);
        fItemsToCollect.add(Material.INK_SACK);
        fPutInChestItems.add(new PutInChestItem(Material.WHEAT, 0));
        fPutInChestItems.add(new PutInChestItem(Material.SEEDS, 10));
        fPutInChestItems.add(new PutInChestItem(Material.APPLE, 0));
        fPutInChestItems.add(new PutInChestItem(Material.CARROT, 10));
        fPutInChestItems.add(new PutInChestItem(Material.CARROT_ITEM, 10));
        fPutInChestItems.add(new PutInChestItem(Material.SPECKLED_MELON, 10));
        fPutInChestItems.add(new PutInChestItem(Material.PUMPKIN, 10));
        fPutInChestItems.add(new PutInChestItem(Material.PUMPKIN_SEEDS, 10));
        fPutInChestItems.add(new PutInChestItem(Material.MELON, 10));
        fPutInChestItems.add(new PutInChestItem(Material.MELON_SEEDS, 10));
        fPutInChestItems.add(new PutInChestItem(Material.POTATO, 10));
        fPutInChestItems.add(new PutInChestItem(Material.POTATO_ITEM, 10));
        fPutInChestItems.add(new PutInChestItem(Material.COCOA, 10));
        fPutInChestItems.add(new PutInChestItem(Material.INK_SACK, 10));
    }

    @Override
    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            addActivityForNow(
                    new SettlerActivityFindRandomPath(getBedPosition(), 23, 10, PositionCondition.FarmingAround),
                    new SettlerActivityFarmerBreakBlock());
        }
        super.runInternal(aTask, aAccess);
    }

    public static class SettlerActivityFarmerSow extends SettlerActivityWithPosition {

        public static final String TYPE = "FarmerSow";
        public Material material = Material.SEEDS;

        public SettlerActivityFarmerSow() {
            type = TYPE;
        }

        public SettlerActivityFarmerSow(BlockPosition aPos, Material aMat) {
            type = TYPE;
            target = aPos;
            material = aMat;
        }

        @Override
        public void serialize(Map<String, Object> aMap) {
            super.serialize(aMap);
            if (material != null) {
                aMap.put("material", material.getId());
            }
        }

        @Override
        public void deserialize(Map<String, Object> aMap) {
            super.deserialize(aMap);
            Object lGet = aMap.get("material");
            if (lGet != null) {
                material = Material.getMaterial(Integer.parseInt(lGet.toString()));
            }
        }

        @Override
        public boolean run(SettlerAccess aAccess, Settler aSettler) {
            if (target != null) {
                BlockPosition lPos = target.clone();
                Block block = lPos.getBlock(aSettler.getWorld());
                while (block.getType().equals(Material.AIR)) {
                    lPos.y--;
                    block = lPos.getBlock(aSettler.getWorld());
                }
                SyncBlockList lList = new SyncBlockList(aSettler.getWorld());
                if (block.getType().equals(Material.GRASS) || block.getType().equals(Material.DIRT)) {
                    final NPCEntityPlayer lPlayer = aSettler.fEntity;
                    runTaskLater(new Runnable() {
                        @Override
                        public void run() {
                            lPlayer.swingArm();
                        }
                    });
                    lList.add(lPos, Material.SOIL, (byte) 0, true);
                }
                if (material != null) {
                    Material lMat = material;
                    if (lMat.equals(Material.CROPS)) {
                        lMat = Material.SEEDS;
                    } else if (lMat.equals(Material.CARROT)) {
                        lMat = Material.CARROT_ITEM;
                    } else if (lMat.equals(Material.POTATO)) {
                        lMat = Material.POTATO_ITEM;
                    } else if (lMat.equals(Material.COCOA)) {
                        lMat = Material.INK_SACK;
                    }
                    if (aSettler.hasAtleastItems(lMat, 1)) {
                        if (aSettler.removeItems(lMat, 1) > 0) {
                            lList.add(target, material, (byte)0, true);
                        }
                    }
                }
                lList.execute();
            }
            return true;
        }
    }

    public static class SettlerActivityFarmerBreakBlock extends SettlerActivity {

        public static final String TYPE = "FarmerBreakBlock";

        public SettlerActivityFarmerBreakBlock() {
            type = TYPE;
        }

        @Override
        public boolean run(SettlerAccess aAccess, Settler aSettler) {
            boolean lFound = false;
            for (Material lMat : farmingFruits) {
                List<BlockPosition> lFindBlocks = aSettler.findBlocks(lMat, 5);
                if (!lFindBlocks.isEmpty()) {
                    for (BlockPosition lPos : lFindBlocks) {
                        aSettler.addActivityForNow(
                                "Farming",
                                new SettlerActivityWalkToTarget(lPos),
                                new SettlerActivitySwingArm(20),
                                new SettlerActivityBreakBlock(lPos));
                    }
                    lFound = true;
                }
            }
            if (!lFound) {
                for (Material lMat : farmingFruitsWithSeed) {
                    List<BlockPosition> lFindBlocks = aSettler.findBlocks(lMat, (byte) 7, 5);
                    if (!lFindBlocks.isEmpty()) {
                        for (BlockPosition lPos : lFindBlocks) {
                            aSettler.addActivityForNow(
                                    "Farming",
                                    new SettlerActivityWalkToTarget(lPos),
                                    new SettlerActivitySwingArm(20),
                                    new SettlerActivityBreakBlock(lPos),
                                    new SettlerActivityFarmerSow(lPos, lMat));
                        }
                    }
                }
            }
            return true;
        }
    }
}
