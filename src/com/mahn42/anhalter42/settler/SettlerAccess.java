/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class SettlerAccess {

    public World world;
    public Random random = new Random();
    protected boolean finit = false;
    protected final ArrayList<Settler> settlers = new ArrayList<Settler>();
    //protected HashMap<BlockPosition, Settler> settlersByPosition  = new HashMap<BlockPosition, Settler>();
    protected HashMap<Integer, Settler> settlersByEntityId = new HashMap<Integer, Settler>();
    protected HashMap<String, Settler> settlersByKey = new HashMap<String, Settler>();
    public long timeOffset;

    public SettlerAccess(World aWorld) {
        world = aWorld;
    }

    public List<? extends Settler> getSettlers() {
        ArrayList<Settler> lResult = new ArrayList<Settler>();
        initialize();
        synchronized (settlers) {
            lResult.addAll(settlers);
        }
        return lResult;
    }

    public Collection<? extends Settler> getSettlersForHomeKey(String aHomeKey) {
        ArrayList<Settler> lResult = new ArrayList<Settler>();
        initialize();
        synchronized (settlers) {
            for (Settler lSettler : settlers) {
                if (lSettler.getHomeKey() == null ? aHomeKey == null : lSettler.getHomeKey().equals(aHomeKey)) {
                    lResult.add(lSettler);
                }
            }
        }
        return lResult;
    }

    protected void initialize() {
        if (!finit) {
            synchronized (settlers) {
                settlers.clear();
                SettlerDB lDB = SettlerPlugin.plugin.getSettlerDB(world);
                if (lDB != null) {
                    lDB.load();
                    for (SettlerDBRecord lRecord : lDB) {
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
        if (!settlers.contains(aSettler)) {
            settlers.add(aSettler);
            settlersByKey.put(aSettler.getKey(), aSettler);
        }
    }

    protected void removeSettlerInternal(Settler aSettler) {
        settlers.remove(aSettler);
        settlersByKey.remove(aSettler.getKey());
        settlersByEntityId.remove(aSettler.getEntityId());
        aSettler.removed();
    }

    public void removeSettler(Settler aSettler) {
        synchronized (settlers) {
            removeSettlerInternal(aSettler);
        }
    }

    public Settler addSettler(Settler aSettler) {
        synchronized (settlers) {
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
                lSettler = (Settler) lSettlerClass.newInstance();
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
    final protected HashMap<Integer, EntityState> entitiyStates = new HashMap<Integer, EntityState>();

    public EntityState getEntityState(int aId) {
        EntityState get;
        synchronized (entitiyStates) {
            get = entitiyStates.get(aId);
        }
        return get;
    }

    public Collection<EntityState> getEntityStatesNearby(BlockPosition aPos, int aRadius, EntityType aType) {
        ArrayList<EntityState> lRes = new ArrayList<EntityState>();
        synchronized (entitiyStates) {
            for (EntityState lState : entitiyStates.values()) {
                if (lState.type.equals(aType) && lState.pos.nearly(aPos, aRadius)) {
                    lRes.add(lState);
                }
            }
        }
        return lRes;
    }

    public Collection<EntityState> getEntityStatesNearby(BlockPosition aPos, int aRadius, List<EntityType> aTypes) {
        ArrayList<EntityState> lRes = new ArrayList<EntityState>();
        synchronized (entitiyStates) {
            for (EntityState lState : entitiyStates.values()) {
                if (aTypes.contains(lState.type) && lState.pos.nearly(aPos, aRadius)) {
                    lRes.add(lState);
                }
            }
        }
        return lRes;
    }

    public Collection<EntityState> getEntityStatesNearby(BlockPosition aPos, int aRadius, Collection<Material> aMats) {
        ArrayList<EntityState> lRes = new ArrayList<EntityState>();
        ArrayList<EntityState> lFounds = new ArrayList<EntityState>();
        synchronized (entitiyStates) {
            for (EntityState lState : entitiyStates.values()) {
                if (lState.type.equals(EntityType.DROPPED_ITEM)
                        && (aMats == null || aMats.contains(lState.material))
                        && lState.pos.nearly(aPos, aRadius)) {
                    lFounds.add(lState);
                }
            }
        }
        while (!lFounds.isEmpty()) {
            double lDist = Double.MAX_VALUE;
            EntityState lStateNear = lFounds.get(0);
            for (EntityState lState : lFounds) {
                double distance = aPos.distance(lState.pos);
                if (distance < lDist) {
                    lStateNear = lState;
                }
            }
            lRes.add(lStateNear);
            lFounds.remove(lStateNear);
        }
        return lRes;
    }
    final protected ArrayList<Settler> settlersForEntity = new ArrayList<Settler>();

    public void addSettlerForEntity(Settler aSettler) {
        synchronized (settlersForEntity) {
            if (!settlersForEntity.contains(aSettler)) {
                settlersForEntity.add(aSettler);
            }
        }
    }
    public boolean shouldRun = false;

    public void runSynchron() {
        shouldRun = true;
        List<Entity> lEntities = world.getEntities();
        synchronized (settlersForEntity) {
            for (Settler lSettler : settlersForEntity) {
                lSettler.checkForBecomeEntity();
            }
            settlersForEntity.clear();
        }
        synchronized (entitiyStates) {
            entitiyStates.clear();
        }
        synchronized (settlers) {
            settlersByEntityId.clear();
            for (Entity lEntity : lEntities) {
                EntityState lState = new EntityState(lEntity);
                synchronized (entitiyStates) {
                    entitiyStates.put(lEntity.getEntityId(), lState);
                }
                if (lEntity instanceof LivingEntity) {
                    //TODO
                    if (lEntity instanceof NPCEntityPlayer && ((NPCEntityPlayer) lEntity).getDataObject() instanceof Settler) {
                        Settler lSettler = (Settler) ((NPCEntityPlayer) lEntity).getDataObject();
                        if (!lSettler.hasEntity() || lSettler.getEntityId() == lEntity.getEntityId()) {
                            settlersByEntityId.put(lEntity.getEntityId(), lSettler);
                            //lSettler.setEntityId(lEntity.getEntityId());
                            lSettler.updateFromEntity((NPCEntityPlayer) lEntity);
                            lSettler.updateToEntity((NPCEntityPlayer) lEntity);
                        }
                    }
                } else {
                    //TODO
                }
            }
        }
    }

    public void removeAllSettlerEntities() {
        shouldRun = false;
        List<Entity> lEntities = world.getEntities();
        synchronized (settlersForEntity) {
            settlersForEntity.clear();
        }
        synchronized (entitiyStates) {
            entitiyStates.clear();
        }
        synchronized (settlers) {
            settlersByEntityId.clear();
            for (Entity lEntity : lEntities) {
                if (lEntity instanceof NPCEntityPlayer && ((NPCEntityPlayer) lEntity).getDataObject() instanceof Settler) {
                    lEntity.remove();
                }
            }
        }
        synchronized (diedSettler) {
            diedSettler.clear();
        }
        synchronized (reachedTargetSettler) {
            reachedTargetSettler.clear();
        }
        synchronized (entitiyStates) {
            entitiyStates.clear();
        }
    }
    final protected ArrayList<Settler> diedSettler = new ArrayList<Settler>();

    public void addSettlerDied(Settler aSettler) {
        synchronized (diedSettler) {
            diedSettler.add(aSettler);
        }
        aSettler.deactivate();
        String lHome = aSettler.getHomeKey();
        if (lHome != null && !lHome.isEmpty()) {
            SettlerBuilding lBuilding = SettlerPlugin.plugin.getSettlerBuildingDB(aSettler.getWorld()).getRecord(lHome);
            if (lBuilding != null) {
                SettlerBuildingTask lTask = new SettlerBuildingTask(SettlerBuildingTask.Kind.SettlerDied, lBuilding);
                lTask.settler = aSettler;
                SettlerPlugin.plugin.getServer().getScheduler().runTaskLaterAsynchronously(SettlerPlugin.plugin, lTask, 20 * 60);
            }
        }
        //aSettler.setEntityId(0);
    }

    public Settler getSettlerById(int lEntityId) {
        Settler lSettler = null;
        synchronized (settlersByEntityId) {
            lSettler = settlersByEntityId.get(lEntityId);
        }
        return lSettler;
    }
    protected ArrayList<Settler> reachedTargetSettler = new ArrayList<Settler>();

    public void addEntityReachedTarget(Entity lEntity) {
        Settler lSettler = getSettlerById(lEntity.getEntityId());
        if (lSettler != null) {
            synchronized (reachedTargetSettler) {
                reachedTargetSettler.add(lSettler);
            }
        }
    }
    protected boolean fEnabled = true;

    public boolean isEnabled() {
        return fEnabled;
    }

    public void enable() {
        fEnabled = true;
    }

    public void disable() {
        fEnabled = false;
    }

    public Collection<Settler> getSettlers(String aName) {
        ArrayList<Settler> lRes = new ArrayList<Settler>();
        Collection<? extends Settler> lSettlers = getSettlers();
        for (Settler lSettler : lSettlers) {
            if (aName.equalsIgnoreCase(lSettler.getSettlerName())) {
                lRes.add(lSettler);
            }
        }
        return lRes;
    }

    public static class SettlerDamage {

        public Settler settler;
        public int damage;
        public EntityType entityType;
        public int entityId;
        public EntityDamageEvent.DamageCause cause;
        public BlockPosition entityPos;
    }
    protected ArrayList<SettlerDamage> settlerDamage = new ArrayList<SettlerDamage>();

    public void addSettlerDamage(Settler aSettler, int aDamage, EntityType aType, int aId, EntityDamageEvent.DamageCause aCause, BlockPosition aEntityPos) {
        SettlerDamage lDamage = new SettlerDamage();
        lDamage.settler = aSettler;
        lDamage.damage = aDamage;
        lDamage.entityType = aType;
        lDamage.entityId = aId;
        lDamage.cause = aCause;
        lDamage.entityPos = aEntityPos;
        synchronized (settlerDamage) {
            settlerDamage.add(lDamage);
        }
    }

    public class EntityState {

        public int id;
        public EntityType type;
        public BlockPosition pos;
        public int health;
        public int foodLevel;
        public float saturation;
        public Material material;
        public int amount;
        public HashMap<String, Object> props = new HashMap<String, Object>();
        public ItemStack item;

        public EntityState(Entity aEntity) {
            id = aEntity.getEntityId();
            type = aEntity.getType();
            pos = new BlockPosition(aEntity.getLocation());
            if (aEntity instanceof LivingEntity) {
                health = ((LivingEntity) aEntity).getHealth();
                if (aEntity instanceof Player) {
                    foodLevel = ((Player) aEntity).getFoodLevel();
                    saturation = ((Player) aEntity).getSaturation();
                }
            } else if (aEntity instanceof Item) {
                ItemStack itemStack = ((Item) aEntity).getItemStack();
                material = itemStack.getType();
                amount = itemStack.getAmount();
                item = new ItemStack(itemStack);
            }
            switch (type) {
                case SHEEP:
                    props.put("isAdult", ((Sheep) aEntity).isAdult());
                    props.put("isSheared", ((Sheep) aEntity).isSheared());
                    break;
                case PIG:
                    props.put("isAdult", ((Pig) aEntity).isAdult());
                    break;
                case COW:
                    props.put("isAdult", ((Cow) aEntity).isAdult());
                    break;
            }
        }
    }

    /*
     protected static class ChunkLoad {

     public enum Kind {

     Loaded,
     Unloaded
     }
     public int x;
     public int z;
     public Kind kind = Kind.Loaded;

     public ChunkLoad(int aX, int aZ, Kind aKind) {
     x = aX;
     z = aZ;
     kind = aKind;
     }
     }
     final protected ArrayList<ChunkLoad> chunkLoads = new ArrayList<ChunkLoad>();

     public void addChunkUnLoad(int aX, int aZ) {
     //SettlerPlugin.plugin.getLogger().info("chunk unloaded " + aX + " " + aZ);
     synchronized (chunkLoads) {
     for (ChunkLoad lLoad : chunkLoads) {
     if (lLoad.x == aX && lLoad.z == aZ) {
     lLoad.kind = ChunkLoad.Kind.Unloaded;
     return;
     }
     }
     chunkLoads.add(new ChunkLoad(aX, aZ, ChunkLoad.Kind.Unloaded));
     }
     }

     public void addChunkLoad(int aX, int aZ) {
     //SettlerPlugin.plugin.getLogger().info("chunk loaded " + aX + " " + aZ);
     synchronized (chunkLoads) {
     for (ChunkLoad lLoad : chunkLoads) {
     if (lLoad.x == aX && lLoad.z == aZ) {
     lLoad.kind = ChunkLoad.Kind.Loaded;
     return;
     }
     }
     chunkLoads.add(new ChunkLoad(aX, aZ, ChunkLoad.Kind.Loaded));
     }
     }

     public ArrayList<ChunkLoad> retrieveChunkLoads() {
     ArrayList<ChunkLoad> lLoads = new ArrayList<ChunkLoad>();
     synchronized (chunkLoads) {
     lLoads.addAll(chunkLoads);
     chunkLoads.clear();
     }
     return lLoads;
     }
     */
    public ArrayList<Settler> retrieveDiedSettlers() {
        ArrayList<Settler> lSettlers = new ArrayList<Settler>();
        synchronized (diedSettler) {
            lSettlers.addAll(diedSettler);
            diedSettler.clear();
        }
        return lSettlers;
    }

    public ArrayList<Settler> retrieveReachedTargetSettlers() {
        ArrayList<Settler> lSettlers = new ArrayList<Settler>();
        synchronized (reachedTargetSettler) {
            lSettlers.addAll(reachedTargetSettler);
            reachedTargetSettler.clear();
        }
        return lSettlers;
    }

    public ArrayList<SettlerDamage> retrieveDamagedSettlers() {
        ArrayList<SettlerDamage> lDamages = new ArrayList<SettlerDamage>();
        synchronized (settlerDamage) {
            lDamages.addAll(settlerDamage);
            settlerDamage.clear();
        }
        return lDamages;
    }
}
