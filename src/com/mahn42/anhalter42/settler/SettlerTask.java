/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.settler.Settler;
import java.util.Collection;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerTask implements Runnable {

    protected World fWorld;
    protected SettlerAccess fAccess;
    
    public SettlerTask(World aWorld) {
        fWorld = aWorld;
    }
    
    public World getWorld() {
        return fWorld;
    }
    
    @Override
    public void run() {
        if (fAccess == null) {
            fAccess = SettlerPlugin.plugin.getSettlerAccess(fWorld);
        }
        Collection<? extends Settler> lSettlers = fAccess.getSettlers();
        for(Settler lSettler : lSettlers) {
            if (lSettler.isActive()) {
                lSettler.run(fAccess);
            }
        }
    }
    
}
