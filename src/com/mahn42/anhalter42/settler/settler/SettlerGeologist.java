/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerProfession;
import com.mahn42.framework.BlockPosition;
import java.util.Random;
import org.bukkit.Material;

/**
 *
 * @author andre
 */
public class SettlerGeologist extends Settler {

    public static final String typeName = "Geologist";
    public static final SettlerProfession profession = new SettlerProfession();

    public static void register() {
        profession.settlerClass = SettlerGeologist.class;
        profession.name = typeName;
        profession.frameMaterial = Material.SIGN;
        profession.armor.add(new SettlerProfession.Item(Material.IRON_PICKAXE, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_LEGGINGS, true));
        profession.armor.add(new SettlerProfession.Item(Material.LEATHER_BOOTS, false));
        profession.inventory.add(new SettlerProfession.Item(Material.SIGN, false));
        register(profession);
        SettlerActivity.registerActivity(SettlerGeologistThinking.TYPE, SettlerGeologistThinking.class);
    }

    public SettlerGeologist() {
        super(typeName);
    }

    @Override
    public void run(SettlerAccess aAccess) {
        if (getCurrentActivity() == null) {
            addActivityForNow(new SettlerGeologistThinking());
        }
        super.run(aAccess);
    }

    public static class SettlerGeologistThinking extends SettlerActivity {

        public static final String TYPE = "GeologistThinking";

        public SettlerGeologistThinking() {
            type = TYPE;
        }

        @Override
        public boolean run(SettlerAccess aAccess, Settler aSettler) {
            Random lRnd = new Random();
            boolean lFound = false;
            if (lRnd.nextBoolean()) {
                BlockPosition lPos = aSettler.getPosition();
                lPos.add(lRnd.nextInt(20) - 10, 0, lRnd.nextInt(20) - 10);
                //aSettler.getWorld().getHighestBlockAt(lPos.x, lPos.z).getType().isTransparent();
                lPos.y = aSettler.getWorld().getHighestBlockYAt(lPos.x, lPos.z);
                lFound = aSettler.canWalkTo(lPos);
                if (lFound) {
                    aSettler.addActivityForNext(new SettlerActivityWalkToTarget(lPos));
                }
            } else {
                aSettler.addActivityForNext(
                        new SettlerActivityStartSneaking(),
                        new SettlerActivitySwingArm(40),
                        new SettlerActivityStopSneaking());
                lFound = true;
            }
            return lFound;
        }
    }
}
