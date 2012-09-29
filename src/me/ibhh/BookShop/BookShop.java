/*
 * Copyright 2012 ibhh. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */
package me.ibhh.BookShop;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.logging.Level;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.java.JavaPlugin;

public class BookShop extends JavaPlugin {

    private String ActionBookShop;
    public double getmoney;
    public int SubstractedXP;
    public float Version = 0;
    public float newversion = 0;
    int rounds1 = 0;
    int rounds = 0;
    public Utilities plugman;
    private Help Help;
    public static String PrefixConsole = "[BookShop] ";
    public static String Prefix = "[BookShop] ";
    public ConfigHandler config;
    public BookShopListener ListenerShop;
    private Update upd;
    public BookShop BookShop = this;
    public static boolean updateaviable = false;
    public PermissionsChecker PermissionsHandler;
    public iConomyHandler MoneyHandler;
    public Logger Loggerclass;
    public boolean toggle = true;
    public MetricsHandler metricshandler;
    public PlayerManager playerManager;
    public ReportToHost report;
    private static String SHOP_CONFIG_FILE;
    public YamlConfiguration SHOP_configuration;
    private File configurationFile;
    public HashMap<Player, Boolean> commandexec = new HashMap<Player, Boolean>();
    public HashMap<String, Boolean> DebugMsg = new HashMap<String, Boolean>();
    private HashMap<Player, String> Config = new HashMap<Player, String>();
    private HashMap<Player, String> Set = new HashMap<Player, String>();
    public String[] commands = {
        "help",
        "showdebug",
        "debugfile",
        "internet",
        "version",
        "update",
        "reload",
        "deletedebug",
        "log",
        "toggle",
        "language",
        "report",
        "backupbook",
        "loadbook",
        "giveall",
        "give",
        "setwelcomebook",
        "removewelcomebook"};

    /**
     * Called by Bukkit on stopping the server
     */
    @Override
    public void onDisable() {
        toggle = true;
        long timetemp = System.currentTimeMillis();
        if (config.Internet) {
            UpdateAvailable(Version);
        }
        metricshandler.saveStatsFiles();
        forceUpdate();
        timetemp = System.currentTimeMillis() - timetemp;
        Logger("disabled in " + timetemp + "ms", "");
    }

