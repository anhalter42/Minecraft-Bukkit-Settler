/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.command.CommandSettlerList;
import com.mahn42.anhalter42.settler.command.CommandSettlerListProfessions;
import com.mahn42.anhalter42.settler.command.CommandSettlerTest;
import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.anhalter42.settler.settler.SettlerActivity;
import com.mahn42.anhalter42.settler.settler.SettlerActivityList;
import com.mahn42.anhalter42.settler.settler.SettlerActivityWalkToTarget;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.BuildingDescription;
import com.mahn42.framework.BuildingDetector;
import com.mahn42.framework.Framework;
import com.mahn42.framework.WorldDBList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class SettlerPlugin extends JavaPlugin {

    public int configSettlerTicks = 10;  // first a little bit slower
    
    public float configSettlerSpeed = 0.88f;
    public int configDefaultPathRadius = 15;
    public int configMiddlePathRadius = 10;
    public int configWidePathRadius = 23;
    
    public int configDefaultPathAttempts = 10;
    public int configDefaultCollectItemRadius = 10;
    public int configWideCollectItemRadius = 23;
    public int configDefaultFindEntityRadius = 42;
    
    public int configSettlersPerRun = 2;
    
    public static SettlerPlugin plugin;
    protected WorldDBList<SettlerDB> settlerDB;
    protected WorldDBList<SettlerBuildingDB> settlerBuildingDB;
    protected HashMap<String, SettlerAccess> settlers = new HashMap<String, SettlerAccess>();
    protected HashMap<String, SettlerTask> worldTasks = new HashMap<String, SettlerTask>();
    protected SettlerSynchronTask settlerSyncTask;
    protected ArrayList<String> names = new ArrayList<String>();
    protected ArrayList<SettlerProfession> professions = new ArrayList<SettlerProfession>();
    
    public static void main(String[] args) {
        SettlerActivity.register();
        SettlerActivityList lList = new SettlerActivityList(null);
        lList.add(new SettlerActivityWalkToTarget(new BlockPosition(1, 2, 3)));
        YamlConfiguration lConf = new YamlConfiguration();
        lList.serialize(lConf, "activityList");
        String lSave = lConf.saveToString();
        Logger.getLogger("xxx").info(lSave);
        lList.deserialize(lConf, "activityList");
        lList.dump(Logger.getLogger("xxx"));
    }

    @Override
    public void onEnable() {
        plugin = this;
        Settler.register();
        SettlerActivity.register();
        loadNames();
        readSettlerConfig();
        registerCraftingRecipes();
        registerSettlerBuildings();
        registerSettlerCommands();
        settlerDB = new WorldDBList<SettlerDB>(SettlerDB.class, "Settler", this);
        settlerBuildingDB = new WorldDBList<SettlerBuildingDB>(SettlerBuildingDB.class, "SettlerBuilding", this);
        settlerSyncTask = new SettlerSynchronTask();
        Framework.plugin.registerSaver(settlerDB);
        Framework.plugin.registerSaver(settlerBuildingDB);
        Framework.plugin.registerMarkerStorage(new MarkerStorage());
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new DynMapSettlerRenderer(), 40, 400);
        getServer().getScheduler().runTaskTimer(this, settlerSyncTask, 10, configSettlerTicks);
        List<World> lWorlds = getServer().getWorlds();
        for (World lWorld : lWorlds) {
            SettlerTask lTask = new SettlerTask(lWorld);
            worldTasks.put(lWorld.getName(), lTask);
            //getServer().getScheduler().runTaskTimer(this, lTask, 10 + (configSettlerTicks / 2), configSettlerTicks);
            getServer().getScheduler().runTaskTimerAsynchronously(this, lTask, 10 + (configSettlerTicks / 2), configSettlerTicks);
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

    public SettlerBuildingDB getSettlerBuildingDB(World aWorld) {
        return settlerBuildingDB.getDB(aWorld);
    }

    public SettlerSynchronTask getSettlerSynchronTask() {
        return settlerSyncTask;
    }
    
    public SettlerProfession getProfessionFromFrame(BuildingDescription aDesc, Material aMat) {
        for(SettlerProfession lProf : professions) {
            if (aMat.equals(lProf.frameMaterial)) {
                return lProf;
            }
        }
        return null;
    }
    
    public void registerProfession(SettlerProfession aProfession) {
        professions.add(aProfession);
    }

    private void registerSettlerBuildings() {
        SettlerBuildingHandler lHandler = new SettlerBuildingHandler();
        registerSettlerBuildingLodge1(lHandler);
        registerSettlerBuildingStorage(lHandler);
    }
    
    private void registerSettlerBuildingLodge1(SettlerBuildingHandler aHandler) {
        BuildingDetector lDetector = Framework.plugin.getBuildingDetector();
        BuildingDescription lDesc;
        BuildingDescription.BlockDescription lBDesc;
        BuildingDescription.RelatedTo lRel;
        lDesc = lDetector.newDescription("Settler.Lodge.1");
        BuildingDescription.BlockMaterialArray lWallMats = lDesc.newBlockMaterialArray();
        lWallMats.add(Material.WOOD);
        lWallMats.add(Material.LOG);
        lWallMats.add(Material.WOODEN_DOOR);
        BuildingDescription.BlockMaterialArray lTopMats = lDesc.newBlockMaterialArray();
        lTopMats.add(Material.WOOD_STAIRS);
        lTopMats.add(Material.WOOD_STEP);
        lTopMats.add(Material.SPRUCE_WOOD_STAIRS);
        lTopMats.add(Material.BIRCH_WOOD_STAIRS);
        lTopMats.add(Material.JUNGLE_WOOD_STAIRS);
        lDesc.typeName = "Lodge for one settler";
        lDesc.handler = aHandler;
        lDesc.iconName = "Settler.Building.Lodge.1";
        lBDesc = lDesc.newBlockDescription("corner1_bottom");
        lBDesc.materials.add(lWallMats);
        lBDesc.detectSensible = true;
        //lRel = lBDesc.newRelatedTo(new Vector(0, 2, 0), "corner1_top");
        lRel = lBDesc.newRelatedTo(new Vector(0, 4, 0), "corner1_top");
        lRel.materials.add(lWallMats);
        //lRel = lBDesc.newRelatedTo(new Vector(3, 0, 0), "corner2_bottom");
        lRel = lBDesc.newRelatedTo(new Vector(4, 0, 0), "corner2_bottom");
        lRel.materials.add(lWallMats);
        //lRel = lBDesc.newRelatedTo(new Vector(0, 0, 3), "corner4_bottom");
        lRel = lBDesc.newRelatedTo(new Vector(0, 0, 4), "corner4_bottom");
        lRel.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(1, 0, 1), "bed");
        lRel = lBDesc.newRelatedTo(new Vector(1, 1, -1), "frame");
        lBDesc = lDesc.newBlockDescription("corner2_bottom");
        lBDesc.materials.add(lWallMats);
        //lRel = lBDesc.newRelatedTo(new Vector(0, 2, 0), "corner2_top");
        lRel = lBDesc.newRelatedTo(new Vector(0, 4, 0), "corner2_top");
        lRel.materials.add(lWallMats);
        //lRel = lBDesc.newRelatedTo(new Vector(0, 0, 3), "corner3_bottom");
        lRel = lBDesc.newRelatedTo(new Vector(0, 0, 4), "corner3_bottom");
        lRel.materials.add(lWallMats);
        lBDesc = lDesc.newBlockDescription("corner3_bottom");
        lBDesc.materials.add(lWallMats);
        //lRel = lBDesc.newRelatedTo(new Vector(0, 2, 0), "corner3_top");
        lRel = lBDesc.newRelatedTo(new Vector(0, 4, 0), "corner3_top");
        lRel.materials.add(lWallMats);
        //lRel = lBDesc.newRelatedTo(new Vector(-3, 0, 0), "corner4_bottom");
        lRel = lBDesc.newRelatedTo(new Vector(-4, 0, 0), "corner4_bottom");
        lRel.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(-1, 0, -1), "chest");
        lBDesc = lDesc.newBlockDescription("corner4_bottom");
        lBDesc.materials.add(lWallMats);
        //lRel = lBDesc.newRelatedTo(new Vector(0, 2, 0), "corner4_top");
        lRel = lBDesc.newRelatedTo(new Vector(0, 4, 0), "corner4_top");
        lRel.materials.add(lWallMats);
        lBDesc = lDesc.newBlockDescription("corner1_top");
        lBDesc.materials.add(lTopMats);
        lBDesc = lDesc.newBlockDescription("corner2_top");
        lBDesc.materials.add(lTopMats);
        lBDesc = lDesc.newBlockDescription("corner3_top");
        lBDesc.materials.add(lTopMats);
        lBDesc = lDesc.newBlockDescription("corner4_top");
        lBDesc.materials.add(lTopMats);
        lBDesc = lDesc.newBlockDescription("bed");
        lBDesc.materials.add(Material.BED_BLOCK);
        lBDesc = lDesc.newBlockDescription("chest");
        lBDesc.materials.add(Material.CHEST);
        lBDesc = lDesc.newBlockDescription("frame");
        lBDesc.materials.add(Material.ITEM_FRAME);
        lDesc.createAndActivateXZ(true);
    }

    private void registerSettlerBuildingStorage(SettlerBuildingHandler aHandler) {
        BuildingDetector lDetector = Framework.plugin.getBuildingDetector();
        BuildingDescription lDesc;
        BuildingDescription.BlockDescription lBDesc;
        BuildingDescription.RelatedTo lRel;
        lDesc = lDetector.newDescription("Settler.StorageShelf.1");
        lDesc.typeName = "Shelf for storage";
        lDesc.handler = aHandler;
        lDesc.iconName = "Settler.Building.StorageShelf.1";
        lBDesc = lDesc.newBlockDescription("shelf1.1");
        lBDesc.materials.add(Material.WOOD_STAIRS);
        lBDesc.materials.add(Material.WOOD_STEP);
        lBDesc.detectSensible = true;
        lRel = lBDesc.newRelatedTo(new Vector(1, 0, 0), "shelf1.2");
        lRel = lBDesc.newRelatedTo(new Vector(0, 1, 0), "chest2.1");
        lRel = lBDesc.newRelatedTo(new Vector(0,-1, 0), "chest1.1");
        lBDesc = lDesc.newBlockDescription("shelf1.2");
        lBDesc.materials.add(Material.WOOD_STAIRS);
        lBDesc.materials.add(Material.WOOD_STEP);
        lBDesc.detectSensible = true;
        lRel = lBDesc.newRelatedTo(new Vector(0, 1, 0), "chest2.2");
        lRel = lBDesc.newRelatedTo(new Vector(0,-1, 0), "chest1.2");
        lBDesc = lDesc.newBlockDescription("chest1.1");
        lBDesc.materials.add(Material.CHEST);
        lBDesc = lDesc.newBlockDescription("chest1.2");
        lBDesc.materials.add(Material.CHEST);
        lBDesc = lDesc.newBlockDescription("chest2.1");
        lBDesc.materials.add(Material.CHEST);
        lBDesc = lDesc.newBlockDescription("chest2.2");
        lBDesc.materials.add(Material.CHEST);
        lDesc.createAndActivateXZ();
    }
    
    private void registerSettlerBuildingLodge2(SettlerBuildingHandler aHandler) {
        BuildingDetector lDetector = Framework.plugin.getBuildingDetector();
        BuildingDescription lDesc;
        BuildingDescription.BlockDescription lBDesc;
        BuildingDescription.RelatedTo lRel;
        lDesc = lDetector.newDescription("Settler.Lodge.2");
        BuildingDescription.BlockMaterialArray lWallMats = lDesc.newBlockMaterialArray();
        lWallMats.add(Material.WOOD);
        lWallMats.add(Material.LOG);
        lWallMats.add(Material.WOODEN_DOOR);
        BuildingDescription.BlockMaterialArray lTopMats = lDesc.newBlockMaterialArray();
        lTopMats.add(Material.WOOD_STAIRS);
        lTopMats.add(Material.WOOD_STEP);
        lTopMats.add(Material.SPRUCE_WOOD_STAIRS);
        lTopMats.add(Material.BIRCH_WOOD_STAIRS);
        lTopMats.add(Material.JUNGLE_WOOD_STAIRS);
        lDesc.typeName = "Lodge for two men";
        lDesc.handler = aHandler;
        lDesc.iconName = "Settler.Building.Lodge.2";
        lBDesc = lDesc.newBlockDescription("corner1_bottom");
        lBDesc.materials.add(lWallMats);
        lBDesc.detectSensible = true;
        lRel = lBDesc.newRelatedTo(new Vector(0, 2, 0), "corner1_top");
        lRel.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(3, 0, 0), "corner2_bottom");
        lRel.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(0, 0, 3), "corner4_bottom");
        lRel.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(1, 0, 1), "bed");
        lRel = lBDesc.newRelatedTo(new Vector(1, 1, -1), "frame");
        lBDesc = lDesc.newBlockDescription("corner2_bottom");
        lBDesc.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(0, 2, 0), "corner2_top");
        lRel.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(0, 0, 3), "corner3_bottom");
        lRel.materials.add(lWallMats);
        lBDesc = lDesc.newBlockDescription("corner3_bottom");
        lBDesc.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(0, 2, 0), "corner3_top");
        lRel.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(-3, 0, 0), "corner4_bottom");
        lRel.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(-1, 0, -1), "chest");
        lBDesc = lDesc.newBlockDescription("corner4_bottom");
        lBDesc.materials.add(lWallMats);
        lRel = lBDesc.newRelatedTo(new Vector(0, 2, 0), "corner4_top");
        lRel.materials.add(lWallMats);
        lBDesc = lDesc.newBlockDescription("corner1_top");
        lBDesc.materials.add(lTopMats);
        lBDesc = lDesc.newBlockDescription("corner2_top");
        lBDesc.materials.add(lTopMats);
        lBDesc = lDesc.newBlockDescription("corner3_top");
        lBDesc.materials.add(lTopMats);
        lBDesc = lDesc.newBlockDescription("corner4_top");
        lBDesc.materials.add(lTopMats);
        lBDesc = lDesc.newBlockDescription("bed");
        lBDesc.materials.add(Material.BED_BLOCK);
        lBDesc = lDesc.newBlockDescription("chest");
        lBDesc.materials.add(Material.CHEST);
        lBDesc = lDesc.newBlockDescription("frame");
        lBDesc.materials.add(Material.ITEM_FRAME);
        lDesc.createAndActivateXZ(true);
    }

    private void registerSettlerCommands() {
        getCommand("s_list_professions").setExecutor(new CommandSettlerListProfessions());
        getCommand("s_list").setExecutor(new CommandSettlerList());
        getCommand("s_test").setExecutor(new CommandSettlerTest());
    }

    public SettlerProfession getProfession(String aName) {
        for(SettlerProfession lProf : professions) {
            if (lProf.name.equals(aName)) {
                return lProf;
            }
        }
        return null;
    }

    private void loadNames() {
        File lFolder = getDataFolder();
        File lNameFile = new File(lFolder.getPath() + File.separatorChar + "names.txt");
        if (!lFolder.exists()) {
            lFolder.mkdirs();
        }
        if (lNameFile.exists()) {
            try {
                FileReader lReader = new FileReader(lNameFile);
                char[] lBuffer = new char[(int)lNameFile.length()];
                try {
                    lReader.read(lBuffer);
                    String lContent = new String(lBuffer);
                    String[] lLines = lContent.split("\n");
                    names.addAll(Arrays.asList(lLines));
                } catch (IOException ex) {
                    Logger.getLogger(SettlerPlugin.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SettlerPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (names.isEmpty()) {
            names.add("Michael");
            names.add("Andre");
            names.add("Heiko");
            names.add("Nils");
        }
    }
    
    public String getRandomSettlerName() {
        Random lRnd = new Random();
        return names.get(lRnd.nextInt(names.size()));
    }

    public String getText(String aText, Object... aObjects) {
        return getText((String)null, aText, aObjects);
    }
    
    public String getText(CommandSender aPlayer, String aText, Object... aObjects) {
        return getText(Framework.plugin.getPlayerLanguage(aPlayer.getName()), aText, aObjects);
    }
    
    public String getText(String aLanguage, String aText, Object... aObjects) {
        return Framework.plugin.getText(this, aLanguage, aText, aObjects);
    }

    private void registerCraftingRecipes() {
        ItemStack lItemStack = new ItemStack(Material.CHAINMAIL_HELMET);
        ShapedRecipe lShapeRecipe = new ShapedRecipe(lItemStack);
        lShapeRecipe.shape("AAA",
                           "B B",
                           "   ");
        lShapeRecipe.setIngredient('A', Material.IRON_INGOT);
        lShapeRecipe.setIngredient('B', Material.STRING);
        getServer().addRecipe(lShapeRecipe);

        lItemStack = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        lShapeRecipe = new ShapedRecipe(lItemStack);
        lShapeRecipe.shape("B B",
                           "AAA",
                           "AAA");
        lShapeRecipe.setIngredient('A', Material.IRON_INGOT);
        lShapeRecipe.setIngredient('B', Material.STRING);
        getServer().addRecipe(lShapeRecipe);

        lItemStack = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        lShapeRecipe = new ShapedRecipe(lItemStack);
        lShapeRecipe.shape("AAA",
                           "A A",
                           "B B");
        lShapeRecipe.setIngredient('A', Material.IRON_INGOT);
        lShapeRecipe.setIngredient('B', Material.STRING);
        getServer().addRecipe(lShapeRecipe);

        lItemStack = new ItemStack(Material.CHAINMAIL_BOOTS);
        lShapeRecipe = new ShapedRecipe(lItemStack);
        lShapeRecipe.shape("   ",
                           "A A",
                           "B B");
        lShapeRecipe.setIngredient('A', Material.IRON_INGOT);
        lShapeRecipe.setIngredient('B', Material.STRING);
        getServer().addRecipe(lShapeRecipe);
    }
}
