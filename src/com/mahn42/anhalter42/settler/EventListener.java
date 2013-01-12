/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.EntityReachedPathItemEvent;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
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
            lAccess.addSettlerDamage((Settler) ((NPCEntityPlayer) lEntity).getDataObject(), aEvent.getDamage(), lEntity.getType(), lEntity.getEntityId(), aEvent.getCause(), new BlockPosition(lEntity.getLocation()));
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
            lEntity.remove();
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent aEvent) {
        SettlerPlugin.plugin.getLogger().info(aEvent.getPlayer().getDisplayName() + aEvent.getRightClicked().toString() + aEvent.getClass().getName());
        /*
        if (aEvent.getRightClicked() instanceof NPCEntityPlayer) {
            NPCEntityPlayer lNPC = (NPCEntityPlayer) aEvent.getRightClicked();
            Player lPlayer = lNPC.getAsPlayer();
            //lPlayer.setItemInHand(new ItemStack(Material.IRON_AXE));
            //lPlayer.setSneaking(!lPlayer.isSneaking());
            lNPC.swingArm();
            lNPC.setMot(0.2f,0,0.2f);
            lPlayer.setCanPickupItems(true);
            SettlerPlugin.plugin.getLogger().info(lPlayer.getDisplayName() + ": food=" + lPlayer.getFoodLevel() + " health=" + lPlayer.getHealth() + "/" + lPlayer.getMaxHealth());
            EntityControl lEC = new EntityControl(lPlayer);
            BlockPosition lPos = new BlockPosition(lPlayer.getLocation());
            lPos.add(10, 0, 10);
            lPos.y = lPlayer.getWorld().getHighestBlockYAt(lPos.x, lPos.z);
            SettlerPlugin.plugin.getLogger().info("send settler to " + lPos);
            lEC.showPath(lPos);
            //lEC.createPath(lPos);
            lEC.path.add(new EntityControlPathItemDestination(lPos, 0.7f));
            Framework.plugin.getEntityController().add(lEC);
            //lNPC.goSleep();
            //PlayerAnimation.ARM_SWING.play(lPlayer);
            //PlayerAnimation.SNEAK.play(lPlayer);
            //lPlayer.setSneaking(!lPlayer.isSneaking());
        }*/
    }
    
    @EventHandler
    public void onEntityReachTarget(EntityReachedPathItemEvent aEvent) {
        Entity lEntity = aEvent.getEntity();
        World lWorld = lEntity.getWorld();
        SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
        lAccess.addEntityReachedTarget(lEntity);
    }
}
