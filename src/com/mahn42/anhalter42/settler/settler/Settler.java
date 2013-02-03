/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerAccess.SettlerDamage;
import com.mahn42.anhalter42.settler.SettlerDBRecord;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.anhalter42.settler.SettlerTask;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.EntityControl;
import com.mahn42.framework.Framework;
import com.mahn42.framework.Framework.ItemType;
import com.mahn42.framework.InventoryHelper;
import com.mahn42.framework.WorldScanner;
import com.mahn42.framework.npc.entity.NPCEntityPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author andre
 */
public class Settler {

    protected static HashMap<String, Class> types = new HashMap<String, Class>();

    public static void register() {
        SettlerFisher.register();
        SettlerWoodcutter.register();
        SettlerForester.register();
        SettlerGeologist.register();
        SettlerShepherd.register();
        SettlerGuard.register();
        SettlerFarmer.register();
        SettlerRancher.register();
        SettlerWearer.register();
    }

    public static Class getSettlerClass(String aTypename) {
        return types.get(aTypename);
    }

    public static void register(String aTypename, Class aClass) {
        types.put(aTypename, aClass);
    }

    public static void register(SettlerProfession aProf) {
        //SettlerPlugin.plugin.getLogger().info("profession registration: " + aProf.name);
        register(aProf.name, aProf.settlerClass);
        SettlerPlugin.plugin.registerProfession(aProf);
    }

    public static Set<String> getSettlerProfessions() {
        return types.keySet();
    }

    public static class PutInChestItem {

        Material material;
        int keep;

        public PutInChestItem(Material aMaterial, int aKeep) {
            material = aMaterial;
            keep = aKeep;
        }
    }
    //Runtime
    protected int fEntityId = 0;
    protected World fWorld;
    protected SettlerProfession fProf;
    protected NPCEntityPlayer fEntity = null;
    protected ArrayList<Material> fItemsToCollect = new ArrayList<Material>();
    protected ArrayList<PutInChestItem> fPutInChestItems = new ArrayList<PutInChestItem>();
    protected ArrayList<SettlerDamage> fDamages = new ArrayList<SettlerDamage>();
    //Meta
    protected String fKey;
    protected String fProfession;
    protected BlockPosition fPosition;
    protected BlockPosition fBedPosition;
    protected String fPlayerName;
    protected String fClanName;
    protected String fHomeKey;
    protected String fSettlerName;
    protected boolean fActive = false;
    //Meta in Blob 
    protected ItemStack fBoots;
    protected ItemStack fLeggings;
    protected ItemStack fChestplate;
    protected ItemStack fHelmet;
    protected ItemStack fItemInHand;
    protected ItemStack[] fInventory = new ItemStack[36];
    protected int fFoodLevel = 20;
    protected int fHealth = 20;
    protected float fSaturation = 20.0f;
    protected String fFather;
    protected String fMother;
    protected SettlerActivityList fActivityList = new SettlerActivityList(this);
    protected long fWorkStart = 0;   // 08:00
    protected long fWorkEnd = 12500; // 20:30
    protected boolean fSendAtHome = false;
    protected float fWalkSpeed = 0.8f;
    protected Rotation fFrameConfig = Rotation.NONE;
    protected long fLivingTicks = 0;
    protected BlockPosition fWorkPosition = null;
    // profession specific?
    protected int fCollectItemRadius = 16;
    protected boolean fResetOnNight = true;

    public Settler(String aProfession) {
        fKey = UUID.randomUUID().toString();
        fProfession = aProfession;
        fProf = SettlerPlugin.plugin.getProfession(fProfession);
        fItemsToCollect.add(Material.COOKIE);
        fPutInChestItems.add(new PutInChestItem(Material.COOKIE, 0));
    }

    public String getKey() {
        return fKey;
    }

    public String getHomeKey() {
        return fHomeKey;
    }

    public void setHomeKey(String aHomeKey) {
        fHomeKey = aHomeKey;
    }

    public World getWorld() {
        return fWorld;
    }

    public void setWorld(World aWorld) {
        fWorld = aWorld;
    }

    public String getProfession() {
        return fProfession;
    }

    public BlockPosition getPosition() {
        return fPosition == null ? null : fPosition.clone();
    }

    public void setPosition(BlockPosition aPos) {
        if (aPos != null) {
            fPosition = aPos.clone();
        }
    }

    public Rotation getFrameConfig() {
        return fFrameConfig;
    }

    public void setFrameConfig(Rotation aConfig) {
        fFrameConfig = aConfig;
    }

    public BlockPosition getBedPosition() {
        return fBedPosition == null ? null : fBedPosition.clone();
    }

    public void setBedPosition(BlockPosition aPos) {
        if (aPos != null) {
            fBedPosition = aPos.clone();
        } else {
            fBedPosition = null;
        }
    }

    public String getPlayerName() {
        return fPlayerName;
    }

    public void setPlayerName(String aPlayerName) {
        fPlayerName = aPlayerName;
    }

    public String getClanName() {
        return fClanName;
    }

    public void setClanName(String aClanName) {
        fClanName = aClanName;
    }

    public String getSettlerName() {
        return fSettlerName;
    }

    public void setSettlerName(String aSettlerName) {
        fSettlerName = aSettlerName;
    }

    public int getFoodLevel() {
        return fFoodLevel;
    }

    public void setFoodLevel(int aValue) {
        fFoodLevel = aValue;
    }

    public int getHealth() {
        return fHealth;
    }

    public void setHealth(int aValue) {
        fHealth = aValue;
    }

    public float getSaturation() {
        return fSaturation;
    }

    public void setSaturation(float aValue) {
        fSaturation = aValue;
    }

    public ItemStack[] getInventory() {
        return fInventory;
    }

