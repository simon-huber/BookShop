package me.ibhh.BookShop.Tools;

import me.ibhh.BookShop.BookShop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ToolsNoCraftbukkit extends Tools{

  @Override
    public Player getmyOfflinePlayer(BookShop plugin, String[] args, int index) {
        String playername = args[index];
        plugin.Logger("Empfaenger: " + playername, "Debug");
        Player player = plugin.getServer().getPlayerExact(playername);
        try {
            if (player == null) {
                player = plugin.getServer().getPlayer(playername);
            }
            if (player == null) {
                for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
                    OfflinePlayer offp = p;
                    if (offp.getName().toLowerCase().equals(playername.toLowerCase())) {
                        plugin.Logger("Player has same name: " + offp.getName(), "Debug");
                        if (offp != null) {
                            if (offp.hasPlayedBefore()) {
                                player = (Player) offp.getPlayer();
                                plugin.Logger("Player has Played before: " + offp.getName(), "Debug");
                            }
                            break;
                        }
                    }
                }
            }
            if (player != null) {
                plugin.Logger("Empfaengername after getting Player: " + player.getName(), "Debug");
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.Logger("Uncatched Exeption!", "Error");
            plugin.getReportHandler().report(3312, "Uncatched Exeption on getting offlineplayer", e.getMessage(), "BookShop", e);
        }
        return player;
    }

    @Override
    public Player getmyOfflinePlayer(BookShop plugin, String playername) {
        plugin.Logger("Empfaenger: " + playername, "Debug");
        Player player = plugin.getServer().getPlayerExact(playername);
        try {
            if (player == null) {
                player = plugin.getServer().getPlayer(playername);
            }
            if (player == null) {
                for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
                    OfflinePlayer offp = p;
                    if (offp.getName().toLowerCase().equals(playername.toLowerCase())) {
                        plugin.Logger("Player has same name: " + offp.getName(), "Debug");
                        if (offp != null) {
                            if (offp.hasPlayedBefore()) {
                                player = (Player) offp.getPlayer();
                                plugin.Logger("Player has Played before: " + offp.getName(), "Debug");
                            }
                            break;
                        }
                    }
                }
            }
            if (player != null) {
                plugin.Logger("Empfaengername after getting Player: " + player.getName(), "Debug");
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.Logger("Uncatched Exeption!", "Error");
            plugin.getReportHandler().report(3312, "Uncatched Exeption on getting offlineplayer", e.getMessage(), "BookShop", e);
        }
        return player;
    }
}
