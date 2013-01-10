/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Framework;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    protected boolean fInit = false;

    @Override
    public void run() {
        if (!fInRun) {
            fInRun = true;
            try {
                PluginManager lPM = Framework.plugin.getServer().getPluginManager();
                fDynmap = lPM.getPlugin("dynmap");
                if (fDynmap != null && fDynmap.isEnabled()) {
                    if (!fInit) {
                        initialize();
                    }
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
        DynmapAPI lDynmapAPI = (DynmapAPI) fDynmap; /* Get API */
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
        for (World lWorld : lWorlds) {
            ArrayList<Settler> lSettlers = SettlerPlugin.plugin.getSettlers(lWorld);
            //SettlerPlugin.plugin.getLogger().info("world " + lWorld.getName() + " has " + lSettlers.size() + " settlers.");
            for (Settler lSettler : lSettlers) {
                if (lSettler.isActive()) {
                    String lIconName = lSettler.getIconName();
                    MarkerIcon lIcon = lMarkerAPI.getMarkerIcon(lIconName);
                    if (lIcon == null) {
                        SettlerPlugin.plugin.getLogger().info("icon '" + lIconName + "' not found.. using default!");
                        lIcon = lMarkerAPI.getMarkerIcon("default");
                    }
                    Marker lIconMark;
                    BlockPosition lPos = lSettler.getPosition();
                    lIconMark = lMarkerSet.createMarker(lSettler.getKey(), lSettler.getSettlerName(), lWorld.getName(), lPos.x, lPos.y, lPos.z, lIcon, false);
                    lIconMark.setDescription(lSettler.getDescription());
                }
            }
        }
    }

    private void initialize() {
        MarkerAPI markerAPI = getMarkerAPI();
        if (markerAPI != null) {
            File lFolder = SettlerPlugin.plugin.getDataFolder();
            lFolder = new File(lFolder.getPath() + File.separatorChar + "markers");
            if (!lFolder.exists()) {
                lFolder.mkdirs();
            } else {
                File[] lFiles = lFolder.listFiles();
                for (File lFile : lFiles) {
                    String lName = lFile.getName();
                    if (lName.endsWith(".png")) {
                        String lLabel = lName.replaceAll(".png", "");
                        String lId = "settler." + lLabel;
                        try {
                            FileInputStream lInput = new FileInputStream(lFile);
                            MarkerIcon markerIcon = markerAPI.getMarkerIcon(lId);
                            if (markerIcon != null) {
                                markerIcon.setMarkerIconImage(lInput);
                            } else {
                                markerAPI.createMarkerIcon(lId, lName, lInput);
                            }
                            try {
                                lInput.close();
                            } catch (IOException ex) {
                                Logger.getLogger(DynMapSettlerRenderer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(DynMapSettlerRenderer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }
}
