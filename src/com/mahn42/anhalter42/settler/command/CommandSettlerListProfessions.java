/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler.command;

import com.mahn42.anhalter42.settler.settler.Settler;
import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author andre
 */
public class CommandSettlerListProfessions implements CommandExecutor {

    //s_list_professions
    @Override
    public boolean onCommand(CommandSender aCommandSender, Command aCommand, String aString, String[] aStrings) {
        Set<String> settlerProfessions = Settler.getSettlerProfessions();
        for (String lProfName : settlerProfessions) {
            aCommandSender.sendMessage(lProfName);
        }
        return true;
    }
}
