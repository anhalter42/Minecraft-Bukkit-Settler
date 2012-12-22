/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerSynchronTask implements Runnable {

    protected boolean fIsRunning = false;
    protected ArrayList<Settler> settlersForEntity = new ArrayList<Settler>();

    public void addSettlerForEntity(Settler aSettler) {
        synchronized (settlersForEntity) {
            settlersForEntity.add(aSettler);
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (!fIsRunning) {
                try {
                    fIsRunning = true;
                    synchronized (settlersForEntity) {
                        for (Settler lSettler : settlersForEntity) {
                            lSettler.checkForBecomeEntity();
                        }
                        settlersForEntity.clear();
                    }
                    List<World> lWorlds = SettlerPlugin.plugin.getServer().getWorlds();
                    for (World lWorld : lWorlds) {
                        SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
                        lAccess.runSynchron();
                    }
                } catch (Exception ex) {
                    SettlerPlugin.plugin.getLogger().throwing(getClass().getName(), null, ex);
                }
            }
            fIsRunning = false;
        }
    }
}
