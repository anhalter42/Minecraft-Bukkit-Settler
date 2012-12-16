/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import java.util.List;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerSynchronTask implements Runnable {

    @Override
    public void run() {
        List<World> lWorlds = SettlerPlugin.plugin.getServer().getWorlds();
        for(World lWorld : lWorlds) {
            SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
            lAccess.runSynchron();
        }
    }
    
}
