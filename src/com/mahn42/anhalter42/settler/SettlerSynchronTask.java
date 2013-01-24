/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.framework.Framework;
import java.util.List;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerSynchronTask implements Runnable {

    protected boolean fIsRunning = false;

    @Override
    public void run() {
        synchronized (this) {
            if (!fIsRunning) {
                fIsRunning = true;
                try {
                    //Framework.plugin.log("settler", getClass().getSimpleName() + " started.");
                    try {
                        List<World> lWorlds = SettlerPlugin.plugin.getServer().getWorlds();
                        for (World lWorld : lWorlds) {
                            SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
                            if (!lWorld.getPlayers().isEmpty()) {
                                lAccess.runSynchron();
                                SettlerBuildingDB lDB = SettlerPlugin.plugin.settlerBuildingDB.getDB(lWorld);
                                for (SettlerBuilding lBuilding : lDB) {
                                    lBuilding.runCheck();
                                }
                            } else {
                                lAccess.removeAllSettlerEntities();
                            }
                        }
                    } catch (Exception ex) {
                        SettlerPlugin.plugin.getLogger().throwing(getClass().getName(), null, ex);
                    }
                } finally {
                    fIsRunning = false;
                    //Framework.plugin.log("settler", getClass().getSimpleName() + " ended.");
                }
            }
        }
    }
}
