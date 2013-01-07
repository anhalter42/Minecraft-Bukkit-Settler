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
public class SettlerActivityJump extends SettlerActivity {

    public static final String TYPE = "Jump";

    public int waitTicks = 0;
    
    public SettlerActivityJump() {
        type = TYPE;
        maxTicks = 1;
    }

    public SettlerActivityJump(int aMaxTicks) {
        type = TYPE;
        maxTicks = aMaxTicks;
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (aSettler.hasEntity() && waitTicks == 0) {
            final NPCEntityPlayer lPlayer = aSettler.fEntity;
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    lPlayer.jump();
                }
            });
            waitTicks = 5;
        } else {
            waitTicks--;
        }
        return false;
    }
    
}
