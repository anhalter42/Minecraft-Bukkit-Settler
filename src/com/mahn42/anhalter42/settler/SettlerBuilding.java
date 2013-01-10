/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.framework.Building;
import java.util.ArrayList;

/**
 *
 * @author andre
 */
public class SettlerBuilding extends Building {

    public int settlerCount = 1;
    public String basicProfession;

    @Override
    protected void toCSVInternal(ArrayList aCols) {
        super.toCSVInternal(aCols);
        aCols.add(basicProfession);
        aCols.add(settlerCount);
    }

    @Override
    protected void fromCSVInternal(DBRecordCSVArray aCols) {
        super.fromCSVInternal(aCols);
        basicProfession = aCols.pop();
        settlerCount = Integer.parseInt(aCols.pop());
    }

    @Override
    public String getIconName() {
        if (basicProfession != null && !basicProfession.isEmpty()) {
            return (super.getIconName() + "." + basicProfession).toLowerCase();
        } else {
            return super.getIconName();
        }
    }
}
