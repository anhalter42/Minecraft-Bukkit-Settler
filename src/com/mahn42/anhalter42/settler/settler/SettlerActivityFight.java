/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.framework.Framework;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import java.util.List;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerActivityFight extends SettlerActivity {

    public static final String TYPE = "Fight";
    //Meta
    public int entityId;
    //Runtime
    public int waitTicks = 0;

    public SettlerActivityFight() {
        type = TYPE;
    }

    public SettlerActivityFight(int aEntityId, int aMaxTicks) {
        type = TYPE;
        maxTicks = aMaxTicks;
        entityId = aEntityId;
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        aMap.put("entityId", entityId);
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        Object lGet = aMap.get("entityId");
        if (lGet != null) {
            entityId = Integer.parseInt(lGet.toString());
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (aSettler.hasEntity()) {
            if (waitTicks <= 0) {
                waitTicks = 5;
                final NPCEntityPlayer lPlayer = aSettler.fEntity;
                runTaskLater(new Runnable() {
                    @Override
                    public void run() {
                        //lPlayer.swingArm();
                        List<LivingEntity> lEntities = lPlayer.getAsPlayer().getWorld().getLivingEntities();
                        for (LivingEntity lEntity : lEntities) {
                            if (lEntity.getEntityId() == entityId) {
                                lPlayer.attack(lEntity);
                                /*
                                int lDamage = 1;
                                ItemStack lItem = lPlayer.getAsPlayer().getItemInHand();
                                if (lItem != null) {
                                    lDamage = Framework.plugin.getItemWeaponLevel(lItem.getType());
                                    int lEnchantmentLevel = lItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                                    if (lEnchantmentLevel > 0) {
                                        lDamage = lDamage + lEnchantmentLevel; //TODO
                                    }
                                }
                                lEntity.damage(lDamage, lEntity);
                                */
                                break;
                            }
                        }
                    }
                });
            } else {
                waitTicks -= SettlerPlugin.plugin.configSettlerTicks;
            }
        }
        return false;
    }
}
