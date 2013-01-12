/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import java.util.Map;

/**
 *
 * @author andre
 */
public class SettlerActivityFindRandomTeleport extends SettlerActivity {

    public static final String TYPE = "FindRandomTeleport";

    public SettlerActivityFindRandomTeleport() {
        type = TYPE;
    }

    public SettlerActivityFindRandomTeleport(int aRadius, int aAttempts) {
        type = TYPE;
        radius = aRadius;
        attempts = aAttempts;
    }

    public int radius = 2;
    public int attempts = 10;
    public BlockPosition position;
    public boolean started = false;
    public boolean found = false;

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        aMap.put("radius", radius);
        aMap.put("attempts", attempts);
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
                    position = lSettler.findRandomTeleportToPosition(lAccess.random, radius, attempts);
                    found = true;
                }
            });
        } else if (found) {
            if (position != null) {
                aSettler.addActivityForNow(new SettlerActivityTeleport(position));
            }
        }
        return found;
    }
}
