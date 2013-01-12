/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
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
                            if (lSheep.isSheared()) {
                                lSheep.setSheared(true);
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
