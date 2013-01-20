/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerAccess.EntityState;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.framework.BlockPosition;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.EntityType;

/**
 *
 * @author andre
 */
public class SettlerGuard extends Settler {

    public static final String typeName = "Guard";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerGuard.class;
        profession.name = typeName;
        profession.frameMaterial = Material.IRON_SWORD;
        profession.armor.add(new SettlerProfession.Item(Material.IRON_SWORD, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, false));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_HELMET, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_CHESTPLATE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        register(profession);
    }

    public SettlerGuard() {
        super(typeName);
        fItemsToCollect.add(Material.IRON_SWORD);
        fItemsToCollect.add(Material.IRON_BOOTS);
        fItemsToCollect.add(Material.IRON_HELMET);
        fItemsToCollect.add(Material.IRON_CHESTPLATE);
        fItemsToCollect.add(Material.IRON_LEGGINGS);
        fItemsToCollect.add(Material.DIAMOND_SWORD);
        fItemsToCollect.add(Material.LEATHER_BOOTS);
        fItemsToCollect.add(Material.LEATHER_HELMET);
        fItemsToCollect.add(Material.LEATHER_CHESTPLATE);
        fItemsToCollect.add(Material.LEATHER_LEGGINGS);
        fPutInChestItems.add(new PutInChestItem(Material.IRON_SWORD, 0));
        fPutInChestItems.add(new PutInChestItem(Material.IRON_BOOTS, 0));
        fPutInChestItems.add(new PutInChestItem(Material.IRON_HELMET, 0));
        fPutInChestItems.add(new PutInChestItem(Material.IRON_CHESTPLATE, 0));
        fPutInChestItems.add(new PutInChestItem(Material.IRON_LEGGINGS, 0));
        fPutInChestItems.add(new PutInChestItem(Material.DIAMOND_SWORD, 0));
        fPutInChestItems.add(new PutInChestItem(Material.LEATHER_BOOTS, 0));
        fPutInChestItems.add(new PutInChestItem(Material.LEATHER_HELMET, 0));
        fPutInChestItems.add(new PutInChestItem(Material.LEATHER_CHESTPLATE, 0));
        fPutInChestItems.add(new PutInChestItem(Material.LEATHER_LEGGINGS, 0));
        fWorkStart = 12000; // 20:00
        fWorkEnd = 0; // 08:00
    }
    protected static ArrayList<EntityType> monsters = new ArrayList<EntityType>();

    {
        monsters.add(EntityType.BLAZE);
        monsters.add(EntityType.CAVE_SPIDER);
        monsters.add(EntityType.CREEPER);
        monsters.add(EntityType.GHAST);
        monsters.add(EntityType.SILVERFISH);
        monsters.add(EntityType.SKELETON);
        monsters.add(EntityType.SPIDER);
        monsters.add(EntityType.ZOMBIE);
    }

    @Override
    protected void runInternal(SettlerAccess aAccess) {
        if (!existsTaggedActivity("Fight") && (fFrameConfig == Rotation.NONE || fFrameConfig == Rotation.COUNTER_CLOCKWISE)) {
            Collection<EntityState> lEnities = aAccess.getEntityStatesNearby(getPosition(), 20, monsters);
            if (!lEnities.isEmpty()) {
                double dist = Double.MAX_VALUE;
                EntityState lmin = null;
                BlockPosition lPos = getPosition();
                for (EntityState lState : lEnities) {
                    double ld = lState.pos.distance(lPos);
                    if (ld < dist) {
                        lmin = lState;
                    }
                }
                if (lmin != null) {
                    if (dist < 4) {
                        addActivityForNow(
                                "Fight",
                                new SettlerActivityFight(lmin.id, 20));

                    } else {
                        addActivityForNow(
                                "Fight",
                                new SettlerActivityWalkToTarget(lmin.pos, SettlerActivityWalkToTarget.WalkAction.Fight, lmin.id),
                                new SettlerActivityFight(lmin.id, 20));
                    }
                }
            }
        }
        if (isWorkingTime() && getCurrentActivity() == null) {
            if (fFrameConfig == Rotation.NONE || fFrameConfig == Rotation.CLOCKWISE) {
                addActivityForNow(new SettlerActivityFindRandomPath(getBedPosition(), 20, 10, PositionCondition.None));
            }
        }
        super.runInternal(aAccess);
    }

    @Override
    protected void runCheckDamage(SettlerAccess aAccess) {
        if (!existsTaggedActivity("Fight")) {
            super.runCheckDamage(aAccess);
        }
    }

    @Override
    public String getFrameConfigName() {
        switch (fFrameConfig) {
            case NONE:
                return "Walk & Attack";
            case COUNTER_CLOCKWISE:
                return "Stay & Attack";
            case FLIPPED:
                return "Stay & Defense";
            case CLOCKWISE:
                return "Walk & Defense";
            default:
                return "";
        }
    }
}
