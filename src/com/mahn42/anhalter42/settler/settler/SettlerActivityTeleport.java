/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author andre
 */
public class SettlerActivityTeleport extends SettlerActivityWithPosition {

    public static final String TYPE = "Teleport";

    public SettlerActivityTeleport() {
        type = TYPE;
    }

    public SettlerActivityTeleport(BlockPosition aPos) {
        type = TYPE;
        target = aPos.clone();
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (target != null && aSettler.hasEntity()) {
            final Player lPlayer = aSettler.fEntity.getAsPlayer();
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    lPlayer.teleport(target.getLocation(lPlayer.getWorld()).add(0.5, 0.5, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            });
        }
        return true;
    }
}
