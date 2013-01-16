/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import java.util.Map;
import org.bukkit.block.Block;

/**
 *
 * @author andre
 */
public class SettlerActivityBreakBlock extends SettlerActivity {

    public static final String TYPE = "BreakBlock";
    public BlockPosition target;
    
    protected boolean done = false;

    public SettlerActivityBreakBlock() {
        type = TYPE;
    }

    public SettlerActivityBreakBlock(BlockPosition aPos) {
        type = TYPE;
        target = aPos.clone();
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        if (target != null) {
            aMap.put("target", target.toCSV(","));
        }
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        Object lObj = aMap.get("target");
        if (lObj != null) {
            target = new BlockPosition();
            target.fromCSV(lObj.toString(), "\\,");
        } else {
            target = null;
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        if (target != null) {
            final Settler lSettler = aSettler;
            runTaskLater(new Runnable() {
                @Override
                public void run() {
                    Block lBlock = target.getBlock(lSettler.getWorld());
                    boolean lbrooken;
                    if (lSettler.getItemInHand() != null) {
                        lbrooken = lBlock.breakNaturally(lSettler.getItemInHand());
                    } else {
                        lbrooken = lBlock.breakNaturally();
                    }
                    control.success = lbrooken;
                    done = true;
                }
            });
        } else {
            done = true;
        }
        return done;
    }
}
