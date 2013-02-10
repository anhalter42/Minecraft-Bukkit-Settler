/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.SettlerAccess.ChunkLoad;
import com.mahn42.anhalter42.settler.SettlerAccess.SettlerDamage;
import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Framework;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerTask implements Runnable {

    protected World fWorld;
    protected SettlerAccess fAccess;

    public SettlerTask(World aWorld) {
        fWorld = aWorld;
    }

    public World getWorld() {
        return fWorld;
    }
    protected ArrayList<ChunkLoad> fChunkLoads;
    protected List<? extends Settler> fSettlers;
    protected ArrayList<Settler> fDiedSettlers;
    protected ArrayList<Settler> fReachedTargetSettlers;
    protected ArrayList<SettlerDamage> fDamagedSettlers;
    protected boolean fIsRunning = false;
    public long timeOffset = 0;

    protected enum ChunkChangeKind {

        Loaded, Unloaded, None
    }

    @Override
    public void run() {
        if (!fIsRunning) {
            fIsRunning = true;
            //if (getWorld().getName().equalsIgnoreCase("world")) {
            //    Framework.plugin.log("settler", getClass().getSimpleName() + " started " + getWorld().getName() + ".");
            //}
            try {
                if (fAccess == null) {
                    fAccess = SettlerPlugin.plugin.getSettlerAccess(fWorld);
                }
                if (fAccess.isEnabled() && fAccess.shouldRun && !getWorld().getPlayers().isEmpty()) {
                    long lprofstart = Framework.plugin.getProfiler().beginProfile("settler.task");
                    //fAccess.shouldRun = false;
                    fDiedSettlers = fAccess.retrieveDiedSettlers();
                    for (Settler lSettler : fDiedSettlers) {
                        fAccess.removeSettler(lSettler);
                        fSettlers.remove(lSettler);
                        lSettler.died();
                    }
                    if (fSettlers == null || fSettlers.isEmpty()) {
                        timeOffset = 0;
                        fSettlers = fAccess.getSettlers();
                        fChunkLoads = fAccess.retrieveChunkLoads();
                        fReachedTargetSettlers = fAccess.retrieveReachedTargetSettlers();
                        fDamagedSettlers = fAccess.retrieveDamagedSettlers();
                    }
                    timeOffset += SettlerPlugin.plugin.configSettlerTicks;
                    fAccess.timeOffset = timeOffset;
                    //if (getWorld().getName().equalsIgnoreCase("world")) {
                    //    Framework.plugin.log("settler", getClass().getSimpleName() + " settler count " + fSettlers.size() + " " + getWorld().getName() + ".");
                    //}
                    int lCount = 0;
                    while (!fSettlers.isEmpty() && lCount < SettlerPlugin.plugin.configSettlersPerRun) {
                        lCount++;
                        Settler lSettler = fSettlers.get(0);
                        fSettlers.remove(0);
                        try {
                            /*
                             if (fDiedSettlers.contains(lSettler)) {
                             lSettler.died();
                             fDiedSettlers.remove(lSettler);
                             }
                             */
                            if (fReachedTargetSettlers != null && fReachedTargetSettlers.contains(lSettler)) {
                                lSettler.targetReached(fAccess);
                                fReachedTargetSettlers.remove(lSettler);
                            }
                            for (SettlerDamage lDamage : fDamagedSettlers) {
                                if (lDamage.settler == lSettler) {
                                    lSettler.addDamage(lDamage);
                                }
                            }
                            if (lSettler.isActive()) {
                                long lsetprofstart = Framework.plugin.getProfiler().beginProfile("settler." + lSettler.getProfession());
                                BlockPosition lPos = lSettler.getPosition();
                                ChunkChangeKind changeKind = getChunkLoadKind(lPos.x >> 4, lPos.z >> 4);
                                switch (changeKind) {
                                    case Loaded:
                                        if (!lSettler.hasEntity()) {
                                            fAccess.addSettlerForEntity(lSettler);
                                        }
                                    case Unloaded:
                                        if (lSettler.hasEntity()) {
                                            // entity should destroyd by minecraft
                                            lSettler.removeEntity();
                                        }
                                    case None:
                                        if (!lSettler.hasEntity()) {
                                            if (fWorld.isChunkLoaded(lPos.x >> 4, lPos.z >> 4)) {
                                                fAccess.addSettlerForEntity(lSettler);
                                            }
                                        }
                                }
                                //if (getWorld().getName().equalsIgnoreCase("world")) {
                                //    Framework.plugin.log("settler", getClass().getSimpleName() + " settler " + lSettler.getSettlerName() + " " + getWorld().getName() + ".");
                                //}
                                lSettler.run(this, fAccess);
                                Framework.plugin.getProfiler().endProfile("settler." + lSettler.getProfession(), lsetprofstart);
                            }
                        } catch (Exception ex) {
                            SettlerPlugin.plugin.getLogger().throwing(getClass().getSimpleName(), null, ex);
                        }
                    }
                    //fSettlers = null;
                    //fChunkLoads = null;
                    //fReachedTargetSettlers = null;
                    //fDamagedSettlers = null;
                    //fDiedSettlers = null;
                    Framework.plugin.getProfiler().endProfile("settler.task", lprofstart);
                }
            } finally {
                fIsRunning = false;
                //if (getWorld().getName().equalsIgnoreCase("world")) {
                //    Framework.plugin.log("settler", getClass().getSimpleName() + " ended " + getWorld().getName() + ".");
                //}
            }
        }
    }

    public ArrayList<SettlerDamage> getNearestDamagedSettlers(BlockPosition aPos, int aRadius) {
        ArrayList<SettlerDamage> lRes = new ArrayList<SettlerDamage>();
        for (SettlerDamage lDamage : fDamagedSettlers) {
            if (lDamage.entityPos != null && aPos.nearly(lDamage.entityPos, aRadius)) {
                lRes.add(lDamage);
            }
        }
        return lRes;
    }

    private ChunkChangeKind getChunkLoadKind(int x, int z) {
        for (ChunkLoad lLoad : fChunkLoads) {
            if (lLoad.x == x && lLoad.z == z) {
                return lLoad.kind == ChunkLoad.Kind.Unloaded ? ChunkChangeKind.Unloaded : ChunkChangeKind.Loaded;
            }
        }
        return ChunkChangeKind.None;
    }
}
