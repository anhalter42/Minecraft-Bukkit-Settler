/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;

/**
 *
 * @author andre
 */
public class SettlerActivityNothing extends SettlerActivity {

    public static final String TYPE = "Nothing";

    public SettlerActivityNothing() {
        type = TYPE;
        maxTicks = 1;
    }

    public SettlerActivityNothing(int aMaxTicks) {
        type = TYPE;
        maxTicks = aMaxTicks;
    }

    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        return false;
    }
    
}
