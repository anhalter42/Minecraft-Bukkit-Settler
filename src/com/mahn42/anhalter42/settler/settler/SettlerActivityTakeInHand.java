/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 *
 * @author andre
 */
public class SettlerActivityTakeInHand extends SettlerActivity {

    public static final String TYPE = "TakeInHand";

    public SettlerActivityTakeInHand() {
        type = TYPE;
    }

    public SettlerActivityTakeInHand(Material aMaterial, byte aData) {
        type = TYPE;
        material = aMaterial;
        data = aData;
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        aMap.put("material", material.getId());
        aMap.put("data", data);
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        Object lGet = aMap.get("material");
        if (lGet != null) {
            material = Material.getMaterial(Integer.parseInt(lGet.toString()));
        }
        lGet = aMap.get("data");
        if (lGet != null) {
            data = Byte.parseByte(lGet.toString());
        }
    }

    public Material material;
    public byte data;
    
    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        ItemStack lItemInHand = aSettler.getItemInHand();
        ItemStack lNew = new ItemStack(material, 1);
        lNew.setData(new MaterialData(material, data));
        lNew.setAmount(1);
        if (lItemInHand != null) {
            if (lItemInHand.isSimilar(lNew)) {
                return true;
            }
            aSettler.insertItems(lItemInHand); // lItemInHand.getType(), lItemInHand.getAmount()); //TODO data
            aSettler.setItemInHand(null);
        }
        if (aSettler.removeItems(lNew) >= 1) {
            aSettler.setItemInHand(lNew);
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " " + material.toString();
    }
}
