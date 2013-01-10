/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author andre
 */
public class SettlerActivityTeleport extends SettlerActivity {

    public static final String TYPE = "Teleport";
    public BlockPosition target;

    public SettlerActivityTeleport() {
        type = TYPE;
    }

    public SettlerActivityTeleport(BlockPosition aPos) {
        type = TYPE;
        target = aPos.clone();
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        aMap.put("target", target.toCSV(","));
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        String lStr = aMap.get("target").toString();
        if (lStr != null && !lStr.isEmpty()) {
            target = new BlockPosition();
            target.fromCSV(lStr, "\\,");
        } else {
            target = null;
        }
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

    @Override
    public String toString() {
        return super.toString() + " to:" + target;
    }
}
