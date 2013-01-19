/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.EntityReachedPathItemEvent;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import java.util.ArrayList;
import org.bukkit.Chunk;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 *
 * @author andre
 */
public class EventListener implements Listener {

    /*
     * World events
     */
    @EventHandler
    public void chunkLoad(ChunkLoadEvent aEvent) {
        World lWorld = aEvent.getWorld();
        Chunk lChunk = aEvent.getChunk();
        SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
        lAccess.addChunkLoad(lChunk.getX(), lChunk.getZ());
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent aEvent) {
        World lWorld = aEvent.getWorld();
        Chunk lChunk = aEvent.getChunk();
        SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
        lAccess.addChunkUnLoad(lChunk.getX(), lChunk.getZ());
    }

    /*
     @EventHandler(ignoreCancelled = true)
     public void worldLoad(WorldLoadEvent aEvent) {
     World lWorld = aEvent.getWorld();
     SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
     Collection<? extends Settler> lSettlers = lAccess.getSettlers();
     for(Settler lSettler : lSettlers) {
     BlockPosition lPos = lSettler.getPosition();
     if (lPos != null) {
     if (lWorld.isChunkLoaded(lPos.x >> 4, lPos.z >> 4)) {
     lAccess.addChunkLoad(lPos.x >> 4, lPos.z >> 4);
     }
     }
     }
     }

     @EventHandler(ignoreCancelled = true)
     public void worldUnload(WorldUnloadEvent aEvent) {
     World lWorld = aEvent.getWorld();
     SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
     Collection<? extends Settler> lSettlers = lAccess.getSettlers();
     for(Settler lSettler : lSettlers) {
     BlockPosition lPos = lSettler.getPosition();
     if (lPos != null) {
     if (!lWorld.isChunkLoaded(lPos.x >> 4, lPos.z >> 4)) {
     lAccess.addChunkUnLoad(lPos.x >> 4, lPos.z >> 4);
     }
     }
     }
     }
     */

    /*
     * Entity events
     */
    /*
     @EventHandler
     public void onEntityCombust(EntityCombustEvent aEvent) {
     //SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
     }
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent aEvent) {
        Entity lEntity = aEvent.getEntity();
        if (lEntity instanceof NPCEntityPlayer && ((NPCEntityPlayer) lEntity).getDataObject() instanceof Settler) {
            //TODO inform over SettlerAccess
            SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(aEvent.getEntity().getWorld());
            aEvent.setDamage(aEvent.getDamage() / 2); // make a settler a little bit stronger
            if (aEvent instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent lEvent = (EntityDamageByEntityEvent) aEvent;
                lAccess.addSettlerDamage((Settler) ((NPCEntityPlayer) lEntity).getDataObject(), aEvent.getDamage(), lEvent.getDamager().getType(), lEvent.getDamager().getEntityId(), aEvent.getCause(), new BlockPosition(lEvent.getDamager().getLocation()));
            } else {
                lAccess.addSettlerDamage((Settler) ((NPCEntityPlayer) lEntity).getDataObject(), aEvent.getDamage(), EntityType.UNKNOWN, 0, aEvent.getCause(), null);
            }
            //aEvent.getEntity().remove();
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent aEvent) {
        //SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent aEvent) {
        //SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent aEvent) {
        //SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent aEvent) {
        //SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
        Player lEntity = aEvent.getEntity();
        if (lEntity instanceof NPCEntityPlayer && ((NPCEntityPlayer) lEntity).getDataObject() instanceof Settler) {
            //TODO inform over SettlerAccess
            SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(aEvent.getEntity().getWorld());
            lAccess.addSettlerDied((Settler) ((NPCEntityPlayer) lEntity).getDataObject());
            //lEntity.remove();
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent aEvent) {
        //SettlerPlugin.plugin.getLogger().info(aEvent.getPlayer().getDisplayName() + aEvent.getRightClicked().toString() + aEvent.getClass().getName());
        Entity lEntity = aEvent.getRightClicked();
        if (lEntity instanceof ItemFrame) {
            ArrayList<SettlerBuilding> lBuildings = SettlerPlugin.plugin.settlerBuildingDB.getDB(aEvent.getPlayer().getWorld()).getBuildingsWithBlock(new BlockPosition(lEntity.getLocation()));
            if (lBuildings.size() == 1) {
                Rotation rotation = ((ItemFrame)lEntity).getRotation();
                lBuildings.get(0).setFrameConfig(rotation);
            }
        } else if (lEntity instanceof NPCEntityPlayer) {
            Object lDataObject = ((NPCEntityPlayer)lEntity).getDataObject();
            if (lDataObject instanceof Settler) {
                //TODO configure settler
                ((Settler)lDataObject).dump();
                aEvent.getPlayer().sendMessage("health: " + ((Settler)lDataObject).getHealth() + " food: " + ((Settler)lDataObject).getFoodLevel());
            }
        }
    }

    @EventHandler
    public void onEntityReachTarget(EntityReachedPathItemEvent aEvent) {
        Entity lEntity = aEvent.getEntity();
        World lWorld = lEntity.getWorld();
        SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
        lAccess.addEntityReachedTarget(lEntity);
    }
}
