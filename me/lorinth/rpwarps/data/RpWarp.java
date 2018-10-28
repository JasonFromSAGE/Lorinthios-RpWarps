package me.lorinth.rpwarps.data;

import me.lorinth.rpwarps.gui.WarpMenu;
import me.lorinth.rpwarps.manager.RpWarpManager;
import me.lorinth.rpwarps.util.ConfigHelper;
import me.lorinth.rpwarps.util.OutputHandler;
import me.lorinth.rpwarps.util.TryParse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RpWarp {

    private int id;
    private short durability;
    private String name;
    private String ownerUUID;
    private Location location;
    private List<String> blockedPlayerUUIDs = new ArrayList<>();
    private Material icon = Material.EYE_OF_ENDER;

    private boolean isServerWarp = false; //Displayed Seperately
    private boolean isAccessWarp = false; //Unlearnable

    public RpWarp(FileConfiguration config, int id){
        this.id = id;
        load(config, id);
    }

    public RpWarp(Player player, Location location){
        id = RpWarpManager.getNextId();
        this.ownerUUID = player.getUniqueId().toString();
        this.location = location;
        name = player.getDisplayName() + "'s Warp";

        OutputHandler.PrintCommandInfo(player, "You have created a new warp!");
        RpWarpManager.getProfileByPlayer(player).addWarp(this);
    }

    private void load(FileConfiguration config, int id){
        String path = id + ".";
        name = config.getString(path + "Name");
        ownerUUID = config.getString(path + "Owner");
        location = ConfigHelper.loadLocation(config, path);

        String iconMaterial = config.getString(path + "Icon");
        if(TryParse.parseMaterial(iconMaterial))
            icon = Material.valueOf(iconMaterial);
        else
            icon = Material.EYE_OF_ENDER;

        blockedPlayerUUIDs = config.getStringList(path + "BlockedPlayers");
        isServerWarp = config.getBoolean(path + "IsServerWarp");
        isAccessWarp = config.getBoolean(path + "IsAccessWarp");
    }

    public void save(FileConfiguration config){
        String path = id + ".";
        config.set(path + "Name", name);
        config.set(path + "Owner", ownerUUID);
        config.set(path + "Icon", icon.name());
        config.set(path + "BlockedPlayers", blockedPlayerUUIDs);
        config.set(path + "IsServerWarp", isServerWarp);
        config.set(path + "IsAccessWarp", isAccessWarp);
        ConfigHelper.saveLocation(config, path, location);
    }

    public void use(Player player){
        if(blockedPlayerUUIDs.contains(player.getUniqueId().toString())){
            OutputHandler.PrintError(player, "You are unable to use this warp");
            return;
        }
        else{
            PlayerWarpProfile profile = RpWarpManager.getProfileByPlayer(player);
            if(!isServerWarp && !isAccessWarp)
                profile.addWarp(this);

            new WarpMenu(player);
        }
    }

    public void warp(Player player){
        if(!blockedPlayerUUIDs.contains(player.getUniqueId().toString()) || isServerWarp){
            player.teleport(getLocation().clone().add(0.5, 1.2, 0.5));
            OutputHandler.PrintCommandInfo(player, "Warped to, " + name);
            //Do Particle Effect
        }
        else{
            OutputHandler.PrintError(player, "You are unable to use this warp, removing it from your list");
            RpWarpManager.getProfileByPlayer(player).removeWarp(this);
        }
    }

    public ItemStack getIcon(){
        ItemStack item = new ItemStack(icon, 1, durability);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getName());

        Player player = Bukkit.getServer().getPlayer(UUID.fromString(ownerUUID));
        String playerName = player != null ? player.getDisplayName() : Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID)).getName();

        if(isServerWarp) {
            List<String> lore = new ArrayList<String>() {{
                add(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "★ - Official Warp - ★");
            }};
            meta.setLore(lore);
        }
        else{
            List<String> lore = new ArrayList<String>() {{
                add(ChatColor.GREEN + "Owner : " + ChatColor.GOLD + playerName);
                add(ChatColor.GREEN + "World : " + ChatColor.GOLD + location.getWorld().getName());
                add(String.format(ChatColor.GREEN + "Location : <%s, %s, %s>", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            }};
            meta.setLore(lore);
        }

        if(isServerWarp){
            meta.addEnchant(Enchantment.getByName("DURABILITY"), 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }

    public int getId(){
        return id;
    }

    public void setIcon(ItemStack item){
        if(item != null){
            if(item.getType() != Material.AIR)
                icon = item.getType();
            else {
                icon = Material.EYE_OF_ENDER;
                durability = item.getDurability();
            }
        }
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = ChatColor.translateAlternateColorCodes('&', name);
    }

    public Player getOwner(){
        return Bukkit.getPlayer(UUID.fromString(ownerUUID));
    }

    public Location getLocation(){
        return location.clone();
    }

    public boolean isServerWarp(){
        return isServerWarp;
    }

    public boolean isAccessWarp(){
        return isAccessWarp;
    }

    public void setServerWarp(boolean isServerWarp){
        this.isServerWarp = isServerWarp;

        if(isServerWarp)
            RpWarpManager.addServerWarp(this);
        else
            RpWarpManager.removeServerWarp(this);
    }

    public void setAccessWarp(boolean isAccessWarp){
        this.isAccessWarp = isAccessWarp;
    }

    public boolean hasEditRights(Player player){
        if(isServerWarp || isAccessWarp)
            return player.hasPermission("rpwarps.admin") || player.isOp();
        else
            return player.getUniqueId().toString().equalsIgnoreCase(ownerUUID) || player.hasPermission("rpwarps.admin");
    }

    public void delete(Player player){
        OutputHandler.PrintCommandInfo(player, OutputHandler.ERROR + "Deleted the Warp, " + name);
        RpWarpManager.deleteWarp(this);
    }

}
