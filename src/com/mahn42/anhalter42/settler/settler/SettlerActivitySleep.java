/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Framework;
import com.mahn42.framework.WorldScanner;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerActivitySleep extends SettlerActivity {

    public static final String TYPE = "Sleep";
    public boolean started = false;

    public SettlerActivitySleep() {
        type = TYPE;
        maxTicks = 8 * 1000; // 8h
    }

    public SettlerActivitySleep(int aMaxTicks) {
        type = TYPE;
        maxTicks = aMaxTicks;
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (!started && aSettler.hasEntity()) {
            final NPCEntityPlayer lPlayer = aSettler.fEntity;
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    World lWorld = lPlayer.getAsPlayer().getWorld();
                    List<BlockPosition> findBlocks = WorldScanner.findBlocks(lWorld, new BlockPosition(lPlayer.getAsPlayer().getLocation()), Material.BED_BLOCK, 3);
                    if (findBlocks.isEmpty()) {
                        Framework.plugin.log("settler", "go sleep no bed found!");
                        lPlayer.goSleep();
                    } else {
                        //Framework.plugin.log("settler", "go sleep bed found.");
                        for (BlockPosition lPos : findBlocks) {
                            if ((lPos.getBlock(lWorld).getData() & 0x8) != 0) {
                                lPlayer.goSleep(lPos);
                                break;
                            }
                        }
                    }
                    started = true;
                }
            });
        }
        return false;
    }

    @Override
    public void deactivate(Settler aSettler) {
        super.deactivate(aSettler);
        if (started) {
            if (aSettler.hasEntity()) {
                final NPCEntityPlayer lPlayer = aSettler.fEntity;
                runTaskLater(new Runnable() {
                    @Override
                    public void run() {
                        lPlayer.awake();
                    }
                });
            }
        }
    }
}
