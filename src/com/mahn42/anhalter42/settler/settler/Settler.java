/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerDBRecord;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Framework;
import com.mahn42.framework.npc.entity.NPCEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author andre
 */
public class Settler {

    protected static HashMap<String, Class> types = new HashMap<String, Class>();

    public static void register() {
        register(SettlerFisher.typeName, SettlerFisher.class);
        register(SettlerWoodcutter.typeName, SettlerWoodcutter.class);
        register(SettlerForester.typeName, SettlerForester.class);
        register(SettlerGeologist.typeName, SettlerGeologist.class);
    }

    public static Class getSettlerClass(String aTypename) {
        return types.get(aTypename);
    }

    public static void register(String aTypename, Class aClass) {
        types.put(aTypename, aClass);
    }

    public static Set<String> getSettlerProfessions() {
        return types.keySet();
    }
    //Runtime
    protected int fEntityId = 0;
    protected World fWorld;
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

    public Settler(String aProfession) {
        fProfession = aProfession;
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
    }

    public void run(SettlerAccess aAccess) {
        /*
         checkForBecomeEntity(aAccess);
         if (hasEntity()) {
         EntityState lState = aAccess.getEntityState(getEntityId());
         }
         */
    }

    public String getIconName() {
        return ("settler." + getProfession()).toLowerCase();
    }

    public String getDisplayName() {
        String lRes = getSettlerName();
        if (lRes == null || lRes.isEmpty()) {
            lRes = getProfession();
        } else {
            lRes = getProfession() + " " + lRes;
        }
        return lRes;
    }

    public boolean hasEntity() {
        return fEntityId != 0;
    }

    public int getEntityId() {
        return fEntityId;
    }

    public void updateEntity(NPCEntity aEntity) {
        Player lPlayer = aEntity.getAsPlayer();
        lPlayer.setDisplayName(getDisplayName());
        BlockPosition lPos;
        lPos = getBedPosition();
        if (lPos != null) {
            lPlayer.setBedSpawnLocation(lPos.getLocation(fWorld));
        }
        PlayerInventory lInv = lPlayer.getInventory();
        lInv.clear();
        lInv.setContents(fInventory);
        lInv.setBoots(fBoots);
        lInv.setChestplate(fChestplate);
        lInv.setHelmet(fHelmet);
        lInv.setLeggings(fLeggings);
        lInv.setItemInHand(fItemInHand);
        lPos = getPosition();
        if (lPos != null) {
            lPlayer.teleport(lPos.getLocation(fWorld));
        }
        lPlayer.setFoodLevel(getFoodLevel());
        lPlayer.setHealth(getHealth());
        lPlayer.setSaturation(getSaturation());
        aEntity.setDataObject(this);
        fEntityId = lPlayer.getEntityId();
    }

    public void updateFromEntity(NPCEntity aEntity) {
        Player lPlayer = aEntity.getAsPlayer();
        setPosition(new BlockPosition(lPlayer.getLocation()));
        setFoodLevel(lPlayer.getFoodLevel());
        setHealth(lPlayer.getHealth());
        setSaturation(lPlayer.getSaturation());
    }

    public void setEntityId(int aEntityId) {
        fEntityId = aEntityId;
    }

    public void createEntity() {
        NPCEntity lNPC = Framework.plugin.createNPC(fWorld, getPosition(), getDisplayName(), this);
        Framework.plugin.log("settler", "new settler '" + getDisplayName() + "' at " + getPosition());
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
    }

    public void deactivate() {
        fActive = false;
    }

    public boolean isActive() {
        return fActive;
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
    }
}
