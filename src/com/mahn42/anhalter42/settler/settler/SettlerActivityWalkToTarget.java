/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.EntityControl;
import com.mahn42.framework.EntityControlPathItemDestination;
import com.mahn42.framework.Framework;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class SettlerActivityWalkToTarget extends SettlerActivityWithPosition {

    public enum WalkAction {
        None,
        SwingArm,
        Fight
    }

    public static final String TYPE = "WalkToTarget";
    // Meta
    public WalkAction action = WalkAction.None;
    public int entityId = 0;
    // Runtime
    public boolean started = false;
    public boolean reached = false;
    protected BlockPosition lastPos;
    protected int samePosTicks = 0;
    protected boolean doorOpened = false;

    public SettlerActivityWalkToTarget() {
        type = TYPE;
    }

    public SettlerActivityWalkToTarget(BlockPosition aPos) {
        type = TYPE;
        target = aPos.clone();
    }

    public SettlerActivityWalkToTarget(BlockPosition aPos, WalkAction aAction, int aEntityId) {
        type = TYPE;
        target = aPos.clone();
        action = aAction;
        entityId = aEntityId;
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        aMap.put("action", action.toString());
        aMap.put("entityId", entityId);
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        Object lObj = aMap.get("action");
        if (lObj != null) {
            action = WalkAction.valueOf(lObj.toString());
        }
        lObj = aMap.get("entityId");
        if (lObj != null) {
            entityId = Integer.parseInt(lObj.toString());
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (target == null) {
            return true;
        }
        if (!started && !reached && target != null && aSettler.hasEntity()) {
            final EntityControl lEC = new EntityControl(aSettler.fEntity.getAsPlayer());
            double lDistance = aSettler.getPosition().distance(target);
            maxTicks = (int) (lDistance * 20 / aSettler.fEntity.getAsPlayer().getWalkSpeed());
            if (maxTicks > 1200) {
                maxTicks = 1200;
            }
            lEC.path.add(new EntityControlPathItemDestination(target, aSettler.fEntity.getAsPlayer().getWalkSpeed() /*aSettler.getWalkSpeed()*/));
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    if (((Player) lEC.entity).isSleeping()) {
                        ((NPCEntityPlayer) lEC.entity).awake();
                    }
                    Framework.plugin.getEntityController().add(lEC);
                    started = true;
                }
            });
        } else {
            final Settler lSettler = aSettler;
            switch (action) {
                case None:
                    break;
                case SwingArm:
                    runTaskLater(new Runnable() {
                        @Override
                        public void run() {
                            if (lSettler.hasEntity()) {
                                lSettler.fEntity.swingArm();
                            }
                        }
                    });
                    break;
                case Fight:
                    if (entityId != 0) {
                        runTaskLater(new Runnable() {
                            @Override
                            public void run() {
                                if (!lSettler.fight(entityId)) {
                                    entityId = 0;
                                }
                            }
                        });
                    }
                    break;
            }
            BlockPosition lPos = aSettler.getPosition();
            if (lastPos != null) {
                if (lastPos.equals(lPos) && !lPos.nearly(target, 2)) {
                    samePosTicks += SettlerPlugin.plugin.configSettlerTicks;
                    if (!doorOpened && samePosTicks > (20 * 2)) { // 2s
                        doorOpened = true;
                        List<BlockPosition> lDoorPoss = aSettler.findBlocks(Material.WOODEN_DOOR, 2);
                        for (BlockPosition lDPos : lDoorPoss) {
                            Block lBlock = lDPos.getBlock(aSettler.getWorld());
                            if ((lBlock.getData() & 0x8) == 0) { // bottom part of door
                                final Block lDoor = lBlock;
                                runTaskLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        lDoor.getWorld().playSound(lDoor.getLocation(), Sound.DOOR_OPEN, 0, 0);
                                        lDoor.setData((byte) (lDoor.getData() | 0x4), true);
                                    }
                                });
                                runTaskLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        lDoor.getWorld().playSound(lDoor.getLocation(), Sound.DOOR_CLOSE, 0, 0);
                                        lDoor.setData((byte) (lDoor.getData() & ~0x4), true);
                                    }
                                }, 40);
                            }
                        }
                    }
                    if (samePosTicks > (20 * 30)) { // 30s?
                        SettlerPlugin.plugin.getLogger().info("Settler " + aSettler.getSettlerName() + " is hanging... " + lPos);
                        aSettler.addActivityForNow(new SettlerActivityFindRandomTeleport(2, 10));
                        //reached = true;
                    }
                } else {
                    samePosTicks = 0;
                    doorOpened = false;
                }
            }
            lastPos = lPos;
        }
        return reached;
    }

    @Override
    public void targetReached(SettlerAccess aAccess, Settler aSettler) {
        super.targetReached(aAccess, aSettler);
        //todo check if really reached?
        reached = true;
    }

    @Override
    public void deactivate(Settler aSettler) {
        super.deactivate(aSettler);
        if (started) {
            Framework.plugin.getEntityController().remove(aSettler.fEntityId);
            if (aSettler.hasEntity()) {
                final NPCEntityPlayer lPlayer = aSettler.fEntity;
                runTaskLater(new Runnable() {
                    @Override
                    public void run() {
                        lPlayer.stop();
                        started = true;
                    }
                });
            }
            started = false;
        }
    }
}