    public void serialize(SettlerDBRecord aRecord) {
        YamlConfiguration lYaml = new YamlConfiguration();
        serialize(lYaml);
        String lKey = getKey();
        if (lKey != null && !lKey.isEmpty()) {
            aRecord.key = lKey;
        } else {
            fKey = aRecord.key;
        }
        aRecord.blob = lYaml.saveToString();
        //SettlerPlugin.plugin.getLogger().info("ser:" + aRecord.blob);
        aRecord.profession = getProfession();
        aRecord.playerName = getPlayerName();
        aRecord.clanName = getClanName();
        aRecord.settlerName = getSettlerName();
        aRecord.homeKey = getHomeKey();
        aRecord.position = getPosition();
        aRecord.bedPosition = getBedPosition();
        aRecord.active = fActive;
    }

    public void deserialize(SettlerDBRecord aRecord) {
        fKey = aRecord.key;
        fProfession = aRecord.profession;
        fProf = SettlerPlugin.plugin.getProfession(fProfession);
        fPlayerName = aRecord.playerName;
        fClanName = aRecord.clanName;
        fSettlerName = aRecord.settlerName;
        fPosition = aRecord.position;
        fBedPosition = aRecord.bedPosition;
        fHomeKey = aRecord.homeKey;
        fActive = aRecord.active;
        YamlConfiguration lYaml = new YamlConfiguration();
        try {
            //SettlerPlugin.plugin.getLogger().info("des:" + aRecord.blob);
            lYaml.loadFromString(aRecord.blob);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(Settler.class.getName()).log(Level.SEVERE, null, ex);
        }
        deserialize(lYaml);
    }

    protected void serializeItemStack(YamlConfiguration aValues, String aName, ItemStack aStack) {
        if (aStack != null) {
            aValues.set(aName, aStack.serialize());
        } else {
            aValues.set(aName, null);
        }
    }

    protected void serialize(YamlConfiguration aValues) {
        serializeItemStack(aValues, "boots", fBoots);
        serializeItemStack(aValues, "leggings", fLeggings);
        serializeItemStack(aValues, "chestplate", fChestplate);
        serializeItemStack(aValues, "helmet", fHelmet);
        ArrayList<Map> lItems = new ArrayList<Map>();
        for (ItemStack lItem : fInventory) {
            if (lItem != null) {
                Map<String, Object> lMap = lItem.serialize();
                lItems.add(lMap);
            } else {
                lItems.add(null);
            }
        }
        aValues.set("inventory", lItems);
        serializeItemStack(aValues, "iteminhand", fItemInHand);
        aValues.set("foodlevel", getFoodLevel());
        aValues.set("health", getHealth());
        aValues.set("saturation", getSaturation());
        aValues.set("father", fFather == null ? "" : fFather);
        aValues.set("mother", fMother == null ? "" : fMother);
        fActivityList.serialize(aValues, "activityList");
        aValues.set("workStart", fWorkStart);
        aValues.set("workEnd", fWorkEnd);
        aValues.set("sendAtHome", fSendAtHome);
        aValues.set("walkSpeed", fWalkSpeed);
        aValues.set("frameConfig", getFrameConfig().toString());
        aValues.set("livingTicks", fLivingTicks);
        if (fWorkPosition != null) {
            aValues.set("workPosition", fWorkPosition.toCSV(","));
        }
    }

    protected ItemStack deserializeItemStack(YamlConfiguration aValues, String aName) {
        Object lObj;
        lObj = aValues.get(aName);
        if (lObj instanceof Map) {
            return ItemStack.deserialize((Map<String, Object>) lObj);
        } else if (lObj instanceof MemorySection) {
            return ItemStack.deserialize(((MemorySection) lObj).getValues(false));
        } else {
            return null;
        }
    }

    protected void deserialize(YamlConfiguration aValues) {
        fBoots = deserializeItemStack(aValues, "boots");
        fLeggings = deserializeItemStack(aValues, "leggings");
        fChestplate = deserializeItemStack(aValues, "chestplate");
        fHelmet = deserializeItemStack(aValues, "helmet");
        fItemInHand = deserializeItemStack(aValues, "iteminhand");
        Object lObj = aValues.get("inventory");
        if (lObj instanceof ArrayList) {
            int lIndex = 0;
            for (Object lItem : (ArrayList) lObj) {
                if (lItem == null || lItem.toString().equals("null")) {
                } else {
                    if (lItem instanceof Map) {
                        ItemStack lIStack = ItemStack.deserialize((Map) lItem);
                        fInventory[lIndex] = lIStack;
                    } else if (lItem instanceof MemorySection) {
                        ItemStack lIStack = ItemStack.deserialize(((MemorySection) lItem).getValues(false));
                        fInventory[lIndex] = lIStack;
                    } else {
                        SettlerPlugin.plugin.getLogger().info("inventory deserialize ?? '" + lItem.toString() + "' ???");
                    }
                }
                lIndex++;
            }
        }
        fFoodLevel = aValues.getInt("foodlevel", fFoodLevel);
        fHealth = aValues.getInt("health", fHealth);
        fSaturation = (float) aValues.getDouble("saturation", (double) fSaturation);
        fFather = aValues.getString("father");
        fMother = aValues.getString("mother");
        fActivityList.deserialize(aValues, "activityList");
        fWorkStart = aValues.getLong("workStart", fWorkStart);
        fWorkEnd = aValues.getLong("workEnd", fWorkEnd);
        fSendAtHome = aValues.getBoolean("sendAtHome", false);
        fWalkSpeed = (float) aValues.getDouble("walkSpeed", (double) fWalkSpeed);
        fFrameConfig = Rotation.valueOf(aValues.getString("frameConfig", Rotation.NONE.toString()));
        fLivingTicks = aValues.getLong("livingTicks", fLivingTicks);
        lObj = aValues.get("workPosition");
        if (lObj != null) {
            fWorkPosition = new BlockPosition();
            fWorkPosition.fromCSV(lObj.toString(), "\\,");
        } else {
            fWorkPosition = null;
        }
    }

