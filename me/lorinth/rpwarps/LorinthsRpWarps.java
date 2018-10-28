package me.lorinth.rpwarps;

import me.lorinth.rpwarps.commands.WarpCommandExecutor;
import me.lorinth.rpwarps.listener.RpWarpBlockListener;
import me.lorinth.rpwarps.listener.RpWarpPlayerListener;
import me.lorinth.rpwarps.manager.RpWarpManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LorinthsRpWarps extends JavaPlugin{

    private static RpWarpManager manager;
    public static LorinthsRpWarps instance;

    @Override
    public void onEnable(){
        manager = new RpWarpManager(this);
        instance = this;
        registerListeners();
    }

    @Override
    public void onDisable(){
        manager.save();
    }

    private void registerListeners(){
        Bukkit.getPluginManager().registerEvents(new RpWarpBlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new RpWarpPlayerListener(), this);

        getCommand("rpw").setExecutor(new WarpCommandExecutor());
    }

    public static RpWarpManager getRpWarpManager(){
        return manager;
    }

}
