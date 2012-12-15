/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerDBRecord;
import com.mahn42.framework.BlockPosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

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
    
    protected String fKey;
    protected String fProfession;
    protected BlockPosition fPosition;
    protected BlockPosition fBedPosition;
    protected String fPlayerName;
    protected String fClanName;
    protected String fHomeKey;
    protected String fSettlerName;
    
    protected World fWorld;
    
    protected ItemStack fBoots;
    protected ItemStack fLeggings;
    protected ItemStack fChestplate;
    protected ItemStack fHelmet;
    protected ItemStack fItemInHand;
    
    protected ItemStack[] fInventory = new ItemStack[64];
    
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
        return fPosition;
    }
    
    public void setPosition(BlockPosition aPos) {
        if (aPos != null) {
            fPosition = aPos.clone();
        }
    }
    
    public BlockPosition getBedPosition() {
        return fBedPosition;
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
        aRecord.profession = getProfession();
        aRecord.playerName = getPlayerName();
        aRecord.clanName = getClanName();
        aRecord.settlerName = getSettlerName();
        aRecord.homeKey = getHomeKey();
        aRecord.position = getPosition();
        aRecord.bedPosition = getBedPosition();
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
        YamlConfiguration lYaml = new YamlConfiguration();
        try {
            lYaml.loadFromString(aRecord.blob);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(Settler.class.getName()).log(Level.SEVERE, null, ex);
        }
        deserialize(lYaml);
    }
    
    protected void serialize(YamlConfiguration aValues) {
        aValues.set("boots", fBoots.serialize());
        aValues.set("leggings", fLeggings.serialize());
        aValues.set("chestplate", fChestplate.serialize());
        aValues.set("helmet", fHelmet.serialize());
        ArrayList<Map> lItems = new ArrayList<Map>();
        for(ItemStack lItem : fInventory) {
            if (lItem != null) {
                Map<String, Object> lMap = lItem.serialize();
                lItems.add(lMap);
            } else {
                lItems.add(null);
            }
        }
        aValues.set("inventory", lItems);
        aValues.set("iteminhand", fItemInHand.serialize());
    }
    
    protected void deserialize(YamlConfiguration aValues) {
        Object lObj;
        lObj = aValues.get("boots");
        if (lObj instanceof Map) {
            fBoots = ItemStack.deserialize((Map<String, Object>)lObj);
        }
        lObj = aValues.get("leggings");
        if (lObj instanceof Map) {
            fLeggings = ItemStack.deserialize((Map<String, Object>)lObj);
        }
        lObj = aValues.get("chestplate");
        if (lObj instanceof Map) {
            fChestplate = ItemStack.deserialize((Map<String, Object>)lObj);
        }
        lObj = aValues.get("helmet");
        if (lObj instanceof Map) {
            fHelmet = ItemStack.deserialize((Map<String, Object>)lObj);
        }
        lObj = aValues.get("inventory");
        if (lObj instanceof ArrayList) {
            int lIndex = 0;
            for(Object lItem : (ArrayList)lObj) {
                if (lItem == null || lItem.toString().equals("null")) {
                } else {
                    ItemStack lIStack = ItemStack.deserialize((Map)lItem);
                    fInventory[lIndex] = lIStack;
                }
                lIndex++;
            } 
        }
        lObj = aValues.get("iteminhand");
        if (lObj instanceof Map) {
            fItemInHand = ItemStack.deserialize((Map<String, Object>)lObj);
        }
    }
    
    public void run() {
    }

    public String getIconName() {
        return "default";
    }

    public String getDisplayName() {
        String lRes = getSettlerName();
        if (lRes == null || lRes.isEmpty()) {
            lRes = getProfession();
        }
        return lRes;
    }
}
