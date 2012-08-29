/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Simon
 */
public class ConfigHandler {
    //define globale variables

    private BookShop plugin;
    public String language,
            commanderrorelse,
            commanderrornoint,
            commanderrornoargs0,
            commanderrortoomanyarguments,
            commanderrorfewargs,
            commanderrornoplayer,
            permissionserror,
            permissionsnotfound,
            iConomyerror,
            infoPrefix,
            Shopsuccessbuy,
            Shopsuccesssellerbuy,
            Shoperrornotenoughmoneyconsumer,
            playernotonline,
            playerwasntonline;
    public boolean autodownload,
            debug,
            debugfile,
            autoinstall,
            Internet,
            UsePrefix;
    public double TaskRepeat, DelayTimeTask;
    public ChatColor Prefix, Text;

    /**
     * Konstruktor
     *
     * @param pl
     */
    public ConfigHandler(BookShop pl) {
        plugin = pl;
    }

    public void updatetonew77() {
        if (plugin.getConfig().contains("help.buy.de")) {
            plugin.Logger("Converting config.yml!", "Warning");
            plugin.Logger("Please delete marked rows!", "Warning");
            plugin.getConfig().set("help.buy.de", "Please delete this row!");
            plugin.getConfig().set("help.buy.en", "Please delete this row!");
            plugin.getConfig().set("help.buylevel.de", "Please delete this row!");
            plugin.getConfig().set("help.buylevel.en", "Please delete this row!");
            plugin.getConfig().set("help.sell.de", "Please delete this row!");
            plugin.getConfig().set("help.sell.en", "Please delete this row!");
            plugin.getConfig().set("help.selllevel.de", "Please delete this row!");
            plugin.getConfig().set("help.selllevel.en", "Please delete this row!");
            plugin.getConfig().set("help.info.de", "Please delete this row!");
            plugin.getConfig().set("help.info.en", "Please delete this row!");
            plugin.getConfig().set("help.send.de", "Please delete this row!");
            plugin.getConfig().set("help.send.en", "Please delete this row!");
            plugin.getConfig().set("help.infoxp.de", "Please delete this row!");
            plugin.getConfig().set("help.infoxp.en", "Please delete this row!");
            plugin.getConfig().set("help.infolevel.de", "Please delete this row!");
            plugin.getConfig().set("help.infolevel.en", "Please delete this row!");
            plugin.saveConfig();
            plugin.reloadConfig();
        }
    }

    /**
     * creates config and updates it.
     */
    public void loadConfigonStart() {
        try {
            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
            plugin.reloadConfig();
            updatetonew77();
            reload();
            plugin.Logger("Config file found!", "Debug");
            if (Internet) {
                plugin.Logger("internet: true!", "Debug");
            } else {
                plugin.Logger("internet: false!", "Debug");
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.onDisable();
            plugin.Logger("Cannot create config!", "Error");
        }
    }

    /**
     * loadsConfig
     */
    public void reload() {
        loadBooleans();
        loadStrings();
        loadDoubles();
        loadcolors();
    }

    public void loadcolors() {
        if (debug) {
            for (ChatColor ch : ChatColor.values()) {
                plugin.Logger("Color: " + ch.name() + " Char: " + ch.getChar() + " String: " + ch.toString(), "Debug");
            }
        }
        Prefix = ChatColor.getByChar(plugin.getConfig().getString("PrefixColor"));
        Text = ChatColor.getByChar(plugin.getConfig().getString("TextColor"));
    }

    /**
     * Loads doubles from config
     */
    public void loadDoubles() {
        TaskRepeat = plugin.getConfig().getDouble("TaskRepeat");
        DelayTimeTask = plugin.getConfig().getDouble("DelayTimeTask");
    }

    /**
     * Loads player config
     *
     * @param player
     * @param sender
     * @return Returns true if player is editable
     */
    public boolean getPlayerConfig(Player player, Player sender) {
        plugin.Logger("Player is online: " + player.isOnline(), "Debug");
        return true;
    }

    /**
     * loads booleans from config
     */
    public void loadBooleans() {
        debug = plugin.getConfig().getBoolean("debug");
        autodownload = plugin.getConfig().getBoolean("autodownload");
        UsePrefix = plugin.getConfig().getBoolean("UsePrefix");
        autoinstall = plugin.getConfig().getBoolean("autoinstall");
        Internet = plugin.getConfig().getBoolean("internet");
        debugfile = plugin.getConfig().getBoolean("debugfile");
    }

    /**
     * loads strings and language files from config
     */
    public void loadStrings() {
        language = plugin.getConfig().getString("language");
        playernotonline = plugin.getConfig().getString("playernotonline." + language);
        playerwasntonline = plugin.getConfig().getString("playerwasntonline." + language);
        Shoperrornotenoughmoneyconsumer = plugin.getConfig().getString("Shop.error.notenoughmoneyconsumer." + language);
        Shopsuccessbuy = plugin.getConfig().getString("Shop.success.buy." + language);
        Shopsuccesssellerbuy = plugin.getConfig().getString("Shop.success.sellerbuy." + language);
        commanderrorelse = plugin.getConfig().getString("command.error.else." + language);
        commanderrornoint = plugin.getConfig().getString("command.error.noint." + language);
        commanderrornoargs0 = plugin.getConfig().getString("command.error.noargs0." + language);
        commanderrortoomanyarguments = plugin.getConfig().getString("command.error.toomanyarguments." + language);
        commanderrorfewargs = plugin.getConfig().getString("command.error.fewargs." + language);
        commanderrornoplayer = plugin.getConfig().getString("command.error.noplayer." + language);
        permissionserror = plugin.getConfig().getString("permissions.error." + language);
        permissionsnotfound = plugin.getConfig().getString("permissions.notfound." + language);
        iConomyerror = plugin.getConfig().getString("iConomy.error." + language);
        infoPrefix = plugin.getConfig().getString("info.prefix." + language);
    }
}
