/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.framework.DBSetWorld;
import java.io.File;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class SettlerDB extends DBSetWorld<SettlerDBRecord> {
    public SettlerDB(World aWorld, File aFile) {
        super(SettlerDBRecord.class, aFile, aWorld);
    }
}
