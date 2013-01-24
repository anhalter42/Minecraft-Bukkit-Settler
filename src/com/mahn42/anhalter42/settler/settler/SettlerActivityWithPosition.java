/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.framework.BlockPosition;
import java.util.Map;

/**
 *
 * @author andre
 */
public abstract class SettlerActivityWithPosition extends SettlerActivity {
    
    public BlockPosition target;
    
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
    public String toString() {
        return super.toString() + " " + target;
    }
}
