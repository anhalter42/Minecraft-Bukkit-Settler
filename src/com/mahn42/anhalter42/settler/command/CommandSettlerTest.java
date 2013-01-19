/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.command;

import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.anhalter42.settler.settler.SettlerActivityWalkToTarget;
import com.mahn42.framework.BlockPosition;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class CommandSettlerTest implements CommandExecutor {

    //s_test newsettler
    @Override
    public boolean onCommand(CommandSender aCommandSender, Command aCommand, String aString, String[] aStrings) {
        World lWorld = SettlerPlugin.plugin.getServer().getWorld("world");
        if (aCommandSender instanceof Player) {
            lWorld = ((Player) aCommandSender).getWorld();
        }
        if (aStrings.length > 0) {
            if (aStrings[0].equalsIgnoreCase("new")) {
                if (aCommandSender instanceof Player) {
                    Player lPlayer = (Player) aCommandSender;
                    SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lPlayer.getWorld());
                    Settler lSettler = lAccess.createSettler(aStrings[1], null);
                    if (lSettler != null) {
                        lSettler.setSettlerName(SettlerPlugin.plugin.getRandomSettlerName());
                        BlockPosition lPos = new BlockPosition(lPlayer.getLocation());
                        lPos.add(1, 0, 1);
                        lSettler.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                        lSettler.setItemInHand(new ItemStack(Material.IRON_PICKAXE));
                        lSettler.setPosition(lPos);
                        lSettler.activate();
                        lPlayer.sendMessage("settler created.");
                    }
                }
            } else if (aStrings[0].equalsIgnoreCase("move")) {
                if (aCommandSender instanceof Player) {
                    Player lPlayer = (Player) aCommandSender;
                    SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lPlayer.getWorld());
                    Settler lSettler = lAccess.getSettlerById(Integer.parseInt(aStrings[1]));
                    if (lSettler != null) {
                        if (aStrings[2].equalsIgnoreCase("later")) {
                            lSettler.addActivityForLater(new SettlerActivityWalkToTarget(new BlockPosition(lPlayer.getLocation())));
                        } else if (aStrings[2].equalsIgnoreCase("next")) {
                            lSettler.addActivityForNext(new SettlerActivityWalkToTarget(new BlockPosition(lPlayer.getLocation())));
                        } else {
                            lSettler.addActivityForNow(new SettlerActivityWalkToTarget(new BlockPosition(lPlayer.getLocation())));
                        }
                        //lSettler.setTargetPosition(new BlockPosition(lPlayer.getLocation()));
                        //lSettler.setActivityState(Settler.ACTSTATE_START);
                        //lSettler.setActivity(Settler.ACT_WALK_TO_TARGET);
                    } else {
                        lPlayer.sendMessage("Settler with Id " + aStrings[1] + " not found!");
                    }
                }
            } else if (aStrings[0].equalsIgnoreCase("dump")) {
                SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
                Settler lSettler = null;
                if (aStrings[1].charAt(0) >= '0' && aStrings[1].charAt(0) <= 'A') {
                    lSettler = lAccess.getSettlerById(Integer.parseInt(aStrings[1]));
                } else {
                    Collection<? extends Settler> lSettlers = lAccess.getSettlers();
                    for(Settler lS : lSettlers) {
                        if (aStrings[1].equalsIgnoreCase(lS.getSettlerName())) {
                            lSettler = lS;
                            break;
                        }
                    }
                }
                if (lSettler != null) {
                    lSettler.dump();
                } else {
                    aCommandSender.sendMessage("Settler with Id " + aStrings[1] + " not found!");
                }
            } else if (aStrings[0].equalsIgnoreCase("time")) {
                long ltime = lWorld.getTime();
                long lt = 800 + ltime / 10;
                if (lt > 2359) {
                    lt -= 2400;
                }
                aCommandSender.sendMessage("Its " + ltime + " time. " + lt);
            } else if (aStrings[0].equalsIgnoreCase("y")) {
                int highestBlockYAt = lWorld.getHighestBlockYAt(((Player) aCommandSender).getLocation());
                Block highestBlockAt = lWorld.getHighestBlockAt(((Player) aCommandSender).getLocation());
                aCommandSender.sendMessage("y " + highestBlockYAt + " block " + highestBlockAt);
            } else if (aStrings[0].equalsIgnoreCase("reorg")) {
                ArrayList<Settler> settlers = SettlerPlugin.plugin.getSettlers(lWorld);
                SettlerAccess settlerAccess = SettlerPlugin.plugin.getSettlerAccess(lWorld);
                for (Settler lSettler : settlers) {
                    if (!lSettler.isActive()) {
                        settlerAccess.removeSettler(lSettler);
                        aCommandSender.sendMessage("settler " + lSettler.getDisplayName() + " was removed!");
                    }
                }
            } else if (aStrings[0].equalsIgnoreCase("isgrass")) {
                boolean lFound = true;
                BlockPosition lPos = new BlockPosition(((Player) aCommandSender).getLocation());
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        BlockPosition lP = lPos.clone();
                        lP.add(x, 0, z);
                        lP.y = lWorld.getHighestBlockYAt(lPos.x, lPos.z);
                        Material lMat = lP.getBlockType(lWorld);
                        while (lMat.equals(Material.AIR)) {
                            lP.y--;
                            lMat = lP.getBlockType(lWorld);
                        }
                        if (!Settler.grassOrDirt.contains(lMat)) {
                            aCommandSender.sendMessage("Mat: " + lMat.name());
                            lFound = false;
                            break;
                        }
                    }
                    if (!lFound) {
                        break;
                    }
                }
                aCommandSender.sendMessage("found: " + lFound);
            } else {
                aCommandSender.sendMessage("whats " + aStrings[0] + "?");
            }
        } else {
            aCommandSender.sendMessage("what?");
        }
        return true;
    }
}
