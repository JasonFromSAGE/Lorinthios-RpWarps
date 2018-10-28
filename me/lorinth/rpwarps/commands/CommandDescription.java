package me.lorinth.rpwarps.commands;

import me.lorinth.rpwarps.util.OutputHandler;
import org.bukkit.entity.Player;

public class CommandDescription{

    private String[] permissions;
    private String commandName;
    private String description;
    private onCommand onCommand;

    public CommandDescription(String commandName, String description, onCommand command, String ... permissions){
        this.commandName = commandName;
        this.description = description;
        this.permissions = permissions;
        this.onCommand = command;
    }

    public String getCommandName(){
        return commandName;
    }

    public boolean hasPermission(Player player){
        for(String perm : permissions)
            if(!(player.hasPermission(perm) || player.isOp()))
                return false;
        return true;
    }

    public void send(Player player){
        OutputHandler.PrintCommandInfo(player, "/rpw " + commandName + " - " + OutputHandler.HIGHLIGHT + description);
    }

    public void execute(Player player, String[] args){
        onCommand.execute(player, args);
    }

    public interface onCommand {
        public abstract void execute(Player player, String[] args);
    }
}
