package me.lorinth.rpwarps.listener;

import me.lorinth.rpwarps.data.RpWarp;
import me.lorinth.rpwarps.manager.RpWarpManager;
import me.lorinth.rpwarps.util.OutputHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class RpWarpBlockListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.SEA_LANTERN) {
            Location loc = event.getBlock().getLocation();

            boolean one = loc.clone().add(1, 0, 0).getBlock().getType() == Material.STONE_BRICKS;
            boolean two = loc.clone().add(-1, 0, 0).getBlock().getType() == Material.STONE_BRICKS;
            boolean three = loc.clone().add(0, 0, 1).getBlock().getType() == Material.STONE_BRICKS;
            boolean four = loc.clone().add(0, 0, -1).getBlock().getType() == Material.STONE_BRICKS;

            if (one && two && three && four)
                RpWarpManager.createWarp(player, event.getBlock());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDestroy(BlockBreakEvent event){
        if (event.getBlock().getType() == Material.SEA_LANTERN) {
            Block block = event.getBlock();
            RpWarp warp = RpWarpManager.getWarpAtLocation(block.getLocation());
            Player player = event.getPlayer();
            if (warp == null)
                return;

            if (warp.hasEditRights(event.getPlayer()))
                warp.delete(player);
            else
                event.setCancelled(true);

        }
    }

    @EventHandler
    public void onBlockRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.OFF_HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.SEA_LANTERN) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        RpWarp warp = RpWarpManager.getWarpAtLocation(block.getLocation());

        if (warp != null) {
            warp.use(player);

            event.setCancelled(true);
        }
        else{
            OutputHandler.PrintInfo(player, "Not a warp");
        }
    }

}
