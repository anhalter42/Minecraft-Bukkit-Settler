/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.SettlerAccess.ChunkLoad;
import com.mahn42.anhalter42.settler.SettlerAccess.SettlerDamage;
import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import java.util.ArrayList;
import java.util.Collection;
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
    protected Collection<? extends Settler> fSettlers;
    protected ArrayList<Settler> fDiedSettlers;
    protected ArrayList<Settler> fReachedTargetSettlers;
    protected ArrayList<SettlerDamage> fDamagedSettlers;
    protected boolean fIsRunning = false;

    protected enum ChunkChangeKind {
        Loaded, Unloaded, None
    }

    @Override
    public void run() {
        if (!fIsRunning) {
            fIsRunning = true;
            try {
                if (fAccess == null) {
                    fAccess = SettlerPlugin.plugin.getSettlerAccess(fWorld);
                }
                fSettlers = fAccess.getSettlers();
                fChunkLoads = fAccess.retrieveChunkLoads();
                fDiedSettlers = fAccess.retrieveDiedSettlers();
                fReachedTargetSettlers = fAccess.retrieveReachedTargetSettlers();
                fDamagedSettlers = fAccess.retrieveDamagedSettlers();
                for (Settler lSettler : fSettlers) {
                    if (fDiedSettlers.contains(lSettler)) {
                        lSettler.died();
                        fDiedSettlers.remove(lSettler);
                    }
                    if (fReachedTargetSettlers.contains(lSettler)) {
                        lSettler.targetReached(fAccess);
                        fReachedTargetSettlers.remove(lSettler);
                    }
                    for(SettlerDamage lDamage : fDamagedSettlers) {
                        if (lDamage.settler == lSettler) {
                            lSettler.addDamage(lDamage);
                        }
                    }
                    if (lSettler.isActive()) {
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
                                    lSettler.setEntityId(0);
                                }
                            case None:
                                if (!lSettler.hasEntity()) {
                                    if (fWorld.isChunkLoaded(lPos.x >> 4, lPos.z >> 4)) {
                                        fAccess.addSettlerForEntity(lSettler);
                                    }
                                }
                        }
                        lSettler.run(fAccess);
                    }
                }
                fSettlers = null;
                fChunkLoads = null;
            } finally {
                fIsRunning = false;
            }
        }
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
