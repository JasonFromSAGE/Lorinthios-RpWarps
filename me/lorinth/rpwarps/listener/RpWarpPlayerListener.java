package me.lorinth.rpwarps.listener;

import me.lorinth.rpwarps.manager.RpWarpManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RpWarpPlayerListener implements Listener {

    @EventHandler
    public void OnPlayerDisconnect(PlayerQuitEvent event){
        RpWarpManager.savePlayerProfile(RpWarpManager.getProfileByPlayer(event.getPlayer()));
    }

    @EventHandler
    public void OnPlayerKicked(PlayerKickEvent event){
        RpWarpManager.savePlayerProfile(RpWarpManager.getProfileByPlayer(event.getPlayer()));
    }

}
