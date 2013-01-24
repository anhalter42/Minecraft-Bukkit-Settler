/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import java.util.Map;

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
    
    public boolean entityDead = false;

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
        if (!entityDead && aSettler.hasEntity()) {
            if (waitTicks <= 0) {
                waitTicks = 5;
                //final NPCEntityPlayer lPlayer = aSettler.fEntity;
                final Settler lSettler = aSettler;
                runTaskLater(new Runnable() {
                    @Override
                    public void run() {
                        entityDead = lSettler.fight(entityId);
                        /*
                        lPlayer.swingArm();
                        boolean lfound = false;
                        List<LivingEntity> lEntities = lPlayer.getAsPlayer().getWorld().getLivingEntities();
                        for (LivingEntity lEntity : lEntities) {
                            if (lEntity.getEntityId() == entityId) {
                                lPlayer.attack(lEntity);
                                
                                int lDamage = 1;
                                ItemStack lItem = lPlayer.getAsPlayer().getItemInHand();
                                if (lItem != null) {
                                    lDamage = Framework.plugin.getItemWeaponLevel(lItem.getType());
                                    int lEnchantmentLevel = lItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                                    if (lEnchantmentLevel > 0) {
                                        lDamage = lDamage + lEnchantmentLevel; //TODO
                                    }
                                }
                                lEntity.damage(lDamage, lPlayer.getAsPlayer());
                                
                                lfound = true;
                                break;
                            }
                        }
                        if (!lfound) {
                            entityDead = true;
                        }
                        */
                    }
                });
            } else {
                waitTicks -= SettlerPlugin.plugin.configSettlerTicks;
            }
        }
        return entityDead;
    }
}
