/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Framework;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

/**
 *
 * @author andre
 */
public class DynMapSettlerRenderer implements Runnable {

    protected boolean fInRun = false;
    protected Plugin fDynmap;

    @Override
    public void run() {
        if (!fInRun) {
            fInRun = true;
            try {
                PluginManager lPM = Framework.plugin.getServer().getPluginManager();
                fDynmap = lPM.getPlugin("dynmap");
                if(fDynmap != null && fDynmap.isEnabled()) {
                    execute();
                }
            } finally {
                fInRun = false;
            }
        }
    }
    public MarkerAPI getMarkerAPI() {
        if (fDynmap == null) {
            return null;
        }
        DynmapAPI lDynmapAPI = (DynmapAPI)fDynmap; /* Get API */
        return lDynmapAPI.getMarkerAPI();
    }
    
    private void execute() {
        MarkerAPI lMarkerAPI = getMarkerAPI();
        MarkerSet lMarkerSet = lMarkerAPI.getMarkerSet("settler.settler");
        if (lMarkerSet != null) {
            lMarkerSet.deleteMarkerSet();
        }
        lMarkerSet = lMarkerAPI.createMarkerSet("settler.settler", "Settler", null, false);
        if (lMarkerSet == null) {
            return;
        }
        lMarkerSet.setLabelShow(true); // for test
        List<World> lWorlds = SettlerPlugin.plugin.getServer().getWorlds();
        for(World lWorld : lWorlds) {
            ArrayList<Settler> lSettlers = SettlerPlugin.plugin.getSettlers(lWorld);
            //SettlerPlugin.plugin.getLogger().info("world " + lWorld.getName() + " has " + lSettlers.size() + " settlers.");
            for(Settler lSettler : lSettlers) {
                MarkerIcon lIcon = lMarkerAPI.getMarkerIcon(lSettler.getIconName());
                if (lIcon == null) {
                    lIcon = lMarkerAPI.getMarkerIcon("default");
                }
                Marker lIconMark;
                BlockPosition lPos = lSettler.getPosition();
                lIconMark = lMarkerSet.createMarker(lSettler.getKey(), lSettler.getDisplayName(), lWorld.getName(), lPos.x, lPos.y, lPos.z, lIcon, false);
                lIconMark.setDescription(lSettler.getDisplayName());
            }
        }
    }    
}
