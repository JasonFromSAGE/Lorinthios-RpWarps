package me.lorinth.rpwarps.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class OutputHandler {
    public static final ChatColor ERROR = ChatColor.RED;
    public static final ChatColor INFO = ChatColor.GREEN;
    public static final ChatColor COMMAND = ChatColor.GREEN;
    public static final ChatColor HIGHLIGHT = ChatColor.YELLOW;
    private static String consolePrefix = "[RPWarps] : ";
    private static String infoPrefix = INFO + consolePrefix;
    private static String errorPrefix = ERROR + "[Error]" + infoPrefix + ERROR;
    private static ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static void PrintInfo(String message){
        console.sendMessage(infoPrefix + message);
    }

    public static void PrintError(String message){
        console.sendMessage(errorPrefix + message);
    }

    public static void PrintException(String message, Exception exception){
        PrintError(message);
        exception.printStackTrace();
    }

    public static void PrintRawInfo(String message){ console.sendMessage(INFO + message); }

    public static void PrintRawError(String message){ console.sendMessage(ERROR + message); }

    public static void PrintInfo(Player player, String message){
        player.sendMessage(infoPrefix + message);
    }

    public static void PrintError(Player player, String message){
        player.sendMessage(errorPrefix + message);
    }

    public static void PrintRawInfo(Player player, String message){
        player.sendMessage(INFO + message);
    }

    public static void PrintRawError(Player player, String message){
        player.sendMessage(ERROR + message);
    }

    public static void PrintCommandInfo(Player player, String message){
        player.sendMessage(COMMAND + message);
    }

    public static void PrintWhiteSpace(Player player, int lines){
        for(int i=0; i<lines; i++)
            player.sendMessage("");
    }
}
