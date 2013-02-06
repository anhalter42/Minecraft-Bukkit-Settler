/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockRect;
import com.mahn42.framework.IMarker;
import com.mahn42.framework.IMarkerStorage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class MarkerStorage implements IMarkerStorage {
    @Override
    public List<IMarker> findMarkers(World aWorld, String aName) {
        ArrayList<IMarker> lResult = new ArrayList<IMarker>();
        SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(aWorld);
        Collection<Settler> settlers = lAccess.getSettlers(aName);
        for(Settler lSettler : settlers) {
            lResult.add(new MarkerStorageMarker(lSettler));
        }
        return lResult;
    }

    @Override
    public List<IMarker> findMarkers(World aWorld, BlockRect aArea) {
        ArrayList<IMarker> lResult = new ArrayList<IMarker>();
        return lResult;
    }    
}
