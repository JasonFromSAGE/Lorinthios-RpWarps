package me.lorinth.rpwarps.data;

import me.lorinth.rpwarps.manager.RpWarpManager;
import me.lorinth.rpwarps.util.OutputHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerWarpProfile {

    private String playerUUID;
    private ArrayList<RpWarp> discoveredWarps = new ArrayList<>();

    public PlayerWarpProfile(FileConfiguration config, String uuid, RpWarpManager manager){
        playerUUID = uuid;
        load(config, manager);
    }

    public PlayerWarpProfile(Player player){
        playerUUID = player.getUniqueId().toString();
    }

    private void load(FileConfiguration config, RpWarpManager manager){
        if(config.contains("LearnedWarps")){
            for(Integer warpId : config.getIntegerList("LearnedWarps"))
                discoveredWarps.add(manager.getRpWarpByID(warpId));
        }
    }

    public void save(File file){
        if(warpCount() > 0){
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            ArrayList<Integer> warpIds = new ArrayList<>();
            for(RpWarp warp : discoveredWarps){
                if(warp != null)
                    warpIds.add(warp.getId());
            }

            config.set("LearnedWarps", warpIds);

            try{
                config.save(file);
            }
            catch(Exception exception){
                OutputHandler.PrintException("Failed to save user profile for, " + playerUUID, exception);
            }
        }
        else{
            if(file.exists())
                file.delete();
        }

    }

    public String getPlayerUUID(){
        return playerUUID;
    }

    public ArrayList<RpWarp> getDiscoveredWarps(){
        return discoveredWarps;
    }

    public boolean knowsWarp(RpWarp warp){
        return discoveredWarps.contains(warp);
    }

    public void addWarp(RpWarp warp){
        if(!knowsWarp(warp)) {
            discoveredWarps.add(warp);
            OutputHandler.PrintInfo(Bukkit.getPlayer(UUID.fromString(playerUUID)), "You have discovered the warp, " + warp.getName());
        }
    }

    public void removeWarp(RpWarp warp){
        if(discoveredWarps.contains(warp))
            discoveredWarps.remove(warp);
    }

    public Integer warpCount(){
        return discoveredWarps.size();
    }

}
