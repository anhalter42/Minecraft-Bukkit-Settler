/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.IMarker;

/**
 *
 * @author andre
 */
public class MarkerStorageMarker implements IMarker{
    
    public Settler settler;

    public MarkerStorageMarker(Settler aSettler) {
        settler = aSettler;
    }

    @Override
    public String getName() {
        return settler.getSettlerName();
    }

    @Override
    public BlockPosition getPosition() {
        return settler.getPosition();
    }
}
