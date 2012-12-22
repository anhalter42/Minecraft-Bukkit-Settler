/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.command.CommandSettlerListProfessions;
import com.mahn42.anhalter42.settler.command.CommandSettlerTest;
import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.framework.BuildingDescription;
import com.mahn42.framework.BuildingDetector;
import com.mahn42.framework.Framework;
import com.mahn42.framework.WorldDBList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

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
    protected SettlerSynchronTask settlerSyncTask;

    public static void main(String[] args) {
    }

    @Override
    public void onEnable() {
        plugin = this;
        Settler.register();
        readSettlerConfig();
        registerSettlerBuildings();
        registerSettlerCommands();
        settlerDB = new WorldDBList<SettlerDB>(SettlerDB.class, "Settler", this);
        settlerBuildingDB = new WorldDBList<SettlerBuildingDB>(SettlerBuildingDB.class, "SettlerBuilding", this);
        settlerSyncTask = new SettlerSynchronTask();
        Framework.plugin.registerSaver(settlerDB);
        Framework.plugin.registerSaver(settlerBuildingDB);
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new DynMapSettlerRenderer(), 40, 40);
        getServer().getScheduler().runTaskTimer(this, settlerSyncTask, 10, 20); // first a little bit slower
        List<World> lWorlds = getServer().getWorlds();
        for (World lWorld : lWorlds) {
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

    public SettlerSynchronTask getSettlerSynchronTask() {
        return settlerSyncTask;
    }

    private void registerSettlerBuildings() {
        SettlerBuildingHandler lHandler = new SettlerBuildingHandler();
        BuildingDetector lDetector = Framework.plugin.getBuildingDetector();
        BuildingDescription lDesc;
        BuildingDescription.BlockDescription lBDesc;
        BuildingDescription.RelatedTo lRel;
        lDesc = lDetector.newDescription("Settler.Geologist");
        lDesc.typeName = "Geologist";
        lDesc.handler = lHandler;
        lDesc.iconName = "Settler.Building.Geologist";
        lBDesc = lDesc.newBlockDescription("base");
        lBDesc.materials.add(Material.SMOOTH_BRICK, (byte) 3);
        lBDesc.detectSensible = true;
        lRel = lBDesc.newRelatedTo(new Vector(0, 1, 0), "antenabase");
        lRel = lBDesc.newRelatedTo("lever", BuildingDescription.RelatedPosition.Nearby, 1);
        lRel = lBDesc.newRelatedTo("sign", BuildingDescription.RelatedPosition.Nearby, 1);
        lBDesc = lDesc.newBlockDescription("lever");
        lBDesc.materials.add(Material.LEVER);
        lBDesc = lDesc.newBlockDescription("sign");
        lBDesc.materials.add(Material.SIGN);
        lBDesc.materials.add(Material.SIGN_POST);
        lBDesc.materials.add(Material.WALL_SIGN);
        lBDesc = lDesc.newBlockDescription("antenabase");
        lBDesc.materials.add(Material.FENCE);
        lRel = lBDesc.newRelatedTo(new Vector(0, 10, 0), "antenatop");
        lRel.materials.add(Material.FENCE);
        lRel.minDistance = 1;
        lBDesc = lDesc.newBlockDescription("antenatop");
        lBDesc.materials.add(Material.FENCE);
        lDesc.createAndActivateXZ(true);

    }

    private void registerSettlerCommands() {
        getCommand("s_list_professions").setExecutor(new CommandSettlerListProfessions());
        getCommand("s_test").setExecutor(new CommandSettlerTest());
    }
}
