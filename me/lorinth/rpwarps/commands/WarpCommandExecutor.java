package me.lorinth.rpwarps.commands;

import me.lorinth.rpwarps.data.RpWarp;
import me.lorinth.rpwarps.manager.RpWarpManager;
import me.lorinth.rpwarps.util.OutputHandler;
import me.lorinth.rpwarps.util.TryParse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class WarpCommandExecutor implements CommandExecutor{

    private ArrayList<CommandDescription> commands = new ArrayList<CommandDescription>(){{
       add(new CommandDescription("rename", "Rename the warp you are looking at", WarpCommandExecutor.this::renameWarp));
       add(new CommandDescription("icon", "Sets the warp you are looking at to the item in your hand", WarpCommandExecutor.this::setIcon));
       add(new CommandDescription("setaccess", "Sets the warp to be access only. You cannot warp back to these points", WarpCommandExecutor.this::setAccess));
        add(new CommandDescription("setserver", "Sets the warp to be a public server warp. Displays in a seperate section in the warp GUI", WarpCommandExecutor.this::setServerWarp));
    }};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;

            if(args.length == 0)
                sendHelp(player);
            else{
                String commandLabel = args[0];
                for(CommandDescription description : commands){
                    if(description.getCommandName().equalsIgnoreCase(commandLabel)){
                        if(description.hasPermission(player))
                            description.execute(player, Arrays.copyOfRange(args, 1, args.length));
                        else
                            OutputHandler.PrintError(player, "You don't have permission to use the command, " + OutputHandler.HIGHLIGHT + description.getCommandName());
                    }
                }
            }
        }
        return false;
    }

    private void sendHelp(Player player){
        OutputHandler.PrintInfo(player, "Command List");
        for(CommandDescription description : commands)
            if(description.hasPermission(player))
                description.send(player);
    }

    private void renameWarp(Player player, String[] args){
        RpWarp warp = RpWarpManager.getWarpPlayerIsLookingAt(player);
        if(warp == null){
            OutputHandler.PrintError(player, "This is not a warp!");
            return;
        }
        if(!warp.hasEditRights(player)) {
            OutputHandler.PrintError(player, "You dont have access to edit this warp!");
            return;
        }
        else{
            String name = "";
            for(String word : args)
                name += word + " ";

            name = ChatColor.translateAlternateColorCodes('&', name.trim());
            warp.setName(name);
            OutputHandler.PrintInfo(player, "Updated your warp with the name, " + name);
        }
    }

    private void setIcon(Player player, String[] args){
        RpWarp warp = RpWarpManager.getWarpPlayerIsLookingAt(player);
        if(warp == null){
            OutputHandler.PrintError(player, "This is not a warp!");
            return;
        }

        if(!warp.hasEditRights(player)) {
            OutputHandler.PrintError(player, "You dont have access to edit this warp!");
            return;
        }
        else{
            warp.setIcon(player.getEquipment().getItemInMainHand());
            OutputHandler.PrintInfo(player, "Set icon for, " + warp.getName() + OutputHandler.INFO + " to " + warp.getIcon().getType());
        }
    }

    private void setAccess(Player player, String[] args){
        RpWarp warp = RpWarpManager.getWarpPlayerIsLookingAt(player);
        if(warp == null){
            OutputHandler.PrintError(player, "This is not a warp!");
            return;
        }
        if(args.length == 0){
            OutputHandler.PrintError(player, "Usage : /rpw setaccess true/false");
            return;
        }
        if(!warp.hasEditRights(player)) {
            OutputHandler.PrintError(player, "You dont have access to edit this warp!");
            return;
        }
        else{
            if(TryParse.parseBoolean(args[0])) {
                warp.setAccessWarp(Boolean.parseBoolean(args[0]));
                OutputHandler.PrintInfo(player, warp.getName() + OutputHandler.INFO + " as an Access Point!");
            }
            else
                OutputHandler.PrintError(player, "Usage : /rpw setaccess true/false");
        }
    }

    private void setServerWarp(Player player, String[] args){
        RpWarp warp = RpWarpManager.getWarpPlayerIsLookingAt(player);
        if(warp == null){
            OutputHandler.PrintError(player, "This is not a warp!");
            return;
        }
        if(args.length == 0){
            OutputHandler.PrintError(player, "Usage : /rpw setserver true/false");
            return;
        }
        if(!warp.hasEditRights(player)) {
            OutputHandler.PrintError(player, "You dont have access to edit this warp!");
            return;
        }
        else{
            if(TryParse.parseBoolean(args[0])) {
                warp.setServerWarp(Boolean.parseBoolean(args[0]));
                OutputHandler.PrintInfo(player, warp.getName() + OutputHandler.INFO + " as a Server Warp Point!");
            }
            else
                OutputHandler.PrintError(player, "Usage : /rpw setserver true/false");
        }
    }
}
