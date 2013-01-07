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
public class SettlerActivitySitDown extends SettlerActivity {

    public static final String TYPE = "SitDown";

    public SettlerActivitySitDown() {
        type = TYPE;
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (aSettler.hasEntity()) {
            final NPCEntityPlayer lPlayer = aSettler.fEntity;
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    lPlayer.sitDown();
                }
            });
        }
        return true;
    }
    
}
