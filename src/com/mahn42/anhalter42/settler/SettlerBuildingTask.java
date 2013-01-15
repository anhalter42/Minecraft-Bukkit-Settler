/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.settler;

import com.mahn42.anhalter42.settler.settler.Settler;
import com.mahn42.anhalter42.settler.settler.SettlerActivityAwake;
import com.mahn42.anhalter42.settler.settler.SettlerActivitySleep;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.BuildingBlock;
import com.mahn42.framework.InventoryHelper;
import java.util.Collection;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author andre
 */
public class SettlerBuildingTask implements Runnable {

    public enum Kind {
        Initialize,
        Check,
        SettlerDied,
        Destroy
    }
    public Kind kind = Kind.Initialize;
    public SettlerBuilding building;
    public Settler settler;
    protected SettlerAccess fAccess;

    public SettlerBuildingTask(Kind aKind, SettlerBuilding aBuilding) {
        kind = aKind;
        building = aBuilding;
    }

    @Override
    public void run() {
        fAccess = SettlerPlugin.plugin.getSettlerAccess(building.world);
        switch (kind) {
            case Initialize:
                initBuilding();
                break;
            case Check:
                checkBuilding();
                break;
            case SettlerDied:
                rebornSettler();
                break;
            case Destroy:
                destroyBuilding();
                break;
        }
    }

    private void initBuilding() {
        //TODO setup settlers
        //building.settlerCount;
        Chest lChest = (Chest) building.getBlock("chest").position.getBlock(building.world).getState();
        Inventory lChestInv = lChest.getInventory();
        for (int lIndex = 1; lIndex <= building.settlerCount; lIndex++) {
            BuildingBlock lBlock = building.getBlock("bed" + lIndex);
            if (lBlock == null && lIndex == 1) {
                lBlock = building.getBlock("bed");
            }
            if (lBlock != null) {
                Settler lSettler = fAccess.createSettler(building.basicProfession, building.key);
                if (lSettler != null) {
                    lSettler.setPlayerName(building.playerName);
                    BlockPosition lPos = lBlock.position.clone();
                    byte data = lBlock.position.getBlock(lSettler.getWorld()).getData();
                    if ((data & 0x08) == 0x00) {
                        switch((data & 0x03)) {
                            case 0: // south
                                lPos.z++;
                                break;
                            case 1: // west
                                lPos.x--;
                                break;
                            case 2: // north
                                lPos.z--;
                                break;
                            case 3: // east
                                lPos.x++;
                                break;
                        }
                    }
                    lSettler.setBedPosition(lPos);
                    lPos.add(0, 1, 0);
                    lSettler.setPosition(lPos);
                    bearSettler(lChestInv, lSettler);
                }
            }
        }
    }

    private void checkBuilding() {
        Collection<? extends Settler> lSettlers = fAccess.getSettlersForHomeKey(building.key);
        if (lSettlers.size() < building.settlerCount) {
            //TODO check which settlers are killed and if enough items to reborn settler
            for(Settler lSettler : lSettlers) {
            }
        }
    }

    private void rebornSettler() {
        Chest lChest = (Chest) building.getBlock("chest").position.getBlock(building.world).getState();
        Inventory lChestInv = lChest.getInventory();
        BlockPosition lPos = settler.getBedPosition();
        lPos.add(0, 1, 0);
        settler.setPosition(lPos);
        bearSettler(lChestInv, settler);
    }
    
    private void bearSettler(Inventory lChestInv, Settler aSettler) {
        if (aSettler.getSettlerName() == null) {
            aSettler.setSettlerName(SettlerPlugin.plugin.getRandomSettlerName());
        }
        SettlerProfession lProf = aSettler.getProf();
        for (SettlerProfession.Item lItem : lProf.armor) {
            if (InventoryHelper.hasAtleastItems(lChestInv, lItem.item.getType(), lItem.item.getAmount())) {
                InventoryHelper.removeItems(lChestInv, lItem.item.getType(), lItem.item.getAmount());
                aSettler.setArmor(lItem.item);
            }
        }
        aSettler.addActivityForNow(new SettlerActivitySleep(40), new SettlerActivityAwake());
        aSettler.activate();
        building.sendToPlayer("Settler %s was born.", aSettler.getDisplayName());
    }

    private void destroyBuilding() {
        Collection<? extends Settler> lSettlers = fAccess.getSettlersForHomeKey(building.key);
        for(Settler lSettler : lSettlers) {
            fAccess.removeSettler(lSettler);
        }
    }

}
