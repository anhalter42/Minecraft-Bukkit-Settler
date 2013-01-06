/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;

/**
 *
 * @author andre
 */
public class SettlerActivitySwingArm extends SettlerActivity {

    public static final String TYPE = "SwingArm";

    public SettlerActivitySwingArm() {
        type = TYPE;
    }

    public SettlerActivitySwingArm(int aMaxTicks) {
        type = TYPE;
        maxTicks = aMaxTicks;
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (aSettler.hasEntity()) {
            final NPCEntityPlayer lPlayer = aSettler.fEntity;
            SettlerPlugin.plugin.getServer().getScheduler().runTaskLater(SettlerPlugin.plugin, new Runnable() {
                @Override
                public void run() {
                    lPlayer.swingArm();
                }
            }, 1);
        }
        return false;
    }
}
