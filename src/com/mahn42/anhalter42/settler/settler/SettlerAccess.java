/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerDB;
import com.mahn42.anhalter42.settler.SettlerDBRecord;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerAccess {
    public World world;
    
    protected boolean finit = false;
    protected final ArrayList<Settler> settlers = new ArrayList<Settler>();
    //protected HashMap<BlockPosition, Settler> settlersByPosition  = new HashMap<BlockPosition, Settler>();
    
    public SettlerAccess(World aWorld) {
        world = aWorld;
    }

    public Collection<? extends Settler> getSettlers() {
        ArrayList<Settler> lResult = new ArrayList<Settler>();
        initialize();
        synchronized(settlers) {
            lResult.addAll(settlers);
        }
        return lResult;
    }

    protected void initialize() {
        if (!finit) {
            synchronized(settlers) {
                settlers.clear();
                SettlerDB lDB = SettlerPlugin.plugin.getSettlerDB(world);
                lDB.load();
                for(SettlerDBRecord lRecord : lDB) {
                    Settler lSettler = createSettlerInternal(lRecord.profession);
                    if (lSettler != null) {
                        lSettler.setWorld(world);
                        lSettler.deserialize(lRecord);
                        addSettlerInternal(lSettler);
                    }
                }
                lDB.clear(); // free memory
            }
            finit = true;
        }
    }
    
    protected void addSettlerInternal(Settler aSettler) {
        settlers.add(aSettler);
    }
    
    public Settler addSettler(Settler aSettler) {
        synchronized(settlers) {
            addSettlerInternal(aSettler);
        }
        return aSettler;
    }
    
    public Settler createSettler(String aProfession, String aHomeKey) {
        Settler lSettler = createSettlerInternal(aProfession);
        if (lSettler != null) {
            lSettler.setHomeKey(aHomeKey);
            addSettler(lSettler);
        }
        return lSettler;
    }
    
    protected Settler createSettlerInternal(String aProfession) {
        Settler lSettler = null;
        Class lSettlerClass = Settler.getSettlerClass(aProfession);
        if (lSettlerClass != null) {
            try {
                lSettler = (Settler)lSettlerClass.newInstance();
                lSettler.setWorld(world);
            } catch (InstantiationException ex) {
                Logger.getLogger(SettlerAccess.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(SettlerAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger(SettlerAccess.class.getName()).log(Level.SEVERE, "Profession " + aProfession + " for settler not known!");
        }
        return lSettler;
    }
}
