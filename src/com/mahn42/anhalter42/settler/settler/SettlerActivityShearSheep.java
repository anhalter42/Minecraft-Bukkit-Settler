/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.Framework;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;

/**
 *
 * @author andre
 */
public class SettlerActivityShearSheep extends SettlerActivity {

    public static final String TYPE = "ShearSheep";

    public SettlerActivityShearSheep() {
        type = TYPE;
    }

    public SettlerActivityShearSheep(int aEntityId) {
        type = TYPE;
        entityId = aEntityId;
    }

    public int entityId = 0;
    
    @Override
    public boolean run(SettlerAccess aAccess, Settler aSettler) {
        final Settler lSettler = aSettler;
        runTaskLater(new Runnable() {
            @Override
            public void run() {
                List<LivingEntity> lEntities = lSettler.getWorld().getLivingEntities();
                for(LivingEntity lEntity : lEntities) {
                    if (lEntity.getEntityId() == entityId) {
                        if (lEntity instanceof Sheep) {
                            Sheep lSheep = (Sheep) lEntity;
                            if (!lSheep.isSheared()) {
                                Framework.plugin.shearSheep(lSheep);
                                //lSettler.fEntity.shearSheep(lSheep);
                                /*
                                lSheep.setSheared(true);
                                Wool lW = new Wool(lSheep.getColor());
                                int lCount = 1 + (new Random()).nextInt(3);
                                ItemStack lWool = new ItemStack(Material.WOOL, lCount);
                                lWool.setData(lW);
                                int insertedItems = lSettler.insertItems(lWool);
                                /*
                                if (insertedItems < lCount) {
                                    lWool = new ItemStack(Material.WOOL, lCount - insertedItems);
                                    lWool.setData(lW);
                                    lSheep.getWorld().dropItemNaturally(lSheep.getLocation(), lWool);
                                }
                                */
                                //SettlerPlugin.plugin.getLogger().info("dyecolor = " + lSheep.getColor().getData() + " sheep " + lSheep);
                                //lWool.setData(new MaterialData(Material.WOOL, lSheep.getColor().getData()));
                                //SettlerPlugin.plugin.getLogger().info("item = " + lWool + " " + lWool.getData() + " " + lWool.getData().getData());
                                //Item dropItemNaturally = lSheep.getWorld().dropItemNaturally(lSheep.getLocation(), lWool);
                                //SettlerPlugin.plugin.getLogger().info("dropped nat item = " + dropItemNaturally.getItemStack() + " " + dropItemNaturally.getItemStack().getData() + " " + dropItemNaturally.getItemStack().getData().getData());
                                //dropItemNaturally.getItemStack().setData(lW); // getItemStack().setData(new MaterialData(Material.WOOL, lSheep.getColor().getData()));
                                //SettlerPlugin.plugin.getLogger().info("dropped nat item = " + dropItemNaturally.getItemStack() + " " + dropItemNaturally.getItemStack().getData() + " " + dropItemNaturally.getItemStack().getData().getData());
                                //dropItemNaturally = lSheep.getWorld().dropItem(lSheep.getLocation(), lWool);
                                //SettlerPlugin.plugin.getLogger().info("dropped item = " + dropItemNaturally.getItemStack() + " " + dropItemNaturally.getItemStack().getData() + " " + dropItemNaturally.getItemStack().getData().getData());
                            }
                        }
                        break;
                    }
                }
            }
        });
        return true;
    }
    
}
