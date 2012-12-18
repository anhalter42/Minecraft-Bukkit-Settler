/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.command;

import com.mahn42.anhalter42.settler.SettlerPlugin;
import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.anhalter42.settler.SettlerAccess;
import com.mahn42.framework.BlockPosition;
import org.bukkit.Material;
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
        if (aStrings.length > 0) {
            if (aStrings[0].equalsIgnoreCase("new")) {
                if (aCommandSender instanceof Player) {
                    Player lPlayer = (Player)aCommandSender;
                    SettlerAccess lAccess = SettlerPlugin.plugin.getSettlerAccess(lPlayer.getWorld());
                    Settler lSettler = lAccess.createSettler(aStrings[1], null);
                    if (lSettler != null) {
                        lSettler.setSettlerName("Nils");
                        BlockPosition lPos = new BlockPosition(lPlayer.getLocation());
                        lPos.add(1, 0, 1);
                        lSettler.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                        lSettler.setItemInHand(new ItemStack(Material.IRON_PICKAXE));
                        lSettler.setPosition(lPos);
                        lSettler.activate();
                        lPlayer.sendMessage("settler created.");
                    }
                }
            } else {
                aCommandSender.sendMessage("whats " + aStrings[0] + "?");
            }
        } else {
            aCommandSender.sendMessage("what?");
        }
        return true;
    }
}
