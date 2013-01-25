/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import com.mahn42.framework.BlockPosition;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerShepherd extends Settler {

    public static final String typeName = "Shepherd";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerShepherd.class;
        profession.name = typeName;
        profession.frameMaterial = Material.SHEARS;
        profession.armor.add(new SettlerProfession.Item(Material.SHEARS, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        profession.output.add(new ItemStack(Material.WOOL));
        register(profession);
    }

    public SettlerShepherd() {
        super(typeName);
        fItemsToCollect.add(Material.WOOL);
        fItemsToCollect.add(Material.SHEARS);
        fPutInChestItems.add(new PutInChestItem(Material.WOOL, 0));
    }

    @Override
    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
        super.runInternal(aTask, aAccess);
        runFindSheep(aTask, aAccess);
    }

    public void runFindSheep(SettlerTask aTask, SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            BlockPosition lPos = getPosition();
            int lRadius = 10;
            if (getFrameConfig() == Rotation.FLIPPED) {
                lPos = getBedPosition();
                lRadius = 23;
            }
            if (!existsTaggedActivity("FindSheep")) {
                Collection<SettlerAccess.EntityState> lStates = aAccess.getEntityStatesNearby(getPosition(), 42, EntityType.SHEEP);
                if (!lStates.isEmpty()) {
                    boolean lFound = false;
                    for (SettlerAccess.EntityState lState : lStates) {
                        if ((Boolean) lState.props.get("isAdult") && !(Boolean) lState.props.get("isSheared")) {
                            lFound = true;
                            if (lState.pos.nearly(getPosition(), 2)) {
                                addActivityForNow(
                                        "FindSheep",
                                        new SettlerActivitySwingArm(5),
                                        new SettlerActivityShearSheep(lState.id),
                                        new SettlerActivityCollectItems(fItemsToCollect));
                            } else if (canWalkTo(lState.pos)) {
                                addActivityForNow(
                                        new SettlerActivityWalkToTarget(lState.pos));
                            }
                            break;
                        }
                    }
                    if (!lFound) {
                        // walk a bit
                        addActivityForNow(new SettlerActivityFindRandomPath(lPos, lRadius, 10, PositionCondition.None));
                    }
                } else {
                    // walk a bit
                    addActivityForNow(new SettlerActivityFindRandomPath(lPos, lRadius, 10, PositionCondition.None));
                }
            } else {
                // walk a bit
                addActivityForNow(new SettlerActivityFindRandomPath(lPos, lRadius, 10, PositionCondition.None));
            }
        }

    }

    @Override
    public String getFrameConfigName() {
        switch (fFrameConfig) {
            case NONE:
                return "shear sheeps";
            case COUNTER_CLOCKWISE:
                return "shear sheeps";
            case FLIPPED:
                return "shear sheeps nearly";
            case CLOCKWISE:
                return "shear sheeps";
            default:
                return "";
        }
    }
}
