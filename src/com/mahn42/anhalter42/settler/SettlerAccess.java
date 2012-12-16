/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.SettlerDB;
import com.mahn42.anhalter42.settler.SettlerDBRecord;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.npc.entity.NPCEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 *
 * @author andre
 */
public class SettlerAccess {
    public World world;
    
    protected boolean finit = false;
    protected final ArrayList<Settler> settlers = new ArrayList<Settler>();
    //protected HashMap<BlockPosition, Settler> settlersByPosition  = new HashMap<BlockPosition, Settler>();
    protected HashMap<Integer, Settler> settlersByEntityId  = new HashMap<Integer, Settler>();
    protected HashMap<String, Settler> settlersByKey  = new HashMap<String, Settler>();
    
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
                if (lDB != null) {
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
            }
            finit = true;
        }
    }
    
    protected void addSettlerInternal(Settler aSettler) {
        settlers.add(aSettler);
        settlersByKey.put(aSettler.getKey(), aSettler);
    }
    
    protected void removeSettlerInternal(Settler aSettler) {
        settlers.remove(aSettler);
        settlersByKey.remove(aSettler.getKey());
        settlersByEntityId.remove(aSettler.getEntityId());
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
    
    protected HashMap<Integer, EntityState> entitiyStates = new HashMap<Integer, EntityState>();
    
    public EntityState getEntityState(int aId) {
        return entitiyStates.get(aId);
    }
    
    public void runSynchron() {
        synchronized(settlers) {
            entitiyStates.clear();
            settlersByEntityId.clear();
            List<Entity> lEntities = world.getEntities();
            for(Entity lEntity : lEntities) {
                EntityState lState = new EntityState(lEntity);
                entitiyStates.put(lEntity.getEntityId(), lState);
                if (lEntity instanceof LivingEntity) {
                    //TODO
                    if (lEntity instanceof NPCEntity) {
                        Settler lSettler = (Settler)((NPCEntity)lEntity).getDataObject();
                        if (lSettler != null) {
                            settlersByEntityId.put(lEntity.getEntityId(), lSettler);
                            lSettler.setEntityId(lEntity.getEntityId());
                        }
                    }
                } else {
                    //TODO
                }
            }
        }
    }
    
    public class EntityState {
        public int id;
        public EntityType type;
        public BlockPosition pos;
        
        public EntityState(Entity aEntity) {
            id = aEntity.getEntityId();
            type = aEntity.getType();
            pos = new BlockPosition(aEntity.getLocation());
        }
    }
}
