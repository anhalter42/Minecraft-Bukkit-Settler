/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.command.CommandSettlerListProfessions;
import com.mahn42.anhalter42.settler.command.CommandSettlerTest;
import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.Framework;
import com.mahn42.framework.WorldDBList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author andre
 */
public class SettlerPlugin extends JavaPlugin {

    public static SettlerPlugin plugin;
    
    protected WorldDBList<SettlerDB> settlerDB;
    protected WorldDBList<SettlerBuildingDB> settlerBuildingDB;
    protected HashMap<String, SettlerAccess> settlers = new HashMap<String, SettlerAccess>();
    protected HashMap<String, SettlerTask> worldTasks = new HashMap<String, SettlerTask>();
    
    
    public static void main(String[] args) {
    }
    
    @Override
    public void onEnable() { 
        plugin = this;
        Settler.register();
        readSettlerConfig();
        registerSettlerBuildings();
        registerSettlerCommands();
        settlerDB = new WorldDBList<SettlerDB>(SettlerDB.class, "Settler",this);
        settlerBuildingDB = new WorldDBList<SettlerBuildingDB>(SettlerBuildingDB.class, "SettlerBuilding",this);
        Framework.plugin.registerSaver(settlerDB);
        Framework.plugin.registerSaver(settlerBuildingDB);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new DynMapSettlerRenderer(), 40, 40);
        getServer().getScheduler().runTaskTimer(this, new SettlerSynchronTask(), 10, 20); // first a little bit slower
        List<World> lWorlds = getServer().getWorlds();
        for(World lWorld : lWorlds) {
            SettlerTask lTask = new SettlerTask(lWorld);
            worldTasks.put(lWorld.getName(), lTask);
            getServer().getScheduler().runTaskTimerAsynchronously(this, lTask, 20, 20); // first a little bit slower
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }
    
    private void readSettlerConfig() {
        FileConfiguration lConfig = getConfig();
    }

    public SettlerAccess getSettlerAccess(World aWorld) {
        SettlerAccess lAccess = settlers.get(aWorld.getName());
        if (lAccess == null) {
            lAccess = new SettlerAccess(aWorld);
            settlers.put(aWorld.getName(), lAccess);
        }
        return lAccess;
    }
    public ArrayList<Settler> getSettlers(World aWorld) {
        SettlerAccess lAccess = settlers.get(aWorld.getName());
        ArrayList<Settler> lResult = new ArrayList<Settler>();
        if (lAccess != null) {
            lResult.addAll(lAccess.getSettlers());
        }
        return lResult;
    }

    public SettlerDB getSettlerDB(World aWorld) {
        return settlerDB.getDB(aWorld);
    }
    
    private void registerSettlerBuildings() {
        
    }

    private void registerSettlerCommands() {
        getCommand("s_list_professions").setExecutor(new CommandSettlerListProfessions());
        getCommand("s_test").setExecutor(new CommandSettlerTest());
    }
}
