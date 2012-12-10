/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author andre
 */
public class SettlerPlugin extends JavaPlugin {

    public static SettlerPlugin plugin;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
    
    @Override
    public void onEnable() { 
        plugin = this;
        readSettlerConfig();
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }
    
    private void readSettlerConfig() {
        FileConfiguration lConfig = getConfig();
    }
}
