package me.lorinth.rpwarps.gui;

import me.lorinth.rpwarps.data.PlayerWarpProfile;
import me.lorinth.rpwarps.data.RpWarp;
import me.lorinth.rpwarps.manager.RpWarpManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class WarpMenu{

    private PlayerWarpProfile profile;
    private IconMenu menu;
    private HashMap<Integer, RpWarp> warps = new HashMap<>();
    private HashMap<Integer, RpWarp> serverWarps = new HashMap<>();
    private int page = 0;
    private int serverRows = -1;
    private int totalRows = 0;

    private final ItemStack divider = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE, 1);
    private final ItemStack up = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (byte) 1);
    private final ItemStack down = new ItemStack(Material.LEGACY_STAINED_GLASS_PANE, 1, (byte) 3);

    public WarpMenu(Player player){
        profile = RpWarpManager.getProfileByPlayer(player);

        int count = 0;
        for(RpWarp warp : profile.getDiscoveredWarps()){
            if(warp == null)
                continue;
            if(!warp.isServerWarp() && !warp.isAccessWarp()){
                warps.put(count, warp);
                count++;
            }
        }
        count = 0;
        for(RpWarp warp : RpWarpManager.getServerWarpList()){
            if(warp == null)
                continue;

            serverWarps.put(count, warp);
            count++;
        }
        if(serverWarps.size() > 0)
            serverRows = serverWarps.size() / 9 + 1;
        totalRows = serverRows + warps.size() / 9 + 1;
        menu = new IconMenu(ChatColor.DARK_PURPLE + "Warp Menu", 5, this::click);

        //OutputHandler.PrintInfo(player, "Server Rows : " + serverRows);
        //OutputHandler.PrintInfo(player, "Total Rows : " + totalRows);

        makePage(player);
    }

    public boolean click(Player clicker, IconMenu menu, IconMenu.Row row, int slot, ItemStack item){
        if(item != null){
            if(item.getType() == Material.LEGACY_STAINED_GLASS_PANE) {
                if (row.row == 0){
                    page -= 1;
                    makePage(clicker);
               }
                if(row.row == 4) {
                    page += 1;
                    makePage(clicker);
                }
            }
            else{
                if(isServerWarpRow(row.row)){
                    if(serverWarps.size() > getServerWarpSlot(row.row, slot)){
                        serverWarps.get(getServerWarpSlot(row.row, slot)).warp(clicker);
                        return false;
                    }
                }
                if(warps.containsKey(getPlayerWarpSlot(row.row, slot))){
                    warps.get(getPlayerWarpSlot(row.row, slot)).warp(clicker);
                    return false;
                }
            }
        }
        return true;
    }

    private void makePage(Player player){
        menu.clear(player);

        int startRow = 0;
        int endRow = 5;
        //OutputHandler.PrintInfo(player, "Opening Warp Menu");
        //OutputHandler.PrintInfo(player, warps.size() + " warps in profile");
        //OutputHandler.PrintInfo(player, "Page : " + page);

        if(page > 0){
            for(int i=0; i<9; i++)
                menu.addButton(menu.getRow(0), i, up.clone(), ChatColor.GREEN + "< - Go Up a Page - >");
            startRow++;
        }

        //OutputHandler.PrintInfo(player, "StartRow : " + startRow);

        //If there is a next page, add a bottom bar
        if(totalRows > page * 3 + 4){
            for(int i=0; i<9; i++)
                menu.addButton(menu.getRow(4), i, down.clone(), ChatColor.GREEN + "< - Go Down a Page - >");
            endRow--;
        }

        for(int currentRow = startRow; currentRow<endRow; currentRow++) {
            //OutputHandler.PrintInfo(player, "NextRow : " + i);
            if(getCurrentRow(currentRow) < serverRows){
                //OutputHandler.PrintInfo(player, "Is Server Row");
                for(int slot=0; slot<9; slot++){
                    int currentSlot = getServerWarpSlot(currentRow, slot);
                    if(serverWarps.containsKey(currentSlot)){
                        RpWarp warp = serverWarps.get(currentSlot);
                        if(warp != null){
                            menu.addButton(menu.getRow(currentRow), slot, warp.getIcon(), null);
                        }
                    }
                }
            }
            else if(getCurrentRow(currentRow) == serverRows){
                //OutputHandler.PrintInfo(player, "Make Divider");
                for(int slot=0; slot<9; slot++)
                    menu.addButton(menu.getRow(currentRow), slot, divider.clone(), "");
            }
            else{
                //OutputHandler.PrintInfo(player, "Make Normal Warp Row");
                for(int slot=0; slot<9; slot++){
                    int currentSlot = getPlayerWarpSlot(currentRow, slot);
                    //OutputHandler.PrintInfo(player, "Current Slot : " + currentSlot);
                    if(warps.containsKey(currentSlot)){
                        RpWarp warp = warps.get(currentSlot);
                        if(warp != null){
                            //OutputHandler.PrintInfo(player, "Warp Found : " + warp.getName());
                            menu.addButton(menu.getRow(currentRow), slot, warp.getIcon(), null);
                        }
                    }
                }
            }
        }

        menu.open(player);
    }

    private int getCurrentRow(int currentRow){
        return currentRow + page*3; //3 in each following page
    }

    private boolean isServerWarpRow(int row){
        return getCurrentRow(row) < serverRows;
    }

    private int getServerWarpSlot(int row, int slot){
        return getCurrentRow(row) * 9 + slot;
    }

    private int getPlayerWarpSlot(int row, int slot){
        return (getCurrentRow(row) - (serverRows + 1)) * 9 + slot;
    }

}
