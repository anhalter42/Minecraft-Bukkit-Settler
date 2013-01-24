/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import org.bukkit.block.Block;

/**
 *
 * @author andre
 */
public class SettlerActivityBreakBlock extends SettlerActivityWithPosition {

    public static final String TYPE = "BreakBlock";
    
    protected boolean done = false;

    public SettlerActivityBreakBlock() {
        type = TYPE;
    }

    public SettlerActivityBreakBlock(BlockPosition aPos) {
        type = TYPE;
        target = aPos.clone();
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
            control.success = false;
            done = true;
        } else {
            control.success = false;
            done = true;
        }
        return done;
    }
}
