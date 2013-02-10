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
import com.mahn42.framework.WorldScanner;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerWoodcutter extends Settler {

    public static final String typeName = "Woodcutter";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerWoodcutter.class;
        profession.name = typeName;
        profession.frameMaterial = Material.IRON_AXE;
        profession.armor.add(new SettlerProfession.Item(Material.IRON_AXE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        profession.output.add(new ItemStack(Material.LOG));
        profession.output.add(new ItemStack(Material.SAPLING));
        profession.output.add(new ItemStack(Material.APPLE));
        register(profession);
        SettlerActivity.registerActivity(SettlerActivityWoodcutterBreakLog.TYPE, SettlerActivityWoodcutterBreakLog.class);
    }

    public SettlerWoodcutter() {
        super(typeName);
        fItemsToCollect.add(Material.LOG);
        fItemsToCollect.add(Material.SAPLING);
        fItemsToCollect.add(Material.APPLE);
        fPutInChestItems.add(new PutInChestItem(Material.LOG, 0));
        fPutInChestItems.add(new PutInChestItem(Material.SAPLING, 0));
        fPutInChestItems.add(new PutInChestItem(Material.APPLE, 0));
        fResetOnNight = false;
    }

    @Override
    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            BlockPosition lPos = getPosition();
            if (getFrameConfig() == Rotation.FLIPPED) {
                lPos = getWorkPosition();
            }
            addActivityForNow(
                    new SettlerActivityFindRandomPath(lPos, SettlerPlugin.plugin.configDefaultPathRadius, SettlerPlugin.plugin.configDefaultPathAttempts, PositionCondition.Tree),
                    new SettlerActivityWoodcutterBreakLog());
        }
        super.runInternal(aTask, aAccess);
    }

    @Override
    public void runCollectItems(SettlerTask aTask, SettlerAccess aAccess) {
        if (!existsTaggedActivity("BreakTree")) {
            super.runCollectItems(aTask, aAccess);
        }
    }

    public static class SettlerActivityWoodcutterBreakLog extends SettlerActivity {

        public static final String TYPE = "WoodcutterBreakLog";

        public SettlerActivityWoodcutterBreakLog() {
            type = TYPE;
        }

        @Override
        public boolean run(SettlerAccess aAccess, Settler aSettler) {
            List<BlockPosition> lFindBlocks = aSettler.findBlocks(Material.LOG, 5);
            for (BlockPosition lPos : lFindBlocks) {
                List<BlockPosition> lTreePoss = WorldScanner.getTreePoss(aSettler.getWorld(), lPos);
                if (!lTreePoss.isEmpty()) {
                    aSettler.addActivityForNow(
                            new SettlerActivityNothing(lTreePoss.size()*20)
                            //new SettlerActivitySitDown(lTreePoss.size()*20),
                            //new SettlerActivityStandUp()
                            );
                    for (BlockPosition lP : lTreePoss) {
                        aSettler.addActivityForNow(
                                "BreakTree",
                                new SettlerActivitySwingArm(20),
                                new SettlerActivityBreakBlock(lP));
                    }
                    aSettler.addActivityForNow(
                            new SettlerActivityWalkToTarget(lPos));
                    break;
                }
            }
            return true;
        }
    }

    @Override
    public String getFrameConfigName() {
        switch (fFrameConfig) {
            case NONE:
                return "chop down everywhere";
            case COUNTER_CLOCKWISE:
                return "chop down everywhere";
            case FLIPPED:
                return "chop down near home";
            case CLOCKWISE:
                return "chop down everywhere";
            default:
                return "";
        }
    }
}