    /**
     * Called by Bukkit on starting the server
     *
     */
    @Override
    public void onEnable() {
        try {
            long timetemp1 = System.nanoTime();
            Loggerclass = new Logger(this);
            report = new ReportToHost(this);
            try {
                config = new ConfigHandler(this);
                config.loadConfigonStart();
                Logger("Version: " + aktuelleVersion(), "Debug");
            } catch (Exception e1) {
                report.report(332, "Config loading failed", e1.getMessage(), "BookShop", e1);
                Logger("Error on loading config: " + e1.getMessage(), "Error");
                e1.printStackTrace();
                Logger("Version: " + Version + " failed to enable!", "Error");
            }
            try {
                // load the config
                SHOP_CONFIG_FILE = getDataFolder().toString() + File.separator + "Shopconfig.yml";
                configurationFile = new File(SHOP_CONFIG_FILE);
                SHOP_configuration = YamlConfiguration.loadConfiguration(configurationFile);
                SHOP_configuration.addDefault("FirstLineOfEveryShop", "[BookShop]");
                SHOP_configuration.addDefault("AdminShop", "AdminShop");
                SHOP_configuration.addDefault("Newspapers", "Newspapers");
                SHOP_configuration.options().copyDefaults(true);
                SHOP_configuration.save(configurationFile);
            } catch (Exception e1) {
                report.report(332, "Config loading failed", e1.getMessage(), "BookShop", e1);
                Logger("Error on loading config: " + e1.getMessage(), "Error");
                e1.printStackTrace();
                Logger("Version: " + Version + " failed to enable!", "Error");
            }
            try {
                upd = new Update(this);
            } catch (IllegalAccessError e) {
                Logger("Cant access Class \"Update\": " + e.getMessage(), "Error");
                e.printStackTrace();
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                report.report(333, "New Update failed", e.getMessage(), "BookShop", sw.toString());
                setEnabled(false);
            }
            try {
                playerManager = new PlayerManager(this);
                plugman = new Utilities(this);
                Help = new Help(this);
                MoneyHandler = new iConomyHandler(this);
                PermissionsHandler = new PermissionsChecker(this, "BookShop");
                ListenerShop = new BookShopListener(this);
            } catch (Exception e1) {
                Logger("Error on enabling: " + e1.getMessage(), "Error");
                report.report(334, "Error on enabling", e1.getMessage(), "BookShop", e1);
                e1.printStackTrace();
                Logger("Version: " + Version + " failed to enable!", "Error");
                try {
                    plugman.unloadPlugin("BookShop");
                } catch (NoSuchFieldException ex) {
                    java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    if (config.Internet) {
                        try {
                            Logger("Searching update for BookShop!", "Debug");
                            aktuelleVersion();
                            newversion = upd.checkUpdate();
                            if (newversion == -1) {
                                newversion = aktuelleVersion();
                            }
                            Logger("installed BookShop version: " + Version + ", latest version: " + newversion, "Debug");
                            if (newversion > Version) {
                                Logger("New version: " + newversion + " found!", "Warning");
                                Logger("******************************************", "Warning");
                                Logger("*********** Please update!!!! ************", "Warning");
                                Logger("* http://ibhh.de/BookShop.jar *", "Warning");
                                Logger("******************************************", "Warning");
                                BookShop.updateaviable = true;
                                if (getConfig().getBoolean("installondownload")) {
                                    install();
                                }
                            } else {
                                Logger("No update found!", "Debug");
                            }
                        } catch (Exception e) {
                            Logger("Error on doing update check! Message: " + e.getMessage(), "Error");
                            Logger("may the mainserver is down!", "Error");
                            report.report(335, "Checking for update failed", e.getMessage(), "BookShop", e);
                        }
                    }
                }
            }, 400L, 50000L);
            if (config.Internet) {
                try {
                    aktuelleVersion();
                    UpdateAvailable(Version);
                    if (updateaviable) {
                        Logger("New version: " + newversion + " found!", "Warning");
                        Logger("******************************************", "Warning");
                        Logger("*********** Please update!!!! ************", "Warning");
                        Logger("* http://ibhh.de/BookShop.jar *", "Warning");
                        Logger("******************************************", "Warning");
                    }
                } catch (Exception e) {
                    Logger("Error on doing update check! Message: " + e.getMessage(), "Error");
                    Logger("may the mainserver is down!", "Error");
                    report.report(336, "Checking for update failed", e.getMessage(), "BookShop", e);
                }
            }
            metricshandler = new MetricsHandler(this);
            metricshandler.loadStatsFiles();
            this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    metricshandler.saveStatsFiles();
                }
            }, 200L, 50000L);
            metricshandler = new MetricsHandler(this);
            metricshandler.loadStatsFiles();
            this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    toggle = false;
                    metricshandler.onStart();
                }
            }, 20);
            timetemp1 = (System.nanoTime() - timetemp1) / 1000000;
            Logger("Enabled in " + timetemp1 + "ms", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger("Uncatched Exeption! Disabling!", "Error");
            this.setEnabled(false);
            report.report(337, "Uncatched Exeption on loading", ex.getMessage(), "BookShop", ex);
            try {
                metricshandler.Error++;
            } catch (Exception e) {
            }
        }
    }

    /**
     * Delete an download new version of BookShop in the Update folder.
     *
     * @param url
     * @param path
     * @param name
     * @param type
     * @return true if successfully downloaded BookShop
     */
    public boolean autoUpdate(final String path) {
        if (config.Internet) {
            try {
                upd.download(path);
            } catch (Exception e) {
                Logger("Error on doing update! Message: " + e.getMessage(), "Error");
                Logger("may the mainserver is down!", "Error");
                Logger("Uncatched Exeption!", "Error");
                report.report(338, "Error on doing update", e.getMessage(), "BookShop", e);
                try {
                    metricshandler.Error++;
                } catch (Exception e1) {
                }
            }
        }
        return true;
    }

    /**
     * On disable checks if new version aviable and downloads if activatet
     */
    public void forceUpdate() {
        if (config.Internet) {
            try {
                if (updateaviable) {
                    newversion = upd.checkUpdate();
                    Logger("New version: " + newversion + " found!", "Warning");
                    Logger("******************************************", "Warning");
                    Logger("*********** Please update!!!! ************", "Warning");
                    Logger("* http://ibhh.de/BookShop.jar *", "Warning");
                    Logger("******************************************", "Warning");
                    if (getConfig().getBoolean("autodownload") || getConfig().getBoolean("installondownload")) {
                        if (getConfig().getBoolean("autodownload")) {
                            try {
                                String path = "plugins" + File.separator + "BookShop" + File.separator;
                                if (upd.download(path)) {
                                    Logger("Downloaded new Version!", "Warning");
                                } else {
                                    Logger(" Cant download new Version!", "Warning");
                                }
                            } catch (Exception e) {
                                Logger("Error on dowloading new Version!", "Error");
                                e.printStackTrace();
                                Logger("Uncatched Exeption!", "Error");
                                try {
                                    metricshandler.Error++;
                                } catch (Exception e1) {
                                }
                            }
                        }
                        if (getConfig().getBoolean("installondownload")) {
                            try {
                                String path = "plugins" + File.separator;
                                if (upd.download(path)) {
                                    Logger("Downloaded new Version!", "Warning");
                                    Logger("BookShop will be updated on the next restart!", "Warning");
                                } else {
                                    Logger(" Cant download new Version!", "Warning");
                                }
                            } catch (Exception e) {
                                Logger("Error on donwloading new Version!", "Error");
                                e.printStackTrace();
                                Logger("Uncatched Exeption!", "Error");
                                try {
                                    metricshandler.Error++;
                                } catch (Exception e1) {
                                }
                            }
                        }
                    } else {
                        Logger("Please type [BookShop download] to download manual! ", "Warning");
                    }
                }
            } catch (Exception e) {
                Logger("Error on doing update check or update! Message: " + e.getMessage(), "Error");
                Logger("may the mainserver is down!", "Error");
                Logger("Uncatched Exeption!", "Error");
                report.report(339, "Error on doing update check or update", e.getMessage(), "BookShop", e);
                try {
                    metricshandler.Error++;
                } catch (Exception e1) {
                }
            }
        }
    }

    /**
     * Gets version.
     *
     * @return float: Version of the installed plugin.
     */
    public float aktuelleVersion() {
        try {
            Version = Float.parseFloat(getDescription().getVersion());
        } catch (Exception e) {
            Logger("Could not parse version in float", "");
            Logger("Error getting version of BookShop! Message: " + e.getMessage(), "Error");
            report.report(3310, "Error getting version of BookShop", e.getMessage(), "BookShop", e);
            Logger("Uncatched Exeption!", "Error");
            try {
                metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
        return Version;
    }

    /**
     * Compares Version to newVersion
     *
     * @param url from newVersion file + currentVersion
     */
    public void UpdateAvailable(final float currVersion) {
        if (config.Internet) {
            try {
                if (upd.checkUpdate() > currVersion) {
                    BookShop.updateaviable = true;
                }
                if (updateaviable) {
                    updateaviable = true;
                } else {
                    updateaviable = false;
                }
            } catch (Exception e) {
                Logger("Error checking for new version! Message: " + e.getMessage(), "Error");
                report.report(3311, "Error checking for new version", e.getMessage(), "BookShop", e);
                Logger("May the mainserver is down!", "Error");
                Logger("Uncatched Exeption!", "Error");
                try {
                    metricshandler.Error++;
                } catch (Exception e1) {
                }
            }
        }
    }

    /**
     * Return player
     *
     * @param args
     * @param index which field is playername
     * @return player objekt (do player.saveData() after editing players data)
     */
    public Player getmyOfflinePlayer(String[] args, int index) {
        String playername = args[index];
        Logger("Empfaenger: " + playername, "Debug");
        Player player = getServer().getPlayerExact(playername);
        try {
            if (player == null) {
                player = getServer().getPlayer(playername);
            }
            if (player == null) {
                for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
                    OfflinePlayer offp = p;
                    if (offp.getName().toLowerCase().equals(playername.toLowerCase())) {
                        Logger("Player has same name: " + offp.getName(), "Debug");
                        if (offp != null) {
                            if (offp.hasPlayedBefore()) {
                                player = (Player) offp.getPlayer();
                                Logger("Player has Played before: " + offp.getName(), "Debug");
                            }
                            break;
                        }
                    }
                }
            }
            if (player == null) {
                MinecraftServer server = ((CraftServer) this.getServer()).getServer();
                EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), args[index], new ItemInWorldManager(server.getWorldServer(0)));
                player = entity == null ? null : (Player) entity.getBukkitEntity();
                if (player != null) {
                    player.loadData();
                    return player;
                }
            }
            if (player != null) {
                Logger("Empfaengername after getting Player: " + player.getName(), "Debug");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger("Uncatched Exeption!", "Error");
            report.report(3312, "Uncatched Exeption on getting offlineplayer", e.getMessage(), "BookShop", e);
            try {
                metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
        return player;
    }

    /**
     * Return player
     *
     * @param args
     * @param index which field is playername
     * @return player objekt (do player.saveData() after editing players data)
     */
    public Player getmyOfflinePlayer(String playername) {
        Logger("Empfaenger: " + playername, "Debug");
        Player player = getServer().getPlayerExact(playername);
        try {
            if (player == null) {
                player = getServer().getPlayer(playername);
            }
            if (player == null) {
                for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
                    OfflinePlayer offp = p;
                    if (offp.getName().toLowerCase().equals(playername.toLowerCase())) {
                        Logger("Player has same name: " + offp.getName(), "Debug");
                        if (offp != null) {
                            if (offp.hasPlayedBefore()) {
                                player = (Player) offp.getPlayer();
                                Logger("Player has Played before: " + offp.getName(), "Debug");
                            }
                            break;
                        }
                    }
                }
            }
            if (player == null) {
                MinecraftServer server = ((CraftServer) this.getServer()).getServer();
                EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playername, new ItemInWorldManager(server.getWorldServer(0)));
                player = entity == null ? null : (Player) entity.getBukkitEntity();
                if (player != null) {
                    player.loadData();
                    return player;
                }
            }
            if (player != null) {
                Logger("Empfaengername after getting Player: " + player.getName(), "Debug");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger("Uncatched Exeption!", "Error");
            report.report(3312, "Uncatched Exeption on getting offlineplayer", e.getMessage(), "BookShop", e);
            try {
                metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
        return player;
    }

    public void install() {
        try {
            if (config.Internet) {
                try {
                    String path = "plugins" + File.separator;
                    if (upd.download(path)) {
                        Logger("Downloaded new Version!", "Warning");
                        Logger("BookShop will be updated on the next restart!", "Warning");
                    } else {
                        Logger(" Cant download new Version!", "Warning");
                    }
                } catch (Exception e) {
                    Logger("Error on downloading new Version!", "Error");
                    report.report(3313, "Error on downloading new Version", e.getMessage(), "BookShop", e);
                    e.printStackTrace();
                    Logger("Uncatched Exeption!", "Error");
                    try {
                        metricshandler.Error++;
                    } catch (Exception e1) {
                    }
                }
            }
            if (getConfig().getBoolean("installondownload")) {
                Logger("Found Update! Installing now because of 'installondownload = true', please wait!", "Warning");
                playerManager.BroadcastMsg("BookShop.update", "Found Update! Installing now because of 'installondownload = true', please wait!");
            }
            try {
                plugman.unloadPlugin("BookShop");
            } catch (NoSuchFieldException ex) {
                Logger("Error on installing! Please check the log!", "Error");
                playerManager.BroadcastMsg("BookShop.update", "Error on installing! Please check the log!");
                java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger("Error on installing! Please check the log!", "Error");
                playerManager.BroadcastMsg("BookShop.update", "Error on installing! Please check the log!");
                java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                plugman.loadPlugin("BookShop");
            } catch (InvalidPluginException ex) {
                Logger("Error on loading after installing! Please check the log!", "Error");
                playerManager.BroadcastMsg("BookShop.update", "Error on loading after installing! Please check the log!");
                java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidDescriptionException ex) {
                Logger("Error on loading after installing! Please check the log!", "Error");
                playerManager.BroadcastMsg("BookShop.update", "Error on loading after installing! Please check the log!");
                java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger("Installing finished!", "");
            playerManager.BroadcastMsg("BookShop.update", "Installing finished!");
        } catch (Exception w) {
            w.printStackTrace();
            Logger("Uncatched Exeption!", "Error");
            report.report(3314, "Uncatched Exeption on installing", w.getMessage(), "BookShop", w);
            try {
                metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
    }

    /**
     * Called by Bukkit on reloading the server
     *
     */
    public void onReload() {
        onDisable();
        onEnable();
    }

    /**
     * Called by Bukkit if player posts a command
     *
     * @param sender
     * @param cmd
     * @param label
     * @param args
     * @return true if no errors happened else return false to Bukkit, then
     * Bukkit prints /BookShop buy <xp|money>
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!toggle) {
            try {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (cmd.getName().equalsIgnoreCase("BookShop")) {
                        long temptime = 0;
                        temptime = System.nanoTime();
                        switch (args.length) {
                            case 1:
                                ActionBookShop = args[0];
                                if (args[0].equalsIgnoreCase("help")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        Help.help(sender, args);
                                    }
                                } else if (args[0].equalsIgnoreCase("reload")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        PlayerLogger(player, "Please wait: Reloading this plugin!", "Warning");
                                        plugman.unloadPlugin("BookShop");
                                        plugman.loadPlugin("BookShop");
                                        PlayerLogger(player, "Reloaded!", "");
                                    }
                                    return true;
                                } else if (args[0].equalsIgnoreCase("setwelcomebook")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                            BookHandler bookInHand = new BookHandler(player.getItemInHand());
                                            BookHandler loadedBook = BookLoader.load(this, bookInHand.getAuthor(), bookInHand.getTitle());
                                            if (loadedBook != null) {
                                                bookInHand.setSelled(loadedBook.getSelled());
                                                BookLoader.delete(this, loadedBook);
                                            }
                                            BookLoader.save(this, bookInHand);
                                            getConfig().set("GiveBookToNewPlayers", true);
                                            getConfig().set("Book", bookInHand.getAuthor() + " - " + bookInHand.getTitle() + ".txt");
                                            PlayerLogger(player, "Successfully set a welcome book!", "");
                                        } else {
                                            PlayerLogger(player, getConfig().getString("command.error.takeBookInHand." + config.language), "Error");
                                        }
                                    }
                                    return true;
                                } else if (args[0].equalsIgnoreCase("removewelcomebook")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        getConfig().set("GiveBookToNewPlayers", false);
                                        PlayerLogger(player, "Successfully unset a welcome book!", "");
                                    }
                                    return true;
                                } else if (args[0].equalsIgnoreCase("backupbook")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                            BookHandler bookInHand = new BookHandler(player.getItemInHand());
                                            BookHandler loadedBook = BookLoader.load(this, bookInHand.getAuthor(), bookInHand.getTitle());
                                            bookInHand.setSelled(loadedBook.getSelled());
                                            BookLoader.delete(this, loadedBook);
                                            BookLoader.save(this, bookInHand);
                                            PlayerLogger(player, "Saved!", "");
                                        } else {
                                            PlayerLogger(player, getConfig().getString("command.error.takeBookInHand." + config.language), "Error");
                                        }
                                    }
                                    return true;
                                } else if (args[0].equalsIgnoreCase("giveall")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        final Player player_final = player;
                                        this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                            @Override
                                            public void run() {
                                                if (player_final.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                    ItemStack item = player_final.getItemInHand();
                                                    PlayerLogger(player_final, "Giving book to every player!", "");
                                                    PlayerLogger(player_final, "Please wait ....", "");
                                                    for (OfflinePlayer off : getServer().getOfflinePlayers()) {
                                                        Player empfaenger = getmyOfflinePlayer(off.getName());
                                                        if (empfaenger.getInventory().firstEmpty() != -1) {
                                                            empfaenger.getInventory().addItem(item);
                                                            PlayerLogger(empfaenger, "You were given a book by an admin!", "");
                                                        } else {
                                                            PlayerLogger(player_final, "Inventory of " + off.getName() + " is full! Can not give him this book!", "Error");
                                                        }
                                                    }
                                                    PlayerLogger(player_final, "Done!", "");
                                                } else {
                                                    PlayerLogger(player_final, "Please take the book in the hand which you want to give to every player!", "Error");
                                                }
                                            }
                                        }, 1);
                                    }
                                    return true;
                                } else if (args[0].equalsIgnoreCase("showdebug")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (DebugMsg.containsKey(player.getName())) {
                                            DebugMsg.remove(player.getName());
                                        } else {
                                            DebugMsg.put(player.getName(), true);
                                        }
                                        return true;
                                    }
                                } else if (args[0].equalsIgnoreCase("debugfile")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        getConfig().set("debugfile", !getConfig().getBoolean("debugfile"));
                                        PlayerLogger(player, "debugfile: " + getConfig().getBoolean("debugfile"), "");
                                        saveConfig();
                                        reloadConfig();
                                        config.reload();
                                        return true;
                                    }
                                } else if (args[0].equalsIgnoreCase("internet")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        getConfig().set("internet", !getConfig().getBoolean("internet"));
                                        PlayerLogger(player, "internet: " + getConfig().getBoolean("internet"), "");
                                        saveConfig();
                                        reloadConfig();
                                        config.reload();
                                        return true;
                                    }
                                } else if (ActionBookShop.equalsIgnoreCase("version")) {
                                    PlayerLogger(player, "Version: " + getDescription().getVersion(), "");
                                    temptime = (System.nanoTime() - temptime) / 1000000;
                                    Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
                                    return true;
                                } else if (ActionBookShop.equalsIgnoreCase("update")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        install();
                                        return true;
                                    }
                                } else if (ActionBookShop.equalsIgnoreCase("deletedebug")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        File file = new File("plugins" + File.separator + "BookShop" + File.separator + "debug.txt");
                                        if (file.exists()) {
                                            if (file.delete()) {
                                                PlayerLogger(player, "file deleted!", "Warning");
                                                try {
                                                    file.createNewFile();


                                                } catch (IOException ex) {
                                                    java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            } else {
                                                PlayerLogger(player, "Error on deleting file!", "Error");
                                            }
                                        }
                                        return true;
                                    }
                                } else if (ActionBookShop.equalsIgnoreCase("log")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        File file = new File("plugins" + File.separator + "BookShop" + File.separator + "debug.txt");
                                        if (file.exists()) {
                                            PlayerLogger(player, "debug.txt is " + file.length() + " Byte big!", "Warning");
                                            PlayerLogger(player, "Type /BookShop deletedebug to delete the debug.txt!", "Warning");
                                        }
                                        return true;
                                    }
                                } else if (ActionBookShop.equalsIgnoreCase("toggle")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (toggle) {
                                            toggle = false;
                                        } else {
                                            toggle = true;
                                        }
                                        PlayerLogger(player, "BookShop offline: " + toggle, "");
                                        return true;
                                    }
                                } else if (args[0].equalsIgnoreCase("configconfirm")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (Config.containsKey(player)) {
                                            String temp = getConfig().getString(Config.get(player));
                                            Logger("Temp: " + temp, "Debug");
                                            boolean isboolean = false;
                                            if (temp.equalsIgnoreCase("true") || temp.equalsIgnoreCase("false")) {
                                                isboolean = true;
                                                Logger("Config is boolean!", "Debug");
                                            }
                                            boolean istTrue = false;
                                            if (isboolean) {
                                                if (Set.get(player).equalsIgnoreCase("true")) {
                                                    istTrue = true;
                                                    Logger("Config is true!", "Debug");
                                                }
                                            }
                                            if (!isboolean) {
                                                getConfig().set(Config.get(player), Set.get(player));
                                            } else {
                                                getConfig().set(Config.get(player), istTrue);
                                                Logger("Set boolean", "Debug");
                                            }
                                            saveConfig();
                                            reloadConfig();
                                            config.reload();
                                            PlayerLogger(player, "You set  " + Config.get(player) + " from " + temp + " to " + getConfig().getString(Config.get(player)) + " !", "Warning");
                                            Set.remove(player);
                                            Config.remove(player);
                                        } else {
                                            PlayerLogger(player, "Please enter a command first!", "Error");
                                        }
                                        return true;
                                    }
                                } else if (args[0].equalsIgnoreCase("configcancel")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (Config.containsKey(player)) {
                                            PlayerLogger(player, "Command canceled!", "Warning");
                                            Set.remove(player);
                                            Config.remove(player);
                                        } else {
                                            PlayerLogger(player, "Please enter a command first!", "Error");
                                        }
                                        return true;
                                    }
                                } else {
                                    Help.help(sender, args);
                                }
                                break;
                            case 2:
                                ActionBookShop = args[0];
                                if (args[0].equalsIgnoreCase("language")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        getConfig().set("language", args[1]);
                                        PlayerLogger(player, "language set to: " + args[1], "");
                                        saveConfig();
                                        Logger("Config saved!", "Debug");
                                        reloadConfig();
                                        Logger("Config reloaded!", "Debug");
                                        Logger("debug reloaded!", "Debug");
                                        config.reload();
                                        Logger("Config reloaded!", "Debug");
                                        return true;
                                    }
                                } else if (args[0].equalsIgnoreCase("give")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                            ItemStack item = player.getItemInHand();
                                            PlayerLogger(player, "Giving book to " + args[1] + "!", "");
                                            Player empfaenger = getmyOfflinePlayer(args[1]);
                                            if (empfaenger.hasPlayedBefore()) {
                                                if (empfaenger.getInventory().firstEmpty() != -1) {
                                                    empfaenger.getInventory().addItem(item);
                                                    PlayerLogger(empfaenger, "You were given a book by an admin!", "");
                                                } else {
                                                    PlayerLogger(player, "Inventory of " + args[1] + " is full! Can not give him this book!", "Error");
                                                }
                                                PlayerLogger(player, "Done!", "");
                                            } else {
                                                PlayerLogger(player, "Player wanst online before!", "Error");
                                            }
                                        } else {
                                            PlayerLogger(player, "Please take the book in the hand which you want to give to every player!", "Error");
                                        }
                                    }
                                    return true;
                                } else if (ActionBookShop.equalsIgnoreCase("help")) {
                                    if (PermissionsHandler.checkpermissions(player, "BookShop.help")) {
                                        if (!Tools.isInteger(args[1])) {
                                            Help.help(player, args);
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
                                            return true;
                                        }
                                        PlayerLogger(player, config.commanderrornoint, "Error");
                                        return false;
                                    }
                                } else if (ActionBookShop.equalsIgnoreCase("report")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (!Tools.isInteger(args[1])) {
                                            PlayerLogger(player, report.report(331, "Reported issue", args[1], "BookShop", "No stacktrace because of command"), "");
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
                                            return true;
                                        }
                                        PlayerLogger(player, config.commanderrornoint, "Error");
                                        return false;
                                    }
                                } else {
                                    Help.help(sender, args);
                                }
                                break;
                            case 3:
                                ActionBookShop = args[0];
                                if (args[0].equalsIgnoreCase("config")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (!Config.containsKey(player)) {
                                            Config.put(player, args[1]);
                                            String Configtext = args[2];
                                            for (int i = 3; i < args.length; i++) {
                                                Configtext = Configtext.concat(args[i]);
                                            }
                                            Set.put(player, Configtext);
                                            PlayerLogger(player, "Do you want to edit " + args[1] + " from " + getConfig().getString(args[1]) + " to " + Configtext + " ?", "Warning");
                                            PlayerLogger(player, String.format("Please confirm within %1$d sec!", getConfig().getInt("Cooldownoftp")), "Warning");
                                            PlayerLogger(player, "Please confirm with \"/BookShop configconfirm\" !", "Warning");
                                            PlayerLogger(player, "Please cancel with \"/BookShop configcancel\" !", "Warning");
                                            final Player player1 = player;
                                            getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (Config.containsKey(player1)) {
                                                        Config.remove(player1);
                                                        Set.remove(player1);
                                                        PlayerLogger(player1, String.format("You havent confirmed within %1$d sec!", getConfig().getInt("Cooldownoftp")), "Warning");
                                                    }
                                                }
                                            }, getConfig().getInt("Cooldownoftp") * 20);
                                            return true;
                                        } else {
                                            PlayerLogger(player, "Please confirm or cancel your last command first!", "Error");
                                            return true;
                                        }
                                    }
                                } else if (ActionBookShop.equalsIgnoreCase("report")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        if (!Tools.isInteger(args[1])) {
                                            String text = "";
                                            for (int i = 1; i < args.length; i++) {
                                                text = text.concat(args[i]);
                                            }
                                            PlayerLogger(player, report.report(331, "Reported issue", args[1], "BookShop", "No stacktrace because of command"), "");
                                            temptime = (System.nanoTime() - temptime) / 1000000;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
                                            return true;
                                        }
                                        PlayerLogger(player, config.commanderrornoint, "Error");
                                        return false;
                                    }
                                } else if (args[0].equalsIgnoreCase("loadbook")) {
                                    if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                        int slot = player.getInventory().firstEmpty();
                                        if (slot != -1) {
                                            String author = args[1];
                                            String title = "";
                                            for (int i = 2; i < args.length; i++) {
                                                title = title.concat(args[i]);
                                            }
                                            String filename = author + " - " + title + ".txt";
                                            BookHandler book = BookLoader.load(this, filename);
                                            player.getInventory().addItem(book.toItemStack(1));
                                            Logger("Book loaded!", "Debug");
                                        } else {
                                            PlayerLogger(player, getConfig().getString("Shop.error.inventoryfull." + config.language), "Error");
                                        }
                                        return true;
                                    }
                                } else {
                                    Help.help(sender, args);
                                }
                                break;
                            default:
                                if (args.length > 3) {
                                    ActionBookShop = args[0];
                                    if (args[0].equalsIgnoreCase("config")) {
                                        if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                            if (!Config.containsKey(player)) {
                                                Config.put(player, args[1]);
                                                String Configtext = args[2];
                                                for (int i = 3; i < args.length; i++) {
                                                    Configtext = Configtext.concat(args[i] + " ");
                                                }
                                                Set.put(player, Configtext);
                                                PlayerLogger(player, "Do you want to edit " + args[1] + " from " + getConfig().getString(args[1]) + " to " + Configtext + " ?", "Warning");
                                                PlayerLogger(player, String.format("Please confirm within %1$d sec!", getConfig().getInt("Cooldownoftp")), "Warning");
                                                PlayerLogger(player, "Please confirm with \"/BookShop configconfirm\" !", "Warning");
                                                PlayerLogger(player, "Please cancel with \"/BookShop configcancel\" !", "Warning");
                                                final Player player1 = player;
                                                getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (Config.containsKey(player1)) {
                                                            Config.remove(player1);
                                                            Set.remove(player1);
                                                            PlayerLogger(player1, String.format("You havent confirmed within %1$d sec!", getConfig().getInt("Cooldownoftp")), "Warning");
                                                        }
                                                    }
                                                }, getConfig().getInt("Cooldownoftp") * 20);
                                                return true;
                                            } else {
                                                PlayerLogger(player, "Please confirm or cancel your last command first!", "Error");
                                                return true;
                                            }
                                        }
                                    } else if (args[0].equalsIgnoreCase("loadbook")) {
                                        if (PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + ActionBookShop.toLowerCase() + ".permission"))) {
                                            int slot = player.getInventory().firstEmpty();
                                            if (slot != -1) {
                                                String author = args[1];
                                                String title = "";
                                                for (int i = 2; i < args.length; i++) {
                                                    title = title.concat(" " + args[i]);
                                                }
                                                String filename = author + " -" + title + ".txt";
                                                try {
                                                    BookHandler book = BookLoader.load(this, filename);
                                                    player.getInventory().addItem(book.toItemStack(1));
                                                    PlayerLogger(player, "Book loaded!", "");
                                                } catch (Exception e) {
                                                    PlayerLogger(player, "Book not found, sorry!", "Error");
                                                }
                                            } else {
                                                PlayerLogger(player, getConfig().getString("Shop.error.inventoryfull." + config.language), "Error");
                                            }
                                            return true;
                                        }
                                    }
                                }
                                Help.help(player, args);
                                return false;
                        }
                    }
                } else if (cmd.getName().equalsIgnoreCase("BookShop")) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("download")) {
                            String path = "plugins" + File.separator;
                            upd.download(path);
                            Logger("Downloaded new Version!", "Warning");
                            Logger("BookShop will be updated on the next restart!", "Warning");
                            return true;
                        } else if (args[0].equalsIgnoreCase("reload")) {
                            Logger("Please wait: Reloading this plugin!", "Warning");
                            plugman.unloadPlugin("BookShop");
                            plugman.loadPlugin("BookShop");
                            Logger("Reloaded!", "");
                            return true;
                        } else if (args[0].equalsIgnoreCase("debug")) {
                            getConfig().set("debug", !getConfig().getBoolean("debug"));
                            Logger("debug set to: " + getConfig().getBoolean("debug"), "");
                            saveConfig();
                            Logger("Config saved!", "Debug");
                            reloadConfig();
                            Logger("Config reloaded!", "Debug");
                            Logger("debug reloaded!", "Debug");
                            config.reload();
                            Logger("Config reloaded!", "Debug");
                            return true;
                        } else if (args[0].equalsIgnoreCase("debugfile")) {
                            getConfig().set("debugfile", !getConfig().getBoolean("debugfile"));
                            Logger("debugfile set to: " + getConfig().getBoolean("debugfile"), "");
                            saveConfig();
                            Logger("Config saved!", "Debug");
                            reloadConfig();
                            Logger("Config reloaded!", "Debug");
                            Logger("debugfile reloaded!", "Debug");
                            config.reload();
                            Logger("Config reloaded!", "Debug");
                            return true;
                        } else if (args[0].equalsIgnoreCase("toggle")) {
                            if (toggle) {
                                toggle = false;
                            } else {
                                toggle = true;
                            }
                            Logger("BookShop offline: " + toggle, "");
                            return true;
                        } else if (args[0].equalsIgnoreCase("autodownload")) {
                            getConfig().set("autodownload", !getConfig().getBoolean("autodownload"));
                            Logger("autodownload set to: " + getConfig().getBoolean("autodownload"), "");
                            saveConfig();
                            Logger("Config saved!", "Debug");
                            reloadConfig();
                            Logger("Config reloaded!", "Debug");
                            Logger("debug reloaded!", "Debug");
                            config.reload();
                            Logger("Config reloaded!", "Debug");
                            return true;
                        } else if (args.length == 2) {
                            if (args[0].equalsIgnoreCase("language")) {
                                getConfig().set("language", args[1]);
                                Logger("language set to: " + args[1], "");
                                saveConfig();
                                Logger("Config saved!", "Debug");
                                reloadConfig();
                                Logger("Config reloaded!", "Debug");
                                Logger("debug reloaded!", "Debug");
                                config.reload();
                                Logger("Config reloaded!", "Debug");
                                return true;
                            }
                        }
                    } else if (args.length == 2) {
                        if (!Tools.isInteger(args[1])) {
                            Logger(report.report(331, "Reported issue", args[1], "BookShop", "No stacktrace because of command"), "");
                            return true;
                        }
                        return false;
                    }
                    return false;
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("toggle")) {
                        if (sender instanceof Player) {
                            Player p = (Player) sender;
                            if (PermissionsHandler.checkpermissions(p, "BookShop.admin")) {
                                if (toggle) {
                                    toggle = false;
                                } else {
                                    toggle = true;
                                }
                                PlayerLogger(p, "BookShop offline: " + toggle, "");
                                return true;
                            }
                        } else {
                            if (toggle) {
                                toggle = false;
                            } else {
                                toggle = true;
                            }
                            Logger("BookShop offline: " + toggle, "");
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
            } catch (Exception e1) {
                sender.sendMessage("Unknown Error: " + e1.getMessage());
                System.out.println("[BookShop] Unknown Error: " + e1.getMessage());
                e1.printStackTrace();
                report.report(3316, "A Command dont work", e1.getMessage(), "BookShop", e1);
                Logger("Uncatched Exeption!", "Error");
                try {
                    metricshandler.Error++;
                } catch (Exception e11) {
                }
            }
        }
        return false;
    }

    /**
     * Intern logger to send player messages and log it into file
     *
     * @param msg
     * @param TYPE
     */
    public void Logger(String msg, String TYPE) {
        try {
            if (TYPE.equalsIgnoreCase("Warning") || TYPE.equalsIgnoreCase("Error")) {
                System.err.println(PrefixConsole + TYPE + ": " + msg);
                if (config.debugfile) {
                    Loggerclass.log("Error: " + msg);
                }
                if (playerManager != null) {
                    playerManager.BroadcastconsoleMsg("BookShop.consolemsg", " Warning: " + msg);
                }
            } else if (TYPE.equalsIgnoreCase("Debug")) {
                if (config.debug) {
                    System.out.println(PrefixConsole + "Debug: " + msg);
                }
                if (config.debugfile) {
                    Loggerclass.log("Debug: " + msg);
                }
                if (playerManager != null) {
                    playerManager.BroadcastconsoleMsg("BookShop.consolemsg", " Debug: " + msg);
                }
            } else {
                if (playerManager != null) {
                    playerManager.BroadcastconsoleMsg("BookShop.consolemsg", msg);
                }
                System.out.println(PrefixConsole + msg);
                if (config.debugfile) {
                    Loggerclass.log(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[BookShop] Error: Uncatch Exeption!");
            report.report(3317, "Logger doesnt work", e.getMessage(), "BookShop", e);
            try {
                metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
    }

    /**
     * Intern logger to send player messages and log it into file
     *
     * @param p
     * @param msg
     * @param TYPE
     */
    public void PlayerLogger(Player p, String msg, String TYPE) {
        try {
            if (TYPE.equalsIgnoreCase("Error")) {
                if (config.UsePrefix) {
                    p.sendMessage(config.Prefix + Prefix + ChatColor.RED + "Error: " + config.Text + msg);
                    if (config.debugfile) {
                        Loggerclass.log("Player: " + p.getName() + " Error: " + msg);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Error: " + config.Text + msg);
                    if (config.debugfile) {
                        Loggerclass.log("Player: " + p.getName() + " Error: " + msg);
                    }
                }
                if (playerManager != null) {
                    playerManager.BroadcastconsoleMsg("BookShop.gamemsg", "Player: " + p.getName() + " Error: " + msg);
                }
            } else {
                if (config.UsePrefix) {
                    p.sendMessage(config.Prefix + Prefix + config.Text + msg);
                    if (config.debugfile) {
                        Loggerclass.log("Player: " + p.getName() + " Msg: " + msg);
                    }
                } else {
                    p.sendMessage(config.Text + msg);
                    if (config.debugfile) {
                        Loggerclass.log("Player: " + p.getName() + " Msg: " + msg);
                    }
                }
                if (playerManager != null) {
                    playerManager.BroadcastconsoleMsg("BookShop.gamemsg", "Player: " + p.getName() + " " + msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[BookShop] Error: Uncatch Exeption!");
            report.report(3317, "PlayerLogger doesnt work", e.getMessage(), "BookShop", e);
            try {
                metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
    }
}
