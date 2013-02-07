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

    public SettlerActivityFindRandomPath(BlockPosition aStart, int aRadius, int aAttempts, Settler.PositionCondition aCondition) {
        type = TYPE;
        radius = aRadius;
        attempts = aAttempts;
        condition = aCondition;
        startPos = aStart;
    }
    
    public int radius = 42;
    public int attempts = 10;
    public BlockPosition startPos;
    public BlockPosition position;
    public boolean started = false;
    public boolean found = false;
    public Settler.PositionCondition condition = Settler.PositionCondition.None;
    
    protected int currentAttempt = 0;

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        aMap.put("radius", radius);
        aMap.put("attempts", attempts);
        aMap.put("condition", condition.toString());
        if (startPos != null) {
            aMap.put("startPos", startPos.toCSV(","));
        }
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
        lGet = aMap.get("startPos");
        if (lGet != null) {
            startPos = new BlockPosition();
            startPos.fromCSV(lGet.toString(), "\\,");
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (!started && aSettler.hasEntity() && currentAttempt <= attempts) {
            final Settler lSettler = aSettler;
            final SettlerAccess lAccess = aAccess;
            started = true;
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    currentAttempt++;
                    position = lSettler.findRandomWalkToPosition(startPos, lAccess.random, radius, 1, condition);
                    if (position == null) {
                        started = false;
                    }
                    found = true;
                }
            });
        } else if (found) {
            if (position != null) {
                aSettler.addActivityForNow(control.tag, new SettlerActivityWalkToTarget(position));
                control.success = true;
            } else {
                control.success = false;
                Framework.plugin.log("settler", "no path for settler " + aSettler.getSettlerName() + " found! " + condition);
                if (condition == Settler.PositionCondition.None) {
                    aSettler.addActivityForNow(control.tag, new SettlerActivityFindRandomTeleport(3, 10));
                }
            }
        }
        return found;
    }
}
