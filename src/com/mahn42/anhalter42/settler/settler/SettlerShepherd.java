/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerProfession;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

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
        register(profession);
    }

    public SettlerShepherd() {
        super(typeName);
        fItemsToCollect.add(Material.WOOL);
        fItemsToCollect.add(Material.SHEARS);
        fPutInChestItems.add(new PutInChestItem(Material.WOOL, 0));
    }

    @Override
    protected void runInternal(SettlerAccess aAccess) {
        super.runInternal(aAccess);
        runFindSheep(aAccess);
    }

    public void runFindSheep(SettlerAccess aAccess) {
        if (isWorkingTime() && getCurrentActivity() == null) {
            if (!existsTaggedActivity("FindSheep")) {
                Collection<SettlerAccess.EntityState> lStates = aAccess.getEntityStatesNearby(getPosition(), 42, EntityType.SHEEP);
                if (!lStates.isEmpty()) {
                    for (SettlerAccess.EntityState lState : lStates) {
                        /*
                        if (lState.pos.nearly(getPosition(), 1)) {
                            addActivityForNow(
                                    "FindSheep",
                                    new SettlerActivityShearSheep(lState.id),
                                    new SettlerActivityCollectItems(fItemsToCollect));
                        } else*/ if (canWalkTo(lState.pos)) {
                            addActivityForNow(
                                    "FindSheep",
                                    new SettlerActivityWalkToTarget(lState.pos),
                                    new SettlerActivityShearSheep(lState.id),
                                    new SettlerActivityCollectItems(fItemsToCollect));
                        }
                    }
                } else {
                    // walk a bit
                    addActivityForNow(new SettlerActivityFindRandomPath());
                }
            }
        }

    }
}
