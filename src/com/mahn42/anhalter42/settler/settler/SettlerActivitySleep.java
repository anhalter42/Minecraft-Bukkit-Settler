/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;

/**
 *
 * @author andre
 */
public class SettlerActivitySleep extends SettlerActivity {

    public static final String TYPE = "Sleep";
    
    public boolean started = false;

    public SettlerActivitySleep() {
        type = TYPE;
    }

    public SettlerActivitySleep(int aMaxTicks) {
        type = TYPE;
        maxTicks = aMaxTicks;
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (!started && aSettler.hasEntity()) {
            final NPCEntityPlayer lPlayer = aSettler.fEntity;
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    lPlayer.goSleep();
                    started = true;
                }
            });
        }
        return false;
    }
    
}
