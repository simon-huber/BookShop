/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Simon
 */
public class Help {

    private BookShop plugin;

    /**
     * Konstruktor of help page
     *
     * @param pl
     */
    public Help(BookShop pl) {
        plugin = pl;
    }

    /**
     * Returns help to player
     *
     * @param sender
     * @param args
     */
    public void help(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            for (String command : plugin.commands) {
                if (plugin.PermissionsHandler.checkpermissionssilent(player, plugin.getConfig().getString("help.commands." + command + ".permission"))) {
                    plugin.PlayerLogger(
                            player,
                            plugin.getConfig().getString("help.commands." + command + ".usage"),
                            "");
                }
            }
        } else if (args.length == 2) {
            boolean found = false;
            for (String command : plugin.commands) {
                if (plugin.getConfig().getString("help.commands." + command + ".name").equalsIgnoreCase(args[1])) {
                    if (plugin.PermissionsHandler.checkpermissions(player, plugin.getConfig().getString("help.commands." + command + ".permission"))) {
                        plugin.PlayerLogger(
                                player,
                                plugin.getConfig().getString("help.commands." + command + ".description." + plugin.config.language),
                                "");
                        found = true;
                    }
                }
            }
            if (!found) {
                for (String command : plugin.commands) {
                    if (plugin.PermissionsHandler.checkpermissionssilent(player, plugin.getConfig().getString("help.commands." + command + ".permission"))) {
                        plugin.PlayerLogger(player, "-----------", "");
                        plugin.PlayerLogger(
                                player,
                                plugin.getConfig().getString("help.commands." + command + ".usage"),
                                "");
                        plugin.PlayerLogger(
                                player,
                                plugin.getConfig().getString("help.commands." + command + ".description." + plugin.config.language),
                                "");
                    }
                }
            }
        } else if (args.length == 1) {
            boolean found = false;
            for (String command : plugin.commands) {
                if (plugin.getConfig().getString("help.commands." + command + ".name").equalsIgnoreCase(args[0])) {
                    if (plugin.PermissionsHandler.checkpermissions(player, plugin.getConfig().getString("help.commands." + command + ".permission"))) {
                        plugin.PlayerLogger(
                                player,
                                plugin.getConfig().getString("help.commands." + command + ".description." + plugin.config.language),
                                "");
                        found = true;
                    }
                }
            }
            if (!found) {
                for (String command : plugin.commands) {
                    if (plugin.PermissionsHandler.checkpermissionssilent(player, plugin.getConfig().getString("help.commands." + command + ".permission"))) {
                        plugin.PlayerLogger(player, "-----------", "");
                        plugin.PlayerLogger(
                                player,
                                plugin.getConfig().getString("help.commands." + command + ".usage"),
                                "");
                        plugin.PlayerLogger(
                                player,
                                plugin.getConfig().getString("help.commands." + command + ".description." + plugin.config.language),
                                "");
                    }
                }
            }
        }
    }
}
