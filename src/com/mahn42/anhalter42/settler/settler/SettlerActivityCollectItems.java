/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerAccess.EntityState;
import java.util.ArrayList;
import java.util.Collection;
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
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        Collection<EntityState> lItems = aAccess.getEntityStatesNearby(aSettler.getPosition(), 1, items);
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
                    if (!lItem.isDead() && lIds.contains(lItem.getEntityId()) && lSettler.getPosition().distance(lItem.getLocation()) < 1.5) {
                        ItemStack lStack = ((Item) lItem).getItemStack();
                        lItem.remove();
                        lSettler.insertItems(lStack.getType(), lStack.getAmount());
                    }
                }
            }
        });
        return true;
    }
}
