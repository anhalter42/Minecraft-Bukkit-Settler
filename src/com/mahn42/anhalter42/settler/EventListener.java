/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.EntityControl;
import com.mahn42.framework.EntityControlPathItemDestination;
import com.mahn42.framework.Framework;
import com.mahn42.framework.npc.entity.NPCEntity;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
        
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent aEvent) {
        World lWorld = aEvent.getWorld();
        Chunk lChunk = aEvent.getChunk();
        SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
        lAccess.addChunkUnLoad(lChunk.getX(), lChunk.getZ());
    }

    /*
     * Entity events
     */
    @EventHandler
    public void onEntityCombust(EntityCombustEvent aEvent) {
        SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent aEvent) {
        LivingEntity lEntity;
        if (aEvent.getEntity() instanceof Player) {
            int health = -1;
            lEntity = (LivingEntity)aEvent.getEntity();
            health = lEntity.getHealth();
            lEntity.getMaxFireTicks();
            lEntity.getMaxHealth();
            SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + " " + health + " of " + lEntity.getMaxHealth() + " " + lEntity.getMaxFireTicks() + aEvent.getClass().getName());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent aEvent) {
        SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent aEvent) {
        //SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent aEvent) {
        SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent aEvent) {
        SettlerPlugin.plugin.getLogger().info(aEvent.getEntityType() + aEvent.getClass().getName());
        Player lEntity = aEvent.getEntity();
        if (lEntity instanceof NPCEntity) {
            //TODO inform over SettlerAccess
            aEvent.getEntity().remove();
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent aEvent) {
        SettlerPlugin.plugin.getLogger().info(aEvent.getPlayer().getDisplayName() + aEvent.getRightClicked().toString() + aEvent.getClass().getName());
        if (aEvent.getRightClicked() instanceof NPCEntity) {
            NPCEntity lNPC = (NPCEntity)aEvent.getRightClicked();
            Player lPlayer = lNPC.getAsPlayer();
            //lPlayer.setItemInHand(new ItemStack(Material.IRON_AXE));
            SettlerPlugin.plugin.getLogger().info(lPlayer.getDisplayName() + ": food=" + lPlayer.getFoodLevel() + " health=" + lPlayer.getHealth() + "/" + lPlayer.getMaxHealth());
            EntityControl lEC = new EntityControl(lPlayer);
            BlockPosition lPos = new BlockPosition(lPlayer.getLocation());
            lPos.add(10, 0, 10);
            lPos.y = lPlayer.getWorld().getHighestBlockYAt(lPos.x, lPos.y);
            lEC.path.add(new EntityControlPathItemDestination(lPos, 1));
            Framework.plugin.getEntityController().add(lEC);
            //lNPC.goSleep();
            //PlayerAnimation.ARM_SWING.play(lPlayer);
            //PlayerAnimation.SNEAK.play(lPlayer);
            //lPlayer.setSneaking(!lPlayer.isSneaking());
        }
    }
}
