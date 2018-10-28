package me.lorinth.rpwarps.manager;

import me.lorinth.rpwarps.LorinthsRpWarps;
import me.lorinth.rpwarps.data.PlayerWarpProfile;
import me.lorinth.rpwarps.data.RpWarp;
import me.lorinth.rpwarps.util.OutputHandler;
import me.lorinth.rpwarps.util.TryParse;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class RpWarpManager {

    private static int currentID = 0;
    private static HashMap<String, PlayerWarpProfile> playerWarpProfiles;
    private static HashMap<Integer, RpWarp> warpList;
    private static HashMap<World, HashMap<Vector, RpWarp>> locationWarps;
    private static ArrayList<RpWarp> serverWarpList;

    private LorinthsRpWarps plugin;

    public RpWarpManager(LorinthsRpWarps plugin){
        this.plugin = plugin;

        playerWarpProfiles = new HashMap<>();
        warpList = new HashMap<>();
        serverWarpList = new ArrayList<>();
        locationWarps = new HashMap<>();

        load();
    }

    private void load(){
        YamlConfiguration warpConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "warps.yml"));
        if(warpConfig.contains("CurrentID"))
            currentID = warpConfig.getInt("CurrentID");

        //Load All warps
        for(String key : warpConfig.getKeys(false)){
            if(!key.equalsIgnoreCase("CurrentID")) {
                if(TryParse.parseInt(key)){
                    int id = Integer.parseInt(key);
                    RpWarp warp = new RpWarp(warpConfig, id);
                    storeWarp(warp);
                }
                else
                    OutputHandler.PrintError("Failed to load warp, " + OutputHandler.HIGHLIGHT + key + OutputHandler.ERROR + ", it is not an Integer ID");
            }
        }

        //Configure the ServerWarpsList
        if(warpConfig.contains("ServerWarps")){
            for(Integer key : warpConfig.getIntegerList("ServerWarps")){
                serverWarpList.add(getRpWarpByID(key));
            }
        }

        //Load player data
        File playerDataDirectory = new File(plugin.getDataFolder() + File.separator + "playerData");
        if(playerDataDirectory.exists() && playerDataDirectory.isDirectory()){
            for(File playerData : playerDataDirectory.listFiles()){
                String uuid = playerData.getName().split("\\.")[0];
                YamlConfiguration data = YamlConfiguration.loadConfiguration(playerData);
                playerWarpProfiles.put(uuid, new PlayerWarpProfile(data, uuid, this));
            }
        }
    }

    public void save(){
        File warpFile = new File(plugin.getDataFolder(), "warps.yml");
        if(warpFile.exists())
            warpFile.delete();

        YamlConfiguration warpConfig = YamlConfiguration.loadConfiguration(warpFile);
        warpConfig.set("CurrentID", currentID);
        for(RpWarp warp : warpList.values()){
            warp.save(warpConfig);
        }
        try{
            warpConfig.save(warpFile);
        }
        catch(Exception exception){
            OutputHandler.PrintException("Failed to save warps", exception);
        }

        for(PlayerWarpProfile profile : playerWarpProfiles.values())
            savePlayerProfile(profile);
    }

    public static void savePlayerProfile(PlayerWarpProfile profile){
        profile.save(new File(LorinthsRpWarps.instance.getDataFolder() + File.separator + "playerData", profile.getPlayerUUID() + ".yml"));
    }

    public static void createWarp(Player player, Block block){
        RpWarp warp = new RpWarp(player, block.getLocation());
        storeWarp(warp);
    }

    public static void deleteWarp(RpWarp warp){
        locationWarps.get(warp.getLocation().getWorld()).remove(warp.getLocation().toVector().toBlockVector());
        warpList.remove(warp.getId());

        for(PlayerWarpProfile profile : playerWarpProfiles.values())
            profile.removeWarp(warp);
    }

    public static void storeWarp(RpWarp warp){
        warpList.put(warp.getId(), warp);
        if(warp.isServerWarp())
            serverWarpList.add(warp);

        if(locationWarps.containsKey(warp.getLocation().getWorld())){
            locationWarps.get(warp.getLocation().getWorld()).put(warp.getLocation().toVector().toBlockVector(), warp);
        }
        else{
            HashMap<Vector, RpWarp> worldWarps = new HashMap<>();
            worldWarps.put(warp.getLocation().toVector().toBlockVector(), warp);
            locationWarps.put(warp.getLocation().getWorld(), worldWarps);
        }
    }

    public static RpWarp getWarpAtLocation(Location location){
        if(locationWarps.containsKey(location.getWorld())){
            HashMap<Vector, RpWarp> warps = locationWarps.get(location.getWorld());
            if(warps.containsKey(location.toVector().toBlockVector()))
                return warps.get(location.toVector().toBlockVector());
        }
        return null;
    }

    public static int getNextId(){
        return currentID++;
    }

    public static ArrayList<RpWarp> getServerWarpList(){
        return serverWarpList;
    }

    public static RpWarp getRpWarpByID(Integer id){
        if(warpList.containsKey(id))
            return warpList.get(id);
        return null;
    }

    public static RpWarp getWarpPlayerIsLookingAt(Player player){
        Block block = player.getTargetBlock(null, 10);
        return getWarpAtLocation(block.getLocation());
    }

    public static PlayerWarpProfile getProfileByPlayer(Player player){
        if(playerWarpProfiles.containsKey(player.getUniqueId().toString()))
            return playerWarpProfiles.get(player.getUniqueId().toString());
        else{
            PlayerWarpProfile profile = new PlayerWarpProfile(player);
            playerWarpProfiles.put(player.getUniqueId().toString(), profile);
            return profile;
        }
    }

    public static void addServerWarp(RpWarp warp){
        serverWarpList.add(warp);
    }

    public static void removeServerWarp(RpWarp warp){
        serverWarpList.remove(warp);
    }

}
