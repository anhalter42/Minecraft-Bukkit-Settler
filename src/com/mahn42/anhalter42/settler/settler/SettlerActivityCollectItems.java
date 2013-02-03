/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerAccess.EntityState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
class SettlerActivityCollectItems extends SettlerActivity {

    public static final String TYPE = "CollectItems";
    public Collection<Material> items;

    public SettlerActivityCollectItems() {
        type = TYPE;
    }

    public SettlerActivityCollectItems(Collection<Material> aItems) {
        type = TYPE;
        items = aItems;
    }

    @Override
    public void serialize(Map<String, Object> aMap) {
        super.serialize(aMap);
        if (items != null) {
            ArrayList<Integer> lMatIds = new ArrayList<Integer>();
            for (Material lMat : items) {
                lMatIds.add(lMat.getId());
            }
            aMap.put("items", lMatIds);
        }
    }

    @Override
    public void deserialize(Map<String, Object> aMap) {
        super.deserialize(aMap);
        Object lGet = aMap.get("items");
        if (lGet != null) {
            items = new ArrayList<Material>();
            for (Object lObj : (Collection) lGet) {
                if (lObj != null) {
                    items.add(Material.getMaterial(Integer.parseInt(lObj.toString())));
                }
            }
        }
    }

    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        Collection<EntityState> lItems = aAccess.getEntityStatesNearby(aSettler.getPosition(), 2, items);
        final ArrayList<Integer> lIds = new ArrayList<Integer>();
        for (EntityState lItem : lItems) {
            lIds.add(lItem.id);
        }
        final World lWorld = aSettler.getWorld();
        final Settler lSettler = aSettler;
        runTaskLater(new Runnable() {
            @Override
            public void run() {
                Collection<Entity> lItems = lWorld.getEntitiesByClasses(Item.class);
                for (Entity lItem : lItems) {
                    if (!lItem.isDead()
                            && lItem.isValid()
                            && lIds.contains(lItem.getEntityId())
                            && lSettler.getPosition().distance(lItem.getLocation()) <= 2.5) {
                        ItemStack lStack = ((Item) lItem).getItemStack();
                        lSettler.insertItems(new ItemStack(lStack));
                        lItem.remove();
                    }
                }
            }
        });
        return true;
    }
}
