/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerAccess.EntityState;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.EntityControl;
import com.mahn42.framework.EntityControlPathItemDestination;
import com.mahn42.framework.Framework;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class SettlerActivityFollowTarget extends SettlerActivity {

    public static final String TYPE = "FollowTarget";

    public int entityId;
    public boolean started = false;
    
    public SettlerActivityFollowTarget() {
        type = TYPE;
    }
    
    public SettlerActivityFollowTarget(int aEntityId) {
        type = TYPE;
        entityId = aEntityId;
    }
    
    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        boolean ready;
        EntityState entityState = aAccess.getEntityState(entityId);
        if (entityState != null) {
            final EntityControl lEC = new EntityControl(aSettler.fEntity.getAsPlayer());
            BlockPosition target = entityState.pos;
            lEC.path.add(new EntityControlPathItemDestination(target, aSettler.fEntity.getAsPlayer().getWalkSpeed() /*aSettler.getWalkSpeed()*/));
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    if (((Player) lEC.entity).isSleeping()) {
                        ((NPCEntityPlayer) lEC.entity).awake();
                    }
                    Framework.plugin.getEntityController().add(lEC);
                    started = true;
                }
            });
            ready = false;
        } else {
            Framework.plugin.log("settler","FollowTarget entity not found! " + entityId);
            ready = true;
        }
        return ready;
    }

    @Override
    public void deactivate(Settler aSettler) {
        super.deactivate(aSettler);
        if (started) {
            Framework.plugin.getEntityController().remove(aSettler.fEntityId);
            if (aSettler.hasEntity()) {
                final NPCEntityPlayer lPlayer = aSettler.fEntity;
                runTaskLater(new Runnable() {
                    @Override
                    public void run() {
                        lPlayer.stop();
                        started = true;
                    }
                });
            }
            started = false;
        }
    }
    
}