    public boolean isWorkingTime() {
        long lTime = getWorld().getTime();
        if (fWorkStart < fWorkEnd) {
            return fWorkStart <= lTime && lTime <= fWorkEnd;
        } else {
            return lTime >= fWorkStart || lTime <= fWorkEnd;
        }
    }

    public void addDamage(SettlerDamage lDamage) {
        fDamages.add(lDamage);
    }

    public void removeEntity() {
        if (hasEntity()) {
            final Player lPlayer = fEntity.getAsPlayer();
            SettlerPlugin.plugin.getServer().getScheduler().runTask(SettlerPlugin.plugin, new Runnable() {
                @Override
                public void run() {
                    lPlayer.remove();
                }
            });
            Framework.plugin.log("settler", "settler " + getSettlerName() + " entity " + fEntityId + " removed.");
        }
        setEntityId(0);
    }

    public void run(SettlerTask aTask, SettlerAccess aAccess) {
        if (!hasEntity()) { // for testing?.. only settler working who have an entity
            return;
        }
        fLivingTicks += SettlerPlugin.plugin.configSettlerTicks;
        runCheckHealth(aTask, aAccess);
        if (!isWorkingTime()) { // no work time :-) we go sleeping
            if (!fSendAtHome && !existsTaggedActivity("Night")) {
                if (getBedPosition() != null && !getPosition().nearly(getBedPosition(), 2)) {
                    fSendAtHome = true;
                    BlockPosition lBed = getBedPosition().clone();
                    lBed.y++;
                    if (fResetOnNight) {
                        SettlerActivity lAct = getCurrentActivity();
                        if (lAct != null) {
                            lAct.deactivate(this);
                        }
                        fActivityList.clear();
                        addActivityForNow(
                                "Night",
                                new SettlerActivityWalkToTarget(getBedPosition()),
                                new SettlerActivityTeleport(lBed),
                                new SettlerActivitySleep(24000),
                                new SettlerActivityAwake());
                    } else {
                        addActivityForNow(
                                "Night",
                                new SettlerActivityWalkToTarget(getBedPosition()),
                                new SettlerActivityTeleport(lBed),
                                new SettlerActivitySleep(24000),
                                new SettlerActivityAwake(),
                                new SettlerActivityWalkToTarget(getPosition()));
                    }
                }
            }
        } else { // lets work..
            fSendAtHome = false;
            SettlerActivity lAct = getCurrentActivity();
            if (lAct != null) {
                if (lAct instanceof SettlerActivitySleep && "Night".equals(lAct.control.tag)) {//(lAct.type.equals(SettlerActivitySleep.TYPE)) {
                    lAct.deactivate(this);
                    fActivityList.remove(lAct);
                }
            }
            runCheckArmor(aTask, aAccess);
            runPutInChestItems(aTask, aAccess);
            runCollectItems(aTask, aAccess);
        }
        runCheckDamage(aTask, aAccess);
        runInternal(aTask, aAccess);
        SettlerActivity lAct = getCurrentActivity();
        if (lAct != null) {
            boolean lRemove = true;
            if ((lAct.control.condition == SettlerActivity.RunCondition.IfPreviousSuccess && lAct.control.previous_success)
                    || (lAct.control.condition == SettlerActivity.RunCondition.IfPreviousFaild && !lAct.control.previous_success)
                    || (lAct.control.condition == SettlerActivity.RunCondition.Always)) {
                lRemove = lAct.run(aAccess, this);
                lAct.runningTicks += SettlerPlugin.plugin.configSettlerTicks;
                if (lRemove || lAct.runningTicks > lAct.maxTicks) {
                    lAct.deactivate(this);
                    fActivityList.remove(lAct);
                    SettlerActivity lNext = getCurrentActivity();
                    if (lNext != null) {
                        lNext.control.previous_success = lAct.control.success;
                    }
                    Framework.plugin.log("settler_x", "settler " + getSettlerName() + " activity poped " + lAct);
                }
            } else {
                lAct.deactivate(this);
                fActivityList.remove(lAct);
                SettlerActivity lNext = getCurrentActivity();
                if (lNext != null) {
                    lNext.control.previous_success = lAct.control.success;
                }
                Framework.plugin.log("settler_x", "settler " + getSettlerName() + " activity skipped " + lAct);
            }
        }
        fDamages.clear();
    }

    protected void runInternal(SettlerTask aTask, SettlerAccess aAccess) {
    }
    protected long fHealthTicks = 0;

    protected void runCheckHealth(SettlerTask aTask, SettlerAccess aAccess) {
        if (hasEntity() && getHealth() < 20 && getFoodLevel() > 15) {
            fHealthTicks += SettlerPlugin.plugin.configSettlerTicks;
            if (fHealthTicks > 30) {
                fHealthTicks = 0;
                setHealth(getHealth() + 1);
                final Player lPlayer = fEntity.getAsPlayer();
                SettlerPlugin.plugin.getServer().getScheduler().runTask(SettlerPlugin.plugin, new Runnable() {
                    @Override
                    public void run() {
                        lPlayer.setHealth(lPlayer.getHealth() + 1);
                    }
                });
            }
        }
    }

    public ItemStack getArmor(ItemType aType) {
        switch (aType) {
            case Boots:
                return getBoots();
            case Leggings:
                return getLeggings();
            case Chestplate:
                return getChestplate();
            case Helmet:
                return getHelmet();
            case Tool:
                return getItemInHand();
            default:
                return null;
        }
    }

