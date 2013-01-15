/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.EntityControl;
import com.mahn42.framework.EntityControlPathItemDestination;
import com.mahn42.framework.Framework;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import java.util.Map;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class SettlerActivityWalkToTarget extends SettlerActivity {

    public static final String TYPE = "WalkToTarget";
    
    public BlockPosition target;
    public boolean started = false;
    public boolean reached = false;
    
    protected BlockPosition lastPos;
    protected int samePosTicks = 0;

    public SettlerActivityWalkToTarget() {
        type = TYPE;
    }

    public SettlerActivityWalkToTarget(BlockPosition aPos) {
        type = TYPE;
        target = aPos.clone();
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        if (target != null) {
            aMap.put("target", target.toCSV(","));
        }
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        Object lObj = aMap.get("target");
        if (lObj != null) {
            target = new BlockPosition();
            target.fromCSV(lObj.toString(), "\\,");
        } else {
            target = null;
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (target == null) {
            return true;
        }
        if (!started && !reached && target != null && aSettler.hasEntity()) {
            final EntityControl lEC = new EntityControl(aSettler.fEntity.getAsPlayer());
            double lDistance = aSettler.getPosition().distance(target);
            maxTicks = (int)(lDistance * 20 / aSettler.fEntity.getAsPlayer().getWalkSpeed());
            if (maxTicks > 1200) {
                maxTicks = 1200;
            }
            lEC.path.add(new EntityControlPathItemDestination(target, aSettler.fEntity.getAsPlayer().getWalkSpeed() /*aSettler.getWalkSpeed()*/));
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    if (((Player)lEC.entity).isSleeping()) {
                        ((NPCEntityPlayer)lEC.entity).awake();
                    }
                    Framework.plugin.getEntityController().add(lEC);
                    started = true;
                }
            });
        } else {
            BlockPosition lPos = aSettler.getPosition();
            if (lastPos != null) {
                if (lastPos.equals(lPos) && !lPos.nearly(target, 3)) {
                    samePosTicks += SettlerPlugin.plugin.configSettlerTicks;
                    if (samePosTicks > (20 * 30)) { // 30s?
                        SettlerPlugin.plugin.getLogger().info("Settler " + aSettler.getSettlerName() + " is hanging... " + lPos);
                        aSettler.addActivityForNow(new SettlerActivityFindRandomTeleport());
                        //reached = true;
                    }
                } else {
                    samePosTicks = 0;
                }
            }
            lastPos = lPos;
        }
        return reached;
    }

    @Override
    public void targetReached(SettlerAccess aAccess, Settler aSettler) {
        super.targetReached(aAccess, aSettler);
        //todo check if really reached?
        reached = true;
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

    @Override
    public String toString() {
        return super.toString() + " to:" + target;
    }
}
