/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.command;

import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.settler.Settler;
import java.util.ArrayList;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class CommandSettlerList implements CommandExecutor {

    //s_list [profession|name] [world]
    @Override
    public boolean onCommand(CommandSender aCommandSender, Command aCommand, String aString, String[] aStrings) {
        World lWorld = SettlerPlugin.plugin.getServer().getWorld("world");
        if (aCommandSender instanceof Player) {
            lWorld = ((Player)aCommandSender).getWorld();
        }
        String aProf = null;
        if (aStrings.length > 0) {
            aProf = aStrings[0];
        }
        if (aStrings.length > 1) {
            lWorld = SettlerPlugin.plugin.getServer().getWorld(aStrings[1]);
        }
        ArrayList<Settler> lSettlers = SettlerPlugin.plugin.getSettlers(lWorld);
        for(Settler lSettler : lSettlers) {
            if (lSettler.isActive() && (aProf == null || aProf.equalsIgnoreCase(lSettler.getProfession()) || aProf.equals(lSettler.getSettlerName()))) {
                aCommandSender.sendMessage(
                        lSettler.getSettlerName() + " " + 
                        lSettler.getProfession() + " " + 
                        lSettler.getPosition() + " " + 
                        lSettler.getEntityId() + " " +
                        (lSettler.getCurrentActivity() != null ? lSettler.getCurrentActivity().toString() : ""));
            }
        }
        return true;
    }
    
}