    protected void runCheckArmor(SettlerTask aTask, SettlerAccess aAccess) {
        BlockPosition lPos = getPosition();
        //TODO should be chest position
        if (getBedPosition() != null && lPos.nearly(getBedPosition(), 4)) {
            boolean lEmpty = false;
            for (SettlerProfession.Item lItem : fProf.armor) {
                if (getArmor(Framework.plugin.getItemType(lItem.item.getType())) == null) {
                    lEmpty = true;
                    break;
                }
            }
            if (lEmpty) {
                List<BlockPosition> findChests = findBlocks(Material.CHEST, 4);
                for (SettlerProfession.Item lItem : fProf.armor) {
                    if (getArmor(Framework.plugin.getItemType(lItem.item.getType())) == null) {
                        for (BlockPosition lChestPos : findChests) {
                            Chest lChest = (Chest) lChestPos.getBlock(getWorld()).getState();
                            Inventory lInv = lChest.getInventory();
                            if (InventoryHelper.hasAtleastItems(lInv, lItem.item.getType(), 1)) {
                                List<ItemStack> lRemoved = InventoryHelper.removeItemsByMaterial(lInv, lItem.item.getType(), 1);
                                if (!lRemoved.isEmpty()) {
                                    setArmor(lRemoved.get(0));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void runCheckDamage(SettlerTask aTask, SettlerAccess aAccess) {
        if (fDamages.size() > 0 && !existsTaggedActivity("Fight")) {
            for (SettlerDamage lDamage : fDamages) {
                if (lDamage.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                        && lDamage.entityPos != null) {
                    if (!lDamage.entityPos.nearly(getPosition(), 2)) {
                        addActivityForNow(
                                "Fight",
                                new SettlerActivityWalkToTarget(lDamage.entityPos, SettlerActivityWalkToTarget.WalkAction.Fight, lDamage.entityId),
                                new SettlerActivityFight(lDamage.entityId, 20));
                    } else {
                        addActivityForNow("Fight", new SettlerActivityFight(lDamage.entityId, 20));
                    }
                    fSendAtHome = false;
                }
            }
        }
    }

    public void runPutInChestItems(SettlerTask aTask, SettlerAccess aAccess) {
        if (!existsTaggedActivity("PutInChestItems")) {
            BlockPosition lPos = getPosition();
            //TODO should be chest position
            if (getBedPosition() != null && lPos.nearly(getBedPosition(), 4)) {
                for (PutInChestItem lpItem : fPutInChestItems) {
                    if (hasAtleastItems(lpItem.material, lpItem.keep + 1)) {
                        for (ItemStack lItem : getInventory()) {
                            if (lItem != null && lItem.getType().equals(lpItem.material)) {
                                addActivityForNow(
                                        "PutInChestItems",
                                        new SettlerActivityPutItemsInChest(lpItem.material, lItem.getData().getData(), -1, lpItem.keep));
                            }
                        }
                    }
                }
            }
        }
    }

    public void runCollectItems(SettlerTask aTask, SettlerAccess aAccess) {
        if (!existsTaggedActivity("CollectItems")) {
            Collection<SettlerAccess.EntityState> lStates = aAccess.getEntityStatesNearby(getPosition(), fCollectItemRadius, fItemsToCollect);
            if (!lStates.isEmpty()) {
                for (SettlerAccess.EntityState lState : lStates) {
                    if (InventoryHelper.canInsertItems(getInventory(), lState.item) > 0) {
                        if (lState.pos.nearly(getPosition(), 2)) {
                            addActivityForNow("CollectItems", new SettlerActivityCollectItems(fItemsToCollect));
                        } else if (canWalkTo(lState.pos)) {
                            if (!existsTaggedActivity("CollectItems.back")) {
                                addActivityForNow(
                                        "CollectItems.back",
                                        new SettlerActivityWalkToTarget(getPosition()));
                            }
                            addActivityForNow(
                                    "CollectItems",
                                    new SettlerActivityWalkToTarget(lState.pos),
                                    new SettlerActivityCollectItems(fItemsToCollect));
                        }
                    }
                }
            }
        }

    }

    public String getIconName() {
        return ("settler." + getProfession()).toLowerCase();
    }

    public String getDisplayName() {
        String lRes = getSettlerName();
        if (lRes == null || lRes.isEmpty()) {
            lRes = SettlerPlugin.plugin.getText(getProfession());
        } else {
            lRes = SettlerPlugin.plugin.getText(getProfession()) + " " + lRes;
        }
        if (Framework.plugin.isDebugSet("settler")) {
            return hasEntity() ? "" + fEntityId : lRes;
        }
        return lRes;
    }

    public boolean hasEntity() {
        return fEntityId != 0;
    }

    public int getEntityId() {
        return fEntityId;
    }

    public void updateEntity(NPCEntityPlayer aEntity) {
        Player lPlayer = aEntity.getAsPlayer();
        updateToEntity(aEntity);
        BlockPosition lPos = getPosition();
        if (lPos != null) {
            lPlayer.teleport(lPos.getLocation(fWorld));
        }
    }

    public void updateToEntity(NPCEntityPlayer aEntity) {
        Player lPlayer = aEntity.getAsPlayer();
        lPlayer.setDisplayName(getSettlerName());
        BlockPosition lPos;
        lPos = getBedPosition();
        if (lPos != null) {
            lPlayer.setBedSpawnLocation(lPos.getLocation(fWorld));
        }
        PlayerInventory lInv = lPlayer.getInventory();
        lInv.clear();
        lInv.setContents(fInventory);
        ItemStack[] lArmor = new ItemStack[4];
        lArmor[0] = fBoots;
        lArmor[1] = fLeggings;
        lArmor[2] = fChestplate;
        lArmor[3] = fHelmet;
        lInv.setArmorContents(lArmor);
        /*
        fEntity.setEquipment(0, fBoots);
        fEntity.setEquipment(1, fLeggings);
        fEntity.setEquipment(2, fChestplate);
        fEntity.setEquipment(3, fHelmet);
         */
        //fEntity.setEquipment(4, fItemInHand);
        //int lheldItemSlot = lInv.getHeldItemSlot();
        lInv.setItemInHand(fItemInHand);

        /*
        lInv.setBoots(fBoots);
        lInv.setLeggings(fLeggings);
        lInv.setChestplate(fChestplate);
        lInv.setHelmet(fHelmet);
        lInv.setItemInHand(fItemInHand);
        //lPlayer.updateInventory();
        //lPlayer.setItemInHand(fItemInHand);
        /*
         ItemStack[] lItems = getInventory();
         for(int i = 0; i<lItems.length; i++) {
         lInv.setItem(i, lItems[i]);
         }
         */
        lPlayer.setFoodLevel(getFoodLevel());
        lPlayer.setHealth(getHealth());
        lPlayer.setSaturation(getSaturation());
        lPlayer.setCanPickupItems(true);
        lPlayer.setWalkSpeed(getWalkSpeed() * SettlerPlugin.plugin.configSettlerSpeed);
        aEntity.setDataObject(this);
    }

    public void setEntity(NPCEntityPlayer aEntity) {
        Player lPlayer = aEntity.getAsPlayer();
        fEntity = aEntity;
        setEntityId(lPlayer.getEntityId());
    }

    public void updateFromEntity(NPCEntityPlayer aEntity) {
        setEntity(aEntity);
        Player lPlayer = aEntity.getAsPlayer();
        setPosition(new BlockPosition(lPlayer.getLocation()));
        setFoodLevel(lPlayer.getFoodLevel());
        setHealth(lPlayer.getHealth());
        setSaturation(lPlayer.getSaturation());
    }

    public void updateInventoryFromEntity(NPCEntityPlayer aEntity) {
        Player lPlayer = aEntity.getAsPlayer();
        PlayerInventory lInv = lPlayer.getInventory();
        ItemStack[] lItems = lInv.getContents();
        for (int i = 0; i < fInventory.length; i++) {
            fInventory[i] = lItems[i];
        }
    }

    public void setEntityId(int aEntityId) {
        if (fEntityId != aEntityId) {
            Framework.plugin.log("settler", "settler " + getSettlerName() + " has now entity id " + aEntityId + ".");
            fEntityId = aEntityId;
            if (fEntityId == 0) {
                fEntity = null;
            }
        }
    }

    public void createEntity() {
        NPCEntityPlayer lNPC = Framework.plugin.createPlayerNPC(fWorld, getPosition(), getSettlerName(), this);
        Framework.plugin.log("settler", "settler entity " + lNPC.getAsPlayer().getEntityId() + "created for '" + getSettlerName() + "' at " + getPosition());
        setEntity(lNPC);
        updateEntity(lNPC);
    }

    public void checkForBecomeEntity() {
        if (!hasEntity()) {
            BlockPosition lPos = getPosition();
            if (fWorld.isChunkLoaded(lPos.x >> 4, lPos.z >> 4)) {
                createEntity();
            }
        }
    }

    public void activate() {
        fActive = true;
        Framework.plugin.log("settler", "settler " + getSettlerName() + " activated.");
    }

    public void deactivate() {
        fActive = false;
        Framework.plugin.log("settler", "settler " + getSettlerName() + " deactivated.");
    }

    public boolean isActive() {
        return fActive;
    }

    public void setWorkPosition(BlockPosition aPosition) {
        if (aPosition != null) {
            fWorkPosition = aPosition.clone();
        } else {
            fWorkPosition = null;
        }
    }

    public BlockPosition getWorkPosition() {
        return fWorkPosition != null ? fWorkPosition.clone() : (getBedPosition() != null ? getBedPosition() : null);
    }

    public ItemStack getLeggings() {
        return fLeggings;
    }

    public void setLeggings(ItemStack aItemStack) {
        fLeggings = aItemStack;
    }

    public ItemStack getBoots() {
        return fBoots;
    }

    public void setBoots(ItemStack aItemStack) {
        fBoots = aItemStack;
    }

    public ItemStack getChestplate() {
        return fChestplate;
    }

    public void setChestplate(ItemStack aItemStack) {
        fChestplate = aItemStack;
    }

    public ItemStack getHelmet() {
        return fHelmet;
    }

    public void setHelmet(ItemStack aItemStack) {
        fHelmet = aItemStack;
    }

    public ItemStack getItemInHand() {
        return fItemInHand;
    }

    public void setItemInHand(ItemStack aItemStack) {
        fItemInHand = aItemStack;
        getInventory()[0] = fItemInHand;
    }

    public SettlerProfession getProf() {
        return fProf;
    }

    public long getWorkStart() {
        return fWorkStart;
    }

    public void setWorkStart(long aValue) {
        fWorkStart = aValue;
    }

    public long getWorkEnd() {
        return fWorkEnd;
    }

    public void setWorkEnd(long aValue) {
        fWorkEnd = aValue;
    }

    public float getWalkSpeed() {
        return fWalkSpeed;
    }

    public void setWalkSpeed(float aValue) {
        fWalkSpeed = aValue;
    }

    public void setArmor(ItemStack aItem) {
        Framework.ItemType lType = Framework.plugin.getItemType(aItem.getType());
        switch (lType) {
            case Boots:
                setBoots(aItem);
                break;
            case Chestplate:
                setChestplate(aItem);
                break;
            case Helmet:
                setHelmet(aItem);
                break;
            case Leggings:
                setLeggings(aItem);
                break;
            case Tool:
                setItemInHand(aItem);
                break;
            default:
                setItemInHand(aItem);
                break;
        }
    }

    @Override
    public boolean equals(Object aObject) {
        if (aObject instanceof Settler) {
            return getKey().equals(((Settler) aObject).getKey());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.fKey != null ? this.fKey.hashCode() : 0);
        return hash;
    }

    public void died() {
        if (hasEntity()) {
            removeEntity();
        }
        //dropAllArmor();
        //dropInventory();
        setBoots(null);
        setLeggings(null);
        setChestplate(null);
        setHelmet(null);
        ItemStack[] lInv = getInventory();
        for (int i = 0; i < lInv.length; i++) {
            lInv[i] = null;
        }
    }

    public void dropAllArmor() {
        Location lLoc = getPosition().getLocation(getWorld());
        ItemStack lItem;
        lItem = getBoots();
        if (lItem != null) {
            setBoots(null);
            getWorld().dropItem(lLoc, lItem);
        }
        lItem = getLeggings();
        if (lItem != null) {
            setLeggings(null);
            getWorld().dropItem(lLoc, lItem);
        }
        lItem = getChestplate();
        if (lItem != null) {
            setChestplate(null);
            getWorld().dropItem(lLoc, lItem);
        }
        lItem = getHelmet();
        if (lItem != null) {
            setHelmet(null);
            getWorld().dropItem(lLoc, lItem);
        }
    }

    public void dropInventory() {
        Location lLoc = getPosition().getLocation(getWorld());
        ItemStack[] lInv = getInventory();
        for (ItemStack lItem : lInv) {
            getWorld().dropItem(lLoc, lItem);
        }
        for (int i = 0; i < lInv.length; i++) {
            lInv[i] = null;
        }
    }

    public void targetReached(SettlerAccess aAccess) {
        SettlerActivity lAct = getCurrentActivity();
        if (lAct != null) {
            lAct.targetReached(aAccess, this);
        }
    }

    public String getDescription() {
        String lRes = getDisplayName();
        SettlerActivity lAct = getCurrentActivity();
        if (lAct != null) {
            lRes += "\n" + lAct.toString() + " +" + (fActivityList.size() - 1);
        }
        return lRes;
    }

    public void dump() {
        Logger l = SettlerPlugin.plugin.getLogger();
        Player lPlayer = null;
        if (hasEntity()) {
            lPlayer = fEntity.getAsPlayer();
        }
        l.info("Key:" + fKey + " livingTicks:" + fLivingTicks);
        l.info("HomeKey:" + fHomeKey);
        l.info("SettlerName:" + fSettlerName);
        l.info("Profession:" + fProfession);
        l.info("PlayerName:" + fPlayerName);
        l.info("Position:" + fPosition + " BedPosition:" + fBedPosition);
        l.info("WorkPosition:" + fWorkPosition);
        l.info("Activity:" + getCurrentActivity());
        l.info("Workingtime:" + fWorkStart + " - " + fWorkEnd);
        l.info("Health:" + fHealth + " Food:" + fFoodLevel);
        l.info("frameConfig:" + fFrameConfig + " " + getFrameConfigName());
        l.info("Boots:" + fBoots + " (" + (lPlayer != null ? lPlayer.getInventory().getBoots() : "-") + ")");
        l.info("Leggings:" + fLeggings + " (" + (lPlayer != null ? lPlayer.getInventory().getLeggings() : "-") + ")");
        l.info("Chestplate:" + fChestplate + " (" + (lPlayer != null ? lPlayer.getInventory().getChestplate() : "-") + ")");
        l.info("Helmet:" + fHelmet + " (" + (lPlayer != null ? lPlayer.getInventory().getHelmet() : "-") + ")");
        l.info("ItemInHand:" + fItemInHand + " (" + (lPlayer != null ? lPlayer.getInventory().getItemInHand() : "-") + ")");
        int lIndex = -1;
        for (ItemStack i : fInventory) {
            lIndex++;
            if (i != null) {
                l.info("Inv("+lIndex+") " + i + " " + i.getData());
            }
        }
        fActivityList.dump(l);
    }

    public SettlerActivity getCurrentActivity() {
        return fActivityList.peek();
    }

    public SettlerActivity[] tagActivities(String aTag, SettlerActivity... aActivities) {
        for (SettlerActivity lAct : aActivities) {
            lAct.control.tag = aTag;
        }
        return aActivities;
    }

    public SettlerActivity[] controlActivities(SettlerActivity.Control aControl, SettlerActivity... aActivities) {
        for (SettlerActivity lAct : aActivities) {
            lAct.control = aControl;
        }
        return aActivities;
    }

    public void addActivityForNow(String aTag, SettlerActivity... aActivities) {
        addActivityForNow(tagActivities(aTag, aActivities));
    }

    public void addActivityForNow(SettlerActivity... aActivities) {
        for (int i = aActivities.length - 1; i >= 0; i--) {
            SettlerActivity lAct = aActivities[i];
            fActivityList.push(lAct);
            Framework.plugin.log("settler_x", "settler " + getSettlerName() + " activity pushed now " + lAct);
        }
    }

    public void addActivityForLater(String aTag, SettlerActivity... aActivities) {
        addActivityForLater(tagActivities(aTag, aActivities));
    }

    public void addActivityForLater(SettlerActivity... aActivities) {
        for (int i = aActivities.length - 1; i >= 0; i--) {
            SettlerActivity lAct = aActivities[i];
            fActivityList.add(lAct);
            Framework.plugin.log("settler_x", "settler " + getSettlerName() + " activity pushed later " + lAct);
        }
    }

    public void addActivityForNext(String aTag, SettlerActivity... aActivities) {
        addActivityForNext(tagActivities(aTag, aActivities));
    }

    public void addActivityForNext(SettlerActivity... aActivities) {
        for (int i = aActivities.length - 1; i >= 0; i--) {
            SettlerActivity lAct = aActivities[i];
            fActivityList.addAsNext(lAct);
            Framework.plugin.log("settler_x", "settler " + getSettlerName() + " activity pushed next " + lAct);
        }
    }

    public boolean existsActivity(Class aClass) {
        return fActivityList.exists(aClass);
    }

    public boolean existsTaggedActivity(String aTag) {
        return fActivityList.existsTag(aTag);
    }

    public boolean canWalkTo(BlockPosition aDest) {
        if (hasEntity()) {
            return EntityControl.existsPath(fEntity.getAsPlayer(), aDest);
        } else {
            return false; //TODO
        }
    }

    public boolean hasAtleastItems(Material aMat, int aCount) {
        boolean lFound = false;
        for (ItemStack lItem : getInventory()) {
            if (lItem != null && lItem.getType().equals(aMat)) {
                aCount -= lItem.getAmount();
                if (aCount <= 0) {
                    lFound = true;
                    break;
                }
            }
        }
        return lFound;
    }

    public int removeItems(Material aMat, int aCount) {
        int aO = aCount;
        int i = -1;
        for (ItemStack lItem : getInventory()) {
            i++;
            if (lItem != null && lItem.getType().equals(aMat)) {
                if (lItem.getAmount() > aCount) {
                    lItem.setAmount(lItem.getAmount() - aCount);
                    aCount = 0;
                    break;
                } else {
                    aCount -= lItem.getAmount();
                    getInventory()[i] = null;
                    if (aCount == 0) {
                        break;
                    }
                }
            }
        }
        return aO - aCount;
    }

    public void removed() {
        if (hasEntity()) {
            removeEntity();
        }
    }

    public int removeItems(ItemStack aItem) {
        return InventoryHelper.removeItems(getInventory(), aItem, 0); // exclude item in hand at pos 0
    }

    public int insertItems(ItemStack aItem) {
        return InventoryHelper.insertItems(getInventory(), aItem, 0); // exclude item in hand at pos 0
    }

    public enum PositionCondition {

        None,
        NaturalBlocksAround,
        GrassOrDirtAround,
        Tree,
        FarmingAround,}
    public static ArrayList<Material> grassOrDirt = new ArrayList<Material>();

    {
        grassOrDirt.add(Material.GRASS);
        grassOrDirt.add(Material.DIRT);
        grassOrDirt.add(Material.LONG_GRASS);
        grassOrDirt.add(Material.YELLOW_FLOWER);
        grassOrDirt.add(Material.RED_ROSE);
        grassOrDirt.add(Material.DEAD_BUSH);
    }
    public static ArrayList<Material> naturalBlocks = new ArrayList<Material>();

    {
        naturalBlocks.add(Material.GRASS);
        naturalBlocks.add(Material.DIRT);
        naturalBlocks.add(Material.LONG_GRASS);
        naturalBlocks.add(Material.DEAD_BUSH);
        naturalBlocks.add(Material.STONE);
        naturalBlocks.add(Material.YELLOW_FLOWER);
        naturalBlocks.add(Material.RED_ROSE);
        naturalBlocks.add(Material.SAND);
        naturalBlocks.add(Material.SANDSTONE);
        naturalBlocks.add(Material.SNOW);
        naturalBlocks.add(Material.LEAVES);
        naturalBlocks.add(Material.LOG);
    }
    public static ArrayList<Material> farmingBlocks = new ArrayList<Material>();

    {
        farmingBlocks.add(Material.WHEAT);
        farmingBlocks.add(Material.CROPS);
        farmingBlocks.add(Material.CARROT);
        farmingBlocks.add(Material.SPECKLED_MELON);
        farmingBlocks.add(Material.PUMPKIN);
        farmingBlocks.add(Material.MELON);
        farmingBlocks.add(Material.MELON_BLOCK);
        farmingBlocks.add(Material.POTATO);
        farmingBlocks.add(Material.SOIL);
        farmingBlocks.add(Material.COCOA);
    }

    public BlockPosition findRandomWalkToPosition(Random aRandom, int aRadius, int aAttempts, PositionCondition aCondition) {
        return findRandomWalkToPosition(getPosition(), aRandom, aRadius, aAttempts, aCondition);
    }

    public BlockPosition findRandomWalkToPosition(BlockPosition aStart, Random aRandom, int aRadius, int aAttempts, PositionCondition aCondition) {
        boolean lFound = false;
        do {
            if (aStart == null) {
                aStart = getPosition();
            }
            BlockPosition lPos = aStart.clone();
            lPos.add(aRandom.nextInt(aRadius * 2) - aRadius, 0, aRandom.nextInt(aRadius * 2) - aRadius);
            lPos.y = getWorld().getHighestBlockYAt(lPos.x, lPos.z);
            Block lBlock = lPos.getBlock(getWorld());
            if (!lBlock.isLiquid()) {
                while (lPos.y > 0 && (!lBlock.getType().isSolid() || lBlock.getType().equals(Material.LEAVES))) {
                    lPos.y--;
                    lBlock = lPos.getBlock(getWorld());
                }
                lPos.y++; // step in AIR
                lBlock = lPos.getBlock(getWorld());
                if (!lBlock.isLiquid()) {
                    lBlock = lPos.getBlockAt(getWorld(), 0, 1, 0);
                    if (!lBlock.getType().isSolid() || lBlock.getType().isTransparent()) {
                        lFound = false;
                        switch (aCondition) {
                            case None:
                                lFound = true;
                                break;
                            case GrassOrDirtAround:
                                lFound = true;
                                for (int x = -2; x <= 2; x++) {
                                    for (int z = -2; z <= 2; z++) {
                                        BlockPosition lP = lPos.clone();
                                        lP.add(x, 0, z);
                                        lP.y = getWorld().getHighestBlockYAt(lPos.x, lPos.z);
                                        Material lMat = lP.getBlockType(getWorld());
                                        while (lMat.equals(Material.AIR)) {
                                            lP.y--;
                                            lMat = lP.getBlockType(getWorld());
                                        }
                                        if (!grassOrDirt.contains(lMat)) {
                                            lFound = false;
                                            break;
                                        }
                                    }
                                    if (!lFound) {
                                        break;
                                    }
                                }
                                break;
                            case NaturalBlocksAround:
                                lFound = true;
                                for (int x = -2; x <= 2; x++) {
                                    for (int z = -2; z <= 2; z++) {
                                        BlockPosition lP = lPos.clone();
                                        lP.add(x, 0, z);
                                        lP.y = getWorld().getHighestBlockYAt(lPos.x, lPos.z);
                                        Material lMat = lP.getBlockType(getWorld());
                                        while (lMat.equals(Material.AIR)) {
                                            lP.y--;
                                            lMat = lP.getBlockType(getWorld());
                                        }
                                        if (!naturalBlocks.contains(lMat)) {
                                            lFound = false;
                                            break;
                                        }
                                    }
                                    if (!lFound) {
                                        break;
                                    }
                                }
                                break;
                            case FarmingAround:
                                lFound = false;
                                for (int x = -2; x <= 2; x++) {
                                    for (int z = -2; z <= 2; z++) {
                                        BlockPosition lP = lPos.clone();
                                        lP.add(x, 0, z);
                                        lP.y = getWorld().getHighestBlockYAt(lPos.x, lPos.z);
                                        Material lMat = lP.getBlockType(getWorld());
                                        while (lMat.equals(Material.AIR)) {
                                            lP.y--;
                                            lMat = lP.getBlockType(getWorld());
                                        }
                                        if (farmingBlocks.contains(lMat)) {
                                            lFound = true;
                                            break;
                                        }
                                    }
                                    if (lFound) {
                                        break;
                                    }
                                }
                                break;
                            case Tree:
                                List<BlockPosition> lPoss = WorldScanner.findBlocks(getWorld(), lPos, Material.LOG, 3);
                                if (lPoss.size() > 0) {
                                    lPoss = WorldScanner.findBlocks(getWorld(), lPos, Material.LEAVES, 3);
                                    if (lPoss.size() > 0) {
                                        lFound = true;
                                    }
                                }
                                break;
                        }
                        if (lFound) {
                            lFound = canWalkTo(lPos);
                            if (lFound) {
                                return lPos;
                            }
                        }
                    }
                }
            }
            aAttempts--;
        } while (!lFound && aAttempts > 0);
        return null;
    }

    public BlockPosition findRandomTeleportToPosition(Random aRandom, int aRadius, int aAttempts) {
        do {
            BlockPosition lPos = getPosition();
            lPos.add(aRandom.nextInt(aRadius * 2) - aRadius, 0, aRandom.nextInt(aRadius * 2) - aRadius);
            lPos.y = getWorld().getHighestBlockYAt(lPos.x, lPos.z);
            Block lBlock = lPos.getBlock(getWorld());
            if (!lBlock.isLiquid()) {
                while (lPos.y > 0 && (!lBlock.getType().isSolid() || lBlock.getType().equals(Material.LEAVES))) {
                    lPos.y--;
                    lBlock = lPos.getBlock(getWorld());
                }
                lPos.y++; // step in AIR
                lBlock = lPos.getBlock(getWorld());
                if (!lBlock.isLiquid()) {
                    lBlock = lPos.getBlockAt(getWorld(), 0, 1, 0);
                    if (!lBlock.getType().isSolid() || lBlock.getType().isTransparent()) {
                        return lPos;
                    }
                }
            }
            aAttempts--;
        } while (aAttempts > 0);
        return null;
    }

    public List<BlockPosition> findBlocks(Material aMaterial, int aRadius) {
        return WorldScanner.findBlocks(getWorld(), getPosition(), aMaterial, aRadius, true);
    }

    public List<BlockPosition> findBlocks(Material aMaterial, byte aData, int aRadius) {
        return WorldScanner.findBlocks(getWorld(), getPosition(), aMaterial, aData, true, aRadius, true);
    }

    public List<BlockPosition> findBlocks(Material aMaterial, byte aData, byte aMask, int aRadius) {
        return WorldScanner.findBlocks(getWorld(), getPosition(), aMaterial, aData, aMask, true, aRadius, true);
    }

    public ItemStack getFirstItem(Material aMaterial) {
        for (int i = 0; i < getInventory().length; i++) {
            ItemStack lItem = getInventory()[i];
            if (lItem != null && lItem.getType().equals(aMaterial)) {
                return lItem;
            }
        }
        return null;
    }

    public boolean fight(int aEntityId) {
        boolean ldead = false;
        if (hasEntity()) {
            fEntity.swingArm();
            boolean lfound = false;
            List<LivingEntity> lEntities = getWorld().getLivingEntities();
            for (LivingEntity lEntity : lEntities) {
                if (lEntity.getEntityId() == aEntityId) {
                    fEntity.attack(lEntity);

                    int lDamage = 1;
                    ItemStack lItem = fEntity.getAsPlayer().getItemInHand();
                    if (lItem != null) {
                        lDamage = Framework.plugin.getItemWeaponLevel(lItem.getType());
                        int lEnchantmentLevel = lItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                        if (lEnchantmentLevel > 0) {
                            lDamage = lDamage + lEnchantmentLevel; //TODO
                        }
                    }
                    lEntity.damage(lDamage, fEntity.getAsPlayer());

                    lfound = true;
                    break;
                }
            }
            if (!lfound) {
                ldead = true;
            }
        }
        return ldead;
    }
    protected int fTargetId;

    public void setTarget(Player aPlayer) {
        fTargetId = 0;
        removeTaggedActivity("Target");
        if (aPlayer != null) {
            fTargetId = aPlayer.getEntityId();
            addActivityForNow("Target", new SettlerActivityFollowTarget(aPlayer.getEntityId()));
        }
    }

    public int getTargetId() {
        return fTargetId;
    }

    public void removeTaggedActivity(String aTag) {
        fActivityList.removeTagged(aTag);
    }

    public String getFrameConfigName() {
        return "";
    }
}
