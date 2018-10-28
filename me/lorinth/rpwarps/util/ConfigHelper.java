package me.lorinth.rpwarps.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class ConfigHelper {

    public static boolean ConfigContainsPath(FileConfiguration config, String path){
        if(path == null || config == null) {
            return false;
        }

        String[] pathParts = path.split("\\.");
        if(pathParts.length == 0){
            return false;
        }

        String keyToSearchFor = pathParts[pathParts.length-1];
        String configSection = makeConfigSection(Arrays.copyOfRange(pathParts, 0, pathParts.length-1));
        if(!configSection.equalsIgnoreCase("")){
            ConfigurationSection section = config.getConfigurationSection(configSection);
            if(section == null)
                return false;

            return section.getKeys(false).contains(keyToSearchFor);
        }
        else
            return config.getKeys(false).contains(keyToSearchFor);
    }

    private static String makeConfigSection(String[] pieces){
        String result = "";

        for(String piece : pieces){
            result += piece + ".";
        }

        if(!result.equalsIgnoreCase(""))
            result = result.substring(0, result.length() - 1);
        return result;
    }

    public static void saveLocation(FileConfiguration config, String path, Location location){
        path = path + "Location.";
        config.set(path + "World", location.getWorld().getName());
        config.set(path + "X", location.getX());
        config.set(path + "Y", location.getY());
        config.set(path + "Z", location.getZ());
        config.set(path + "Yaw", location.getYaw());
        config.set(path + "Pitch", location.getPitch());
    }

    public static Location loadLocation(FileConfiguration config, String path){
        path = path + "Location.";
        World world = Bukkit.getWorld(config.getString(path + "World"));
        Integer x = config.getInt(path + "X");
        Integer y = config.getInt(path + "Y");
        Integer z = config.getInt(path + "Z");
        float yaw = (float) config.getDouble(path + "Yaw");
        float pitch = (float) config.getDouble(path + "Pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }
}
