/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class SettlerActivityStartSneaking extends SettlerActivity {

    public static final String TYPE = "StartSneaking";

    public SettlerActivityStartSneaking() {
        type = TYPE;
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (aSettler.hasEntity()) {
            final Player lPlayer = aSettler.fEntity.getAsPlayer();
            SettlerPlugin.plugin.getServer().getScheduler().runTaskLater(SettlerPlugin.plugin, new Runnable() {
                @Override
                public void run() {
                    lPlayer.setSneaking(true);
                }
            }, 1);
        }
        return true;
    }
    
}
