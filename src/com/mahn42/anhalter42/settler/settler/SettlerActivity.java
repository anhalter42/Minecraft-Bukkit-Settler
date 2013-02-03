/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.settler;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andre
 */
public abstract class SettlerActivity {

    public enum RunCondition {

        IfPreviousSuccess,
        IfPreviousFaild,
        Always
    }

    public static class Control {

        public String tag;
        public RunCondition condition = RunCondition.Always;
        public boolean previous_success = true;
        public boolean success = true;

        public Control() {
        }

        public Control(String aTag) {
            tag = aTag;
        }

        public Control(String aTag, RunCondition aCondition) {
            tag = aTag;
            condition = aCondition;
        }

        public Control(RunCondition aCondition) {
            condition = aCondition;
        }
    }
    
    public static HashMap<String, Class> activityTypes = new HashMap<String, Class>();

    public static void register() {
        registerActivity(SettlerActivityAwake.TYPE, SettlerActivityAwake.class);
        registerActivity(SettlerActivityCollectItems.TYPE, SettlerActivityCollectItems.class);
        registerActivity(SettlerActivityFight.TYPE, SettlerActivityFight.class);
        registerActivity(SettlerActivityFindRandomPath.TYPE, SettlerActivityFindRandomPath.class);
        registerActivity(SettlerActivityFindRandomTeleport.TYPE, SettlerActivityFindRandomTeleport.class);
        registerActivity(SettlerActivityGetItemsFromChest.TYPE, SettlerActivityGetItemsFromChest.class);
        registerActivity(SettlerActivityJump.TYPE, SettlerActivityJump.class);
        registerActivity(SettlerActivityNothing.TYPE, SettlerActivityNothing.class);
        registerActivity(SettlerActivityPlaceBlock.TYPE, SettlerActivityPlaceBlock.class);
        registerActivity(SettlerActivityPutItemsInChest.TYPE, SettlerActivityPutItemsInChest.class);
        registerActivity(SettlerActivityShearSheep.TYPE, SettlerActivityShearSheep.class);
        registerActivity(SettlerActivitySitDown.TYPE, SettlerActivitySitDown.class);
        registerActivity(SettlerActivitySleep.TYPE, SettlerActivitySleep.class);
        registerActivity(SettlerActivityStandUp.TYPE, SettlerActivityStandUp.class);
        registerActivity(SettlerActivityStartSneaking.TYPE, SettlerActivityStartSneaking.class);
        registerActivity(SettlerActivityStopSneaking.TYPE, SettlerActivityStopSneaking.class);
        registerActivity(SettlerActivitySwingArm.TYPE, SettlerActivitySwingArm.class);
        registerActivity(SettlerActivityTakeInHand.TYPE, SettlerActivityTakeInHand.class);
        registerActivity(SettlerActivityTeleport.TYPE, SettlerActivityTeleport.class);
        registerActivity(SettlerActivityWalkToTarget.TYPE, SettlerActivityWalkToTarget.class);
        registerActivity(SettlerActivityBreakBlock.TYPE, SettlerActivityBreakBlock.class);

    }

    public static void registerActivity(String aName, Class aClass) {
        activityTypes.put(aName, aClass);
    }

    public static SettlerActivity newInstance(String aTypename) {
        Class lClass = activityTypes.get(aTypename);
        if (lClass != null) {
            SettlerActivity lAct = null;
            try {
                lAct = (SettlerActivity) lClass.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(SettlerActivity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(SettlerActivity.class.getName()).log(Level.SEVERE, null, ex);
            }
            lAct.type = aTypename;
            return lAct;
        } else {
            return null;
        }
    }
    //META
    public String type;
    public int maxTicks = 20 * 60; // 1min
    public int runningTicks = 0;
    public Control control = new Control();

    public void serialize(Map<String, Object> aMap) {
        aMap.put("type", type);
        aMap.put("maxTicks", maxTicks);
        aMap.put("runningTicks", runningTicks);
        aMap.put("tag", control.tag);
    }

    public void deserialize(Map<String, Object> aMap) {
        Object lGet;
        lGet = aMap.get("type");
        if (lGet != null) {
            type = lGet.toString();
        }
        lGet = aMap.get("maxTicks");
        if (lGet != null) {
            maxTicks = Integer.parseInt(lGet.toString());
        }
        lGet = aMap.get("runningTicks");
        if (lGet != null) {
            runningTicks = Integer.parseInt(lGet.toString());
        }
        lGet = aMap.get("tag");
        if (lGet != null) {
            control.tag = lGet.toString();
        }
    }

    @Override
    public String toString() {
        return (control.tag == null ? "" : control.tag + " ") + type + " " + (maxTicks - runningTicks);
    }

    public abstract boolean run(SettlerAccess aAccess, Settler aSettler);

    public void targetReached(SettlerAccess aAccess, Settler aSettler) {
    }

    public void deactivate(Settler aSettler) {
    }

    public void runTaskLater(Runnable aRunnable) {
        runTaskLater(aRunnable, 1);
    }

    public void runTaskLater(Runnable aRunnable, int aTicks) {
        SettlerPlugin.plugin.getServer().getScheduler().runTaskLater(SettlerPlugin.plugin, aRunnable, aTicks);
    }
}
