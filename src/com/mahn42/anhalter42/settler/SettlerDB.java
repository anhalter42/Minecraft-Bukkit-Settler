/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.DBSetWorld;
import com.mahn42.framework.IBeforeAfterExecute;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerDB extends DBSetWorld<SettlerDBRecord> implements IBeforeAfterExecute {
    public SettlerDB() {
        super(SettlerDBRecord.class);
    }
    public SettlerDB(World aWorld, File aFile) {
        super(SettlerDBRecord.class, aFile, aWorld);
    }

    @Override
    public void beforeExecute(Object aObject) {
        //SettlerPlugin.plugin.getLogger().info("prepare for world " + world.getName());
        clear();
        // insert all current settlers
        ArrayList<Settler> lSettlers = SettlerPlugin.plugin.getSettlers(world);
        for(Settler lSettler : lSettlers) {
            SettlerDBRecord lR = new SettlerDBRecord();
            lSettler.serialize(lR);
            addRecord(lR);
        }
    }

    @Override
    public void afterExecute(Object aObject) {
        clear(); // free memory, we only need the running settler instances
    }
}
