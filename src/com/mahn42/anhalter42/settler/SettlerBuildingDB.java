/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.framework.BuildingDB;
import com.mahn42.framework.IBeforeAfterExecute;
import java.io.File;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerBuildingDB extends BuildingDB<SettlerBuilding> {
    public SettlerBuildingDB() {
        super(SettlerBuilding.class);
    }

    public SettlerBuildingDB(World aWorld, File aFile) {
        super(SettlerBuilding.class, aWorld, aFile);
    }
}
