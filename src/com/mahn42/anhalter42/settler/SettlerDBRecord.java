/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.DBRecordWorld;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andre
 */
public class SettlerDBRecord extends DBRecordWorld {

    public String profession = "Settler";
    public BlockPosition position = new BlockPosition();
    public BlockPosition bedPosition = new BlockPosition();
    public String playerName = "";
    public String clanName = "";
    public String homeKey = "";
    public String blob = "";
    public String settlerName = "";
    public boolean active;

    @Override
    protected void toCSVInternal(ArrayList aCols) {
        super.toCSVInternal(aCols);
        aCols.add(profession);
        aCols.add(position == null ? "" : position.toCSV(","));
        aCols.add(bedPosition == null ? "" : bedPosition.toCSV(","));
        aCols.add(playerName);
        aCols.add(clanName);
        aCols.add(homeKey);
        aCols.add(settlerName);
        aCols.add(active);
        aCols.add(Base64.encode(blob.getBytes(), 0));
    }

    @Override
    protected void fromCSVInternal(DBRecordCSVArray aCols) {
        super.fromCSVInternal(aCols);
        profession = aCols.pop();
        String lPop = aCols.pop();
        if (!lPop.isEmpty()) {
            position.fromCSV(lPop, "\\,");
        } else {
            position = null;
        }
        lPop = aCols.pop();
        if (!lPop.isEmpty()) {
            bedPosition.fromCSV(lPop, "\\,");
        } else {
            bedPosition = null;
        }
        playerName = aCols.pop();
        clanName = aCols.pop();
        homeKey = aCols.pop();
        settlerName = aCols.pop();
        active = Boolean.parseBoolean(aCols.pop());
        try {
            blob = new String(Base64.decode(aCols.pop()));
        } catch (Base64DecodingException ex) {
            Logger.getLogger(SettlerDBRecord.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
