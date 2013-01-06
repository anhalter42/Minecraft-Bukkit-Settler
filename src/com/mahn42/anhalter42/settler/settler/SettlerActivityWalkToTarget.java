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
import java.util.Map;

/**
 *
 * @author andre
 */
public class SettlerActivityWalkToTarget extends SettlerActivity {

    public static final String TYPE = "WalkToTarget";
    
    public BlockPosition target;
    public boolean started = false;
    public boolean reached = false;

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
        aMap.put("target", target.toCSV(","));
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        String lStr = aMap.get("target").toString();
        if (lStr != null && !lStr.isEmpty()) {
            target = new BlockPosition();
            target.fromCSV(lStr, "\\,");
        } else {
            target = null;
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (!started && !reached && target != null && aSettler.hasEntity()) {
            final EntityControl lEC = new EntityControl(aSettler.fEntity.getAsPlayer());
            lEC.path.add(new EntityControlPathItemDestination(target));
            SettlerPlugin.plugin.getServer().getScheduler().runTaskLater(SettlerPlugin.plugin, new Runnable() {
                @Override
                public void run() {
                    Framework.plugin.getEntityController().add(lEC);
                    started = true;
                }
            }, 1);
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
            started = false;
        }
    }

    @Override
    public String toString() {
        return super.toString() + " to:" + target;
    }
}
