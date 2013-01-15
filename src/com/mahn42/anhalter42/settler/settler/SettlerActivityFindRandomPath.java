/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Framework;
import java.util.Map;

/**
 *
 * @author andre
 */
public class SettlerActivityFindRandomPath extends SettlerActivity {

    public static final String TYPE = "FindRandomPath";

    public SettlerActivityFindRandomPath() {
        type = TYPE;
    }

    public SettlerActivityFindRandomPath(int aRadius, int aAttempts, Settler.PositionCondition aCondition) {
        type = TYPE;
        radius = aRadius;
        attempts = aAttempts;
        condition = aCondition;
    }
    public int radius = 42;
    public int attempts = 10;
    public BlockPosition position;
    public boolean started = false;
    public boolean found = false;
    public Settler.PositionCondition condition = Settler.PositionCondition.None;

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        aMap.put("radius", radius);
        aMap.put("attempts", attempts);
        aMap.put("condition", condition.toString());
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        Object lGet = aMap.get("radius");
        if (lGet != null) {
            radius = Integer.parseInt(lGet.toString());
        }
        lGet = aMap.get("attempts");
        if (lGet != null) {
            attempts = Integer.parseInt(lGet.toString());
        }
        lGet = aMap.get("condition");
        if (lGet != null) {
            condition = Settler.PositionCondition.valueOf(lGet.toString());
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (!started && aSettler.hasEntity()) {
            final Settler lSettler = aSettler;
            final SettlerAccess lAccess = aAccess;
            started = true;
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    position = lSettler.findRandomWalkToPosition(lAccess.random, radius, attempts, condition);
                    found = true;
                }
            });
        } else if (found) {
            if (position != null) {
                aSettler.addActivityForNow(new SettlerActivityWalkToTarget(position));
                control.success = true;
            } else {
                control.success = false;
                Framework.plugin.log("settler", "no path for settler " + aSettler.getSettlerName() + " found! " + condition);
                if (condition == Settler.PositionCondition.None) {
                    aSettler.addActivityForNow(new SettlerActivityFindRandomTeleport(3, 10));
                }
            }
        }
        return found;
    }
}
