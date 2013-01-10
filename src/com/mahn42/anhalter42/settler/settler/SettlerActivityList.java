/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author andre
 */
public class SettlerActivityList {

    public Settler settler;
    //protected int fPos = -1;
    protected ArrayList<SettlerActivity> fActivities = new ArrayList<SettlerActivity>();

    public SettlerActivityList(Settler aSettler) {
        settler = aSettler;
    }
    
    public void serialize(YamlConfiguration aYaml, String aPath) {
        ArrayList llist = new ArrayList();
        for (SettlerActivity lAct : fActivities) {
            Map<String, Object> lMap = new HashMap<String, Object>();
            lAct.serialize(lMap);
            llist.add(lMap);
        }
        aYaml.set(aPath, llist);
        //aYaml.set(aPath+"_pos", fPos);
    }

    public void deserialize(YamlConfiguration aYaml, String aPath) {
        fActivities.clear();
        for (Map lMap : aYaml.getMapList(aPath)) {
            Object lTypeObj = lMap.get("type");
            if (lTypeObj != null) {
                SettlerActivity lAct = SettlerActivity.newInstance(lTypeObj.toString());
                if (lAct != null) {
                    lAct.deserialize(lMap);
                    fActivities.add(lAct);
                } else {
                    SettlerPlugin.plugin.getLogger().info("Unkown SettlerActivity '" + lTypeObj + "' !");
                }
            }
        }
        //fPos = aYaml.getInt(aPath+"_pos", fPos);
    }

    public boolean isEmpty() {
        return fActivities.isEmpty();
    }

    public SettlerActivity peek() {
        if (!isEmpty()) {
            return fActivities.get(0); //fPos
        } else {
            return null;
        }
    }

    // jetzt zu tun (aktuelles wird unterbrochen und nach hinten verschoben)
    public SettlerActivity push(SettlerActivity e) {
        if (!isEmpty()) {
            SettlerActivity lAct = peek();
            lAct.deactivate(settler);
            fActivities.add(null);
            for (int i = fActivities.size() - 1; i > 0; i--) {
                fActivities.set(i, fActivities.get(i - 1));
            }
            fActivities.set(0, e);
        } else {
            fActivities.add(e);
        }
        return e;
    }

    // später zu tun (am ende der aktuellen liste)
    public SettlerActivity add(SettlerActivity e) {
        fActivities.add(e);
        return e;
    }

    public void remove(SettlerActivity e) {
        fActivities.remove(e);
    }

    public void dump(Logger l) {
        for (SettlerActivity lAct : fActivities) {
            l.info(lAct.toString());
        }
    }

    // direkt nach der aktuellen aktivität
    public SettlerActivity addAsNext(SettlerActivity aActivity) {
        if (isEmpty()) {
            return add(aActivity);
        } else {
            fActivities.add(null);
            for (int i = fActivities.size() - 1; i > 1; i--) {
                fActivities.set(i, fActivities.get(i - 1));
            }
            fActivities.set(1, aActivity);
            return aActivity;
        }
    }
    
    public boolean exists(Class aClass) {
        for(SettlerActivity lAct : fActivities) {
            if (aClass.isInstance(lAct)) {
                return true;
            }
        }
        return false;
    }

    public boolean existsTag(String aTag) {
        for(SettlerActivity lAct : fActivities) {
            if (lAct.tag == aTag) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return fActivities.size();
    }
}
