/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class SettlerActivityStopSneaking extends SettlerActivity {

    public static final String TYPE = "StopSneaking";

    public SettlerActivityStopSneaking() {
        type = TYPE;
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (aSettler.hasEntity()) {
            final Player lPlayer = aSettler.fEntity.getAsPlayer();
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    lPlayer.setSneaking(false);
                }
            });
        }
        return true;
    }
    
}
