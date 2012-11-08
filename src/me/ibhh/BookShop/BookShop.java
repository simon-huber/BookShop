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
    public float Version = 0.0F;
    public float newversion = 0.0F;
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
    public HashMap<Player, Boolean> commandexec = new HashMap();
    public HashMap<String, Boolean> DebugMsg = new HashMap();
    private HashMap<Player, String> Config = new HashMap();
    private HashMap<Player, String> Set = new HashMap();
    public String[] commands = {"help", "showdebug", "debugfile", "internet", "version", "update", "reload", "deletedebug", "log", "toggle", "language", "report", "backupbook", "loadbook", "giveall", "give", "setwelcomebook", "removewelcomebook"};

    @Override
    public void onDisable() {
        this.toggle = true;
        long timetemp = System.currentTimeMillis();
        if (this.config.Internet) {
            UpdateAvailable(this.Version);
        }
        this.metricshandler.saveStatsFiles();
        forceUpdate();
        timetemp = System.currentTimeMillis() - timetemp;
        Logger("disabled in " + timetemp + "ms", "");
    }

    private boolean changeLanguage(String language) {
        if (getConfig().getString("permissions.error." + getConfig().getString("language")) == null) {
            Logger("Language not valid!", "Error");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onEnable() {
        try {
            long timetemp1 = System.nanoTime();
            this.Loggerclass = new Logger(this);
            Exception ex1 = null;
            try {
                this.config = new ConfigHandler(this);
                this.config.loadConfigonStart();
                if (!changeLanguage(getConfig().getString("language"))) {
                    getConfig().set("language", "en");
                    saveConfig();
                    reloadConfig();
                    Logger("Language changed to en because your selection wasnt found!", "Error");
                    if (!changeLanguage(getConfig().getString("language"))) {
                        getConfig().set("language", "de");
                        saveConfig();
                        reloadConfig();
                        Logger("Language changed to de because your selection wasnt found!", "Error");
                    }
                }
                Logger("Version: " + aktuelleVersion(), "Debug");
            } catch (Exception e1) {
                ex1 = e1;
                Logger("Error on loading config: " + e1.getMessage(), "Error");
                e1.printStackTrace();
                Logger("Version: " + this.Version + " failed to enable!", "Error");
            }

            this.report = new ReportToHost(this);
            if (ex1 != null) {
                this.report.report(332, "Config loading failed", ex1.getMessage(), "BookShop", ex1);
            }
            try {
                SHOP_CONFIG_FILE = getDataFolder().toString() + File.separator + "Shopconfig.yml";
                this.configurationFile = new File(SHOP_CONFIG_FILE);
                this.SHOP_configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
                this.SHOP_configuration.addDefault("FirstLineOfEveryShop", "[BookShop]");
                this.SHOP_configuration.addDefault("AdminShop", "AdminShop");
                this.SHOP_configuration.addDefault("Newspapers", "Newspapers");
                this.SHOP_configuration.options().copyDefaults(true);
                this.SHOP_configuration.save(this.configurationFile);
            } catch (Exception e1) {
                this.report.report(332, "Config loading failed", e1.getMessage(), "BookShop", e1);
                Logger("Error on loading config: " + e1.getMessage(), "Error");
                Logger("Using defaults!", "Error");
                e1.printStackTrace();
                Logger("Version: " + this.Version + " failed to enable!", "Error");
            }
            try {
                this.upd = new Update(this);
            } catch (IllegalAccessError e) {
                Logger("Cant access Class \"Update\": " + e.getMessage(), "Error");
                e.printStackTrace();
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                this.report.report(333, "New Update failed", e.getMessage(), "BookShop", sw.toString());
                setEnabled(false);
            }
            try {
                this.playerManager = new PlayerManager(this);
                this.plugman = new Utilities(this);
                this.Help = new Help(this);
                this.MoneyHandler = new iConomyHandler(this);
                this.PermissionsHandler = new PermissionsChecker(this, "BookShop");
                this.ListenerShop = new BookShopListener(this);
            } catch (Exception e1) {
                Logger("Error on enabling: " + e1.getMessage(), "Error");
                this.report.report(334, "Error on enabling", e1.getMessage(), "BookShop", e1);
                e1.printStackTrace();
                Logger("Version: " + this.Version + " failed to enable!", "Error");
                try {
                    this.plugman.unloadPlugin("BookShop");
                } catch (NoSuchFieldException ex) {
                    java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    if (BookShop.this.config.Internet) {
                        try {
                            BookShop.this.Logger("Searching update for BookShop!", "Debug");
                            BookShop.this.aktuelleVersion();
                            BookShop.this.newversion = BookShop.this.upd.checkUpdate().floatValue();
                            if (BookShop.this.newversion == -1.0F) {
                                BookShop.this.newversion = BookShop.this.aktuelleVersion();
                            }
                            BookShop.this.Logger("installed BookShop version: " + BookShop.this.Version + ", latest version: " + BookShop.this.newversion, "Debug");
                            if (BookShop.this.newversion > BookShop.this.Version) {
                                BookShop.this.Logger("New version: " + BookShop.this.newversion + " found!", "Warning");
                                BookShop.this.Logger("******************************************", "Warning");
                                BookShop.this.Logger("*********** Please update!!!! ************", "Warning");
                                BookShop.this.Logger("* http://ibhh.de/BookShop.jar *", "Warning");
                                BookShop.this.Logger("******************************************", "Warning");
                                BookShop.updateaviable = true;
                                if (BookShop.this.getConfig().getBoolean("installondownload")) {
                                    BookShop.this.install();
                                }
                            } else {
                                BookShop.this.Logger("No update found!", "Debug");
                            }
                        } catch (Exception e) {
                            BookShop.this.Logger("Error on doing update check! Message: " + e.getMessage(), "Error");
                            BookShop.this.Logger("may the mainserver is down!", "Error");
                            BookShop.this.report.report(335, "Checking for update failed", e.getMessage(), "BookShop", e);
                        }
                    }
                }
            }, 400L, 50000L);

            if (this.config.Internet) {
                try {
                    aktuelleVersion();
                    UpdateAvailable(this.Version);
                    if (updateaviable) {
                        Logger("New version: " + this.newversion + " found!", "Warning");
                        Logger("******************************************", "Warning");
                        Logger("*********** Please update!!!! ************", "Warning");
                        Logger("* http://ibhh.de/BookShop.jar *", "Warning");
                        Logger("******************************************", "Warning");
                    }
                } catch (Exception e) {
                    Logger("Error on doing update check! Message: " + e.getMessage(), "Error");
                    Logger("may the mainserver is down!", "Error");
                    this.report.report(336, "Checking for update failed", e.getMessage(), "BookShop", e);
                }
            }
            this.metricshandler = new MetricsHandler(this);
            this.metricshandler.loadStatsFiles();
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
                public void run() {
                    BookShop.this.metricshandler.saveStatsFiles();
                }
            }, 200L, 50000L);

            this.metricshandler = new MetricsHandler(this);
            this.metricshandler.loadStatsFiles();
            getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                public void run() {
                    BookShop.this.toggle = false;
                    BookShop.this.metricshandler.onStart();
                }
            }, 20L);

            timetemp1 = (System.nanoTime() - timetemp1) / 1000000L;
            Logger("Enabled in " + timetemp1 + "ms", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger("Uncatched Exeption! Disabling!", "Error");
            setEnabled(false);
            this.report.report(337, "Uncatched Exeption on loading", ex.getMessage(), "BookShop", ex);
            try {
                MetricsHandler.Error += 1;
            } catch (Exception e) {
            }
        }
    }

    public boolean autoUpdate(String path) {
        if (this.config.Internet) {
            try {
                this.upd.download(path);
            } catch (Exception e) {
                Logger("Error on doing update! Message: " + e.getMessage(), "Error");
                Logger("may the mainserver is down!", "Error");
                Logger("Uncatched Exeption!", "Error");
                this.report.report(338, "Error on doing update", e.getMessage(), "BookShop", e);
                try {
                    MetricsHandler.Error += 1;
                } catch (Exception e1) {
                }
            }
        }
        return true;
    }

    public void forceUpdate() {
        if (this.config.Internet) {
            try {
                if (updateaviable) {
                    this.newversion = this.upd.checkUpdate().floatValue();
                    Logger("New version: " + this.newversion + " found!", "Warning");
                    Logger("******************************************", "Warning");
                    Logger("*********** Please update!!!! ************", "Warning");
                    Logger("* http://ibhh.de/BookShop.jar *", "Warning");
                    Logger("******************************************", "Warning");
                    if ((getConfig().getBoolean("autodownload")) || (getConfig().getBoolean("installondownload"))) {
                        if (getConfig().getBoolean("autodownload")) {
                            try {
                                String path = "plugins" + File.separator + "BookShop" + File.separator;
                                if (this.upd.download(path)) {
                                    Logger("Downloaded new Version!", "Warning");
                                } else {
                                    Logger(" Cant download new Version!", "Warning");
                                }
                            } catch (Exception e) {
                                Logger("Error on dowloading new Version!", "Error");
                                e.printStackTrace();
                                Logger("Uncatched Exeption!", "Error");
                                try {
                                    MetricsHandler.Error += 1;
                                } catch (Exception e1) {
                                }
                            }
                        }
                        if (getConfig().getBoolean("installondownload")) {
                            try {
                                String path = "plugins" + File.separator;
                                if (this.upd.download(path)) {
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
                                    MetricsHandler.Error += 1;
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
                this.report.report(339, "Error on doing update check or update", e.getMessage(), "BookShop", e);
                try {
                    MetricsHandler.Error += 1;
                } catch (Exception e1) {
                }
            }
        }
    }

    public float aktuelleVersion() {
        try {
            this.Version = Float.parseFloat(getDescription().getVersion());
        } catch (Exception e) {
            Logger("Could not parse version in float", "");
            Logger("Error getting version of BookShop! Message: " + e.getMessage(), "Error");
            this.report.report(3310, "Error getting version of BookShop", e.getMessage(), "BookShop", e);
            Logger("Uncatched Exeption!", "Error");
            try {
                MetricsHandler.Error += 1;
            } catch (Exception e1) {
            }
        }
        return this.Version;
    }

    public void UpdateAvailable(float currVersion) {
        if (this.config.Internet) {
            try {
                if (upd == null) {
                    updateaviable = false;
                }
                if (this.upd.checkUpdate() > currVersion) {
                    updateaviable = true;
                }
                if (updateaviable) {
                    updateaviable = true;
                } else {
                    updateaviable = false;
                }
            } catch (Exception e) {
                Logger("Error checking for new version! Message: " + e.getMessage(), "Error");
                this.report.report(3311, "Error checking for new version", e.getMessage(), "BookShop", e);
                Logger("May the mainserver is down!", "Error");
                Logger("Uncatched Exeption!", "Error");
                try {
                    MetricsHandler.Error += 1;
                } catch (Exception e1) {
                }
            }
        }
    }

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
            if (this.config.Internet) {
                try {
                    String path = "plugins" + File.separator;
                    if (this.upd.download(path)) {
                        Logger("Downloaded new Version!", "Warning");
                        Logger("BookShop will be updated on the next restart!", "Warning");
                    } else {
                        Logger(" Cant download new Version!", "Warning");
                    }
                } catch (Exception e) {
                    Logger("Error on downloading new Version!", "Error");
                    this.report.report(3313, "Error on downloading new Version", e.getMessage(), "BookShop", e);
                    e.printStackTrace();
                    Logger("Uncatched Exeption!", "Error");
                    try {
                        MetricsHandler.Error += 1;
                    } catch (Exception e1) {
                    }
                }
            }
            if (getConfig().getBoolean("installondownload")) {
                Logger("Found Update! Installing now because of 'installondownload = true', please wait!", "Warning");
                this.playerManager.BroadcastMsg("BookShop.update", "Found Update! Installing now because of 'installondownload = true', please wait!");
            }
            try {
                this.plugman.unloadPlugin("BookShop");
            } catch (NoSuchFieldException ex) {
                Logger("Error on installing! Please check the log!", "Error");
                this.playerManager.BroadcastMsg("BookShop.update", "Error on installing! Please check the log!");
                java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger("Error on installing! Please check the log!", "Error");
                this.playerManager.BroadcastMsg("BookShop.update", "Error on installing! Please check the log!");
                java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                this.plugman.loadPlugin("BookShop");
            } catch (InvalidPluginException ex) {
                Logger("Error on loading after installing! Please check the log!", "Error");
                this.playerManager.BroadcastMsg("BookShop.update", "Error on loading after installing! Please check the log!");
                java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidDescriptionException ex) {
                Logger("Error on loading after installing! Please check the log!", "Error");
                this.playerManager.BroadcastMsg("BookShop.update", "Error on loading after installing! Please check the log!");
                java.util.logging.Logger.getLogger(BookShop.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger("Installing finished!", "");
            this.playerManager.BroadcastMsg("BookShop.update", "Installing finished!");
        } catch (Exception w) {
            w.printStackTrace();
            Logger("Uncatched Exeption!", "Error");
            this.report.report(3314, "Uncatched Exeption on installing", w.getMessage(), "BookShop", w);
            try {
                MetricsHandler.Error += 1;
            } catch (Exception e1) {
            }
        }
    }

    public void onReload() {
        onDisable();
        onEnable();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (!this.toggle) {
                if ((sender instanceof Player)) {
                    Player player = (Player) sender;
                    if (cmd.getName().equalsIgnoreCase("BookShop")) {
                        long temptime = 0L;
                        temptime = System.nanoTime();
                        switch (args.length) {
                            case 1:
                                this.ActionBookShop = args[0];
                                if (args[0].equalsIgnoreCase("help")) {
                                    if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                        break;
                                    }
                                    this.Help.help(sender, args);
                                } else {
                                    if (args[0].equalsIgnoreCase("reload")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            PlayerLogger(player, "Please wait: Reloading this plugin!", "Warning");
                                            this.plugman.unloadPlugin("BookShop");
                                            this.plugman.loadPlugin("BookShop");
                                            PlayerLogger(player, "Reloaded!", "");
                                        }
                                        return true;
                                    }
                                    if (args[0].equalsIgnoreCase("setwelcomebook")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                BookHandler bookInHand = new BookHandler(player.getItemInHand());
                                                BookHandler loadedBook = BookLoader.load(this, bookInHand.getAuthor(), bookInHand.getTitle());
                                                if (loadedBook != null) {
                                                    bookInHand.setSelled(loadedBook.getSelled());
                                                    BookLoader.delete(this, loadedBook);
                                                }
                                                if (bookInHand != null) {
                                                    BookLoader.save(this, bookInHand);
                                                    getConfig().set("GiveBookToNewPlayers", Boolean.valueOf(true));
                                                    getConfig().set("Book", bookInHand.getAuthor() + " - " + bookInHand.getTitle() + ".txt");
                                                    saveConfig();
                                                    PlayerLogger(player, "Successfully set a welcome book!", "");
                                                } else {
                                                    PlayerLogger(player, "unknown error", "Error");
                                                }
                                            } else {
                                                PlayerLogger(player, getConfig().getString("command.error.takeBookInHand." + this.config.language), "Error");
                                            }
                                        }
                                        return true;
                                    }
                                    if (args[0].equalsIgnoreCase("removewelcomebook")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            getConfig().set("GiveBookToNewPlayers", Boolean.valueOf(false));
                                            PlayerLogger(player, "Successfully unset a welcome book!", "");
                                        }
                                        return true;
                                    }
                                    if (args[0].equalsIgnoreCase("backupbook")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                BookHandler bookInHand = new BookHandler(player.getItemInHand());
                                                BookHandler loadedBook = BookLoader.load(this, bookInHand.getAuthor(), bookInHand.getTitle());
                                                if (bookInHand != null && loadedBook != null) {
                                                    bookInHand.setSelled(loadedBook.getSelled());
                                                    BookLoader.delete(this, loadedBook);
                                                    BookLoader.save(this, bookInHand);
                                                    PlayerLogger(player, "Saved!", "");
                                                } else if (bookInHand != null && loadedBook == null) {
                                                    BookLoader.save(this, bookInHand);
                                                    PlayerLogger(player, "Saved!", "");
                                                } else {
                                                    PlayerLogger(player, getConfig().getString("command.error.takeBookInHand." + this.config.language), "Error");
                                                }
                                            } else {
                                                PlayerLogger(player, getConfig().getString("command.error.takeBookInHand." + this.config.language), "Error");
                                            }
                                            return true;
                                        }
                                    }
                                    if (args[0].equalsIgnoreCase("giveall")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            final Player player_final = player;
                                            getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                                public void run() {
                                                    if (player_final.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                        ItemStack item = player_final.getItemInHand();
                                                        BookShop.this.PlayerLogger(player_final, "Giving book to every player!", "");
                                                        BookShop.this.PlayerLogger(player_final, "Please wait ....", "");
                                                        for (OfflinePlayer off : BookShop.this.getServer().getOfflinePlayers()) {
                                                            Player empfaenger = BookShop.this.getmyOfflinePlayer(off.getName());
                                                            if (empfaenger.getInventory().firstEmpty() != -1) {
                                                                empfaenger.getInventory().addItem(new ItemStack[]{item});
                                                                BookShop.this.PlayerLogger(empfaenger, "You were given a book by an admin!", "");
                                                            } else {
                                                                BookShop.this.PlayerLogger(player_final, "Inventory of " + off.getName() + " is full! Can not give him this book!", "Error");
                                                            }
                                                        }
                                                        BookShop.this.PlayerLogger(player_final, "Done!", "");
                                                    } else {
                                                        BookShop.this.PlayerLogger(player_final, "Please take the book in the hand which you want to give to every player!", "Error");
                                                    }
                                                }
                                            }, 1L);
                                        }

                                        return true;
                                    }
                                    if (args[0].equalsIgnoreCase("showdebug")) {
                                        if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            break;
                                        }
                                        if (this.DebugMsg.containsKey(player.getName())) {
                                            this.DebugMsg.remove(player.getName());
                                        } else {
                                            this.DebugMsg.put(player.getName(), Boolean.valueOf(true));
                                        }
                                        return true;
                                    } else if (args[0].equalsIgnoreCase("debugfile")) {
                                        if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            break;
                                        }
                                        getConfig().set("debugfile", Boolean.valueOf(!getConfig().getBoolean("debugfile")));
                                        PlayerLogger(player, "debugfile: " + getConfig().getBoolean("debugfile"), "");
                                        saveConfig();
                                        reloadConfig();
                                        this.config.reload();
                                        return true;
                                    } else if (args[0].equalsIgnoreCase("internet")) {
                                        if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            break;
                                        }
                                        getConfig().set("internet", Boolean.valueOf(!getConfig().getBoolean("internet")));
                                        PlayerLogger(player, "internet: " + getConfig().getBoolean("internet"), "");
                                        saveConfig();
                                        reloadConfig();
                                        this.config.reload();
                                        return true;
                                    } else {
                                        if (this.ActionBookShop.equalsIgnoreCase("version")) {
                                            PlayerLogger(player, "Version: " + getDescription().getVersion(), "");
                                            temptime = (System.nanoTime() - temptime) / 1000000L;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
                                            return true;
                                        }
                                        if (this.ActionBookShop.equalsIgnoreCase("update")) {
                                            if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                                break;
                                            }
                                            install();
                                            return true;
                                        } else if (this.ActionBookShop.equalsIgnoreCase("log")) {
                                            if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                                break;
                                            }
                                            File file = new File("plugins" + File.separator + "BookShop" + File.separator + "debug.txt");
                                            if (file.exists()) {
                                                PlayerLogger(player, "debug.txt is " + file.length() + " Byte big!", "Warning");
                                                PlayerLogger(player, "Type /BookShop deletedebug to delete the debug.txt!", "Warning");
                                            }
                                            return true;
                                        } else if (this.ActionBookShop.equalsIgnoreCase("toggle")) {
                                            if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                                break;
                                            }
                                            if (this.toggle) {
                                                this.toggle = false;
                                            } else {
                                                this.toggle = true;
                                            }
                                            PlayerLogger(player, "BookShop offline: " + this.toggle, "");
                                            return true;
                                        } else if (args[0].equalsIgnoreCase("configconfirm")) {
                                            if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                                break;
                                            }
                                            if (this.Config.containsKey(player)) {
                                                String temp = getConfig().getString((String) this.Config.get(player));
                                                Logger("Temp: " + temp, "Debug");
                                                boolean isboolean = false;
                                                if ((temp.equalsIgnoreCase("true")) || (temp.equalsIgnoreCase("false"))) {
                                                    isboolean = true;
                                                    Logger("Config is boolean!", "Debug");
                                                }
                                                boolean istTrue = false;
                                                if ((isboolean)
                                                        && (((String) this.Set.get(player)).equalsIgnoreCase("true"))) {
                                                    istTrue = true;
                                                    Logger("Config is true!", "Debug");
                                                }

                                                if (!isboolean) {
                                                    getConfig().set((String) this.Config.get(player), this.Set.get(player));
                                                } else {
                                                    getConfig().set((String) this.Config.get(player), Boolean.valueOf(istTrue));
                                                    Logger("Set boolean", "Debug");
                                                }
                                                saveConfig();
                                                reloadConfig();
                                                this.config.reload();
                                                PlayerLogger(player, "You set  " + (String) this.Config.get(player) + " from " + temp + " to " + getConfig().getString((String) this.Config.get(player)) + " !", "Warning");
                                                this.Set.remove(player);
                                                this.Config.remove(player);
                                            } else {
                                                PlayerLogger(player, "Please enter a command first!", "Error");
                                            }
                                            return true;
                                        } else if (args[0].equalsIgnoreCase("configcancel")) {
                                            if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                                break;
                                            }
                                            if (this.Config.containsKey(player)) {
                                                PlayerLogger(player, "Command canceled!", "Warning");
                                                this.Set.remove(player);
                                                this.Config.remove(player);
                                            } else {
                                                PlayerLogger(player, "Please enter a command first!", "Error");
                                            }
                                            return true;
                                        } else {
                                            this.Help.help(sender, args);
                                        }
                                    }
                                }
                                break;


                            case 2:
                                this.ActionBookShop = args[0];
                                if (args[0].equalsIgnoreCase("language")) {
                                    if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                        break;
                                    }
                                    if(!changeLanguage(args[1])){
                                        PlayerLogger(player, "Selected language not found!", "Error");
                                        return true;
                                    }
                                    getConfig().set("language", args[1]);
                                    PlayerLogger(player, "language set to: " + args[1], "");
                                    saveConfig();
                                    Logger("Config saved!", "Debug");
                                    reloadConfig();
                                    Logger("Config reloaded!", "Debug");
                                    Logger("debug reloaded!", "Debug");
                                    this.config.reload();
                                    Logger("Config reloaded!", "Debug");
                                    return true;
                                } else {
                                    if (args[0].equalsIgnoreCase("give")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                ItemStack item = player.getItemInHand();
                                                PlayerLogger(player, "Giving book to " + args[1] + "!", "");
                                                Player empfaenger = getmyOfflinePlayer(args[1]);
                                                if (empfaenger.hasPlayedBefore()) {
                                                    if (empfaenger.getInventory().firstEmpty() != -1) {
                                                        empfaenger.getInventory().addItem(new ItemStack[]{item});
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
                                    }
                                    if (this.ActionBookShop.equalsIgnoreCase("help")) {
                                        if (!this.PermissionsHandler.checkpermissions(player, "BookShop.help")) {
                                            break;
                                        }
                                        if (!Tools.isInteger(args[1])) {
                                            this.Help.help(player, args);
                                            temptime = (System.nanoTime() - temptime) / 1000000L;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
                                            return true;
                                        }
                                        PlayerLogger(player, this.config.commanderrornoint, "Error");
                                        return false;
                                    } else if (this.ActionBookShop.equalsIgnoreCase("report")) {
                                        if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            break;
                                        }
                                        if (!Tools.isInteger(args[1])) {
                                            PlayerLogger(player, this.report.report(331, "Reported issue", args[1], "BookShop", "No stacktrace because of command"), "");
                                            temptime = (System.nanoTime() - temptime) / 1000000L;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
                                            return true;
                                        }
                                        PlayerLogger(player, this.config.commanderrornoint, "Error");
                                        return false;
                                    } else {
                                        this.Help.help(sender, args);
                                    }
                                }
                                break;
                            case 3:
                                this.ActionBookShop = args[0];
                                if (args[0].equalsIgnoreCase("config")) {
                                    if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                        break;
                                    }
                                    if (!this.Config.containsKey(player)) {
                                        this.Config.put(player, args[1]);
                                        String Configtext = args[2];
                                        for (int i = 3; i < args.length; i++) {
                                            Configtext = Configtext.concat(args[i]);
                                        }
                                        this.Set.put(player, Configtext);
                                        PlayerLogger(player, "Do you want to edit " + args[1] + " from " + getConfig().getString(args[1]) + " to " + Configtext + " ?", "Warning");
                                        PlayerLogger(player, String.format("Please confirm within %1$d sec!", new Object[]{Integer.valueOf(getConfig().getInt("Cooldownoftp"))}), "Warning");
                                        PlayerLogger(player, "Please confirm with \"/BookShop configconfirm\" !", "Warning");
                                        PlayerLogger(player, "Please cancel with \"/BookShop configcancel\" !", "Warning");
                                        final Player player1 = player;
                                        getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                            public void run() {
                                                if (BookShop.this.Config.containsKey(player1)) {
                                                    BookShop.this.Config.remove(player1);
                                                    BookShop.this.Set.remove(player1);
                                                    BookShop.this.PlayerLogger(player1, String.format("You havent confirmed within %1$d sec!", new Object[]{Integer.valueOf(BookShop.this.getConfig().getInt("Cooldownoftp"))}), "Warning");
                                                }
                                            }
                                        }, getConfig().getInt("Cooldownoftp") * 20);

                                        return true;
                                    }
                                    PlayerLogger(player, "Please confirm or cancel your last command first!", "Error");
                                    return true;
                                } else if (this.ActionBookShop.equalsIgnoreCase("report")) {
                                    if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                        break;
                                    }
                                    if (!Tools.isInteger(args[1])) {
                                        String text = "";
                                        for (int i = 1; i < args.length; i++) {
                                            text = text.concat(" " + args[i]);
                                        }
                                        PlayerLogger(player, this.report.report(331, "Reported issue", text, "BookShop", "No stacktrace because of command"), "");
                                        temptime = (System.nanoTime() - temptime) / 1000000L;
                                        Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
                                        return true;
                                    }
                                    PlayerLogger(player, this.config.commanderrornoint, "Error");
                                    return false;
                                } else if (args[0].equalsIgnoreCase("loadbook")) {
                                    if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                        break;
                                    }
                                    int slot = player.getInventory().firstEmpty();
                                    if (slot != -1) {
                                        String author = args[1];
                                        String title = "";
                                        for (int i = 2; i < args.length; i++) {
                                            title = title.concat(args[i]);
                                        }
                                        String filename = author + " - " + title + ".txt";
                                        BookHandler book = BookLoader.load(this, filename);
                                        if (book == null) {
                                            PlayerLogger(player, "unknown error: book == null", "Error");
                                            PlayerLogger(player, "May the book doesnt exist!", "Error");
                                            return true;
                                        }
                                        player.getInventory().addItem(new ItemStack[]{book.toItemStack(1)});
                                        Logger("Book loaded!", "Debug");
                                    } else {
                                        PlayerLogger(player, getConfig().getString("Shop.error.inventoryfull." + this.config.language), "Error");
                                    }
                                    return true;
                                } else {
                                    this.Help.help(sender, args);
                                }
                                break;
                            default:
                                if (args.length > 3) {
                                    this.ActionBookShop = args[0];
                                    if (args[0].equalsIgnoreCase("config")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            if (!this.Config.containsKey(player)) {
                                                this.Config.put(player, args[1]);
                                                String Configtext = args[2];
                                                for (int i = 3; i < args.length; i++) {
                                                    Configtext = Configtext.concat(args[i] + " ");
                                                }
                                                this.Set.put(player, Configtext);
                                                PlayerLogger(player, "Do you want to edit " + args[1] + " from " + getConfig().getString(args[1]) + " to " + Configtext + " ?", "Warning");
                                                PlayerLogger(player, String.format("Please confirm within %1$d sec!", new Object[]{Integer.valueOf(getConfig().getInt("Cooldownoftp"))}), "Warning");
                                                PlayerLogger(player, "Please confirm with \"/BookShop configconfirm\" !", "Warning");
                                                PlayerLogger(player, "Please cancel with \"/BookShop configcancel\" !", "Warning");
                                                final Player player1 = player;
                                                getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                                    public void run() {
                                                        if (BookShop.this.Config.containsKey(player1)) {
                                                            BookShop.this.Config.remove(player1);
                                                            BookShop.this.Set.remove(player1);
                                                            BookShop.this.PlayerLogger(player1, String.format("You havent confirmed within %1$d sec!", new Object[]{Integer.valueOf(BookShop.this.getConfig().getInt("Cooldownoftp"))}), "Warning");
                                                        }
                                                    }
                                                }, getConfig().getInt("Cooldownoftp") * 20);

                                                return true;
                                            }
                                            PlayerLogger(player, "Please confirm or cancel your last command first!", "Error");
                                            return true;
                                        }
                                    } else if (args[0].equalsIgnoreCase("loadbook")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
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
                                                    player.getInventory().addItem(new ItemStack[]{book.toItemStack(1)});
                                                    PlayerLogger(player, "Book loaded!", "");
                                                } catch (Exception e) {
                                                    PlayerLogger(player, "Book not found, sorry!", "Error");
                                                }
                                            } else {
                                                PlayerLogger(player, getConfig().getString("Shop.error.inventoryfull." + this.config.language), "Error");
                                            }
                                            return true;
                                        }
                                    } else if ((this.ActionBookShop.equalsIgnoreCase("report"))
                                            && (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission")))) {
                                        if (!Tools.isInteger(args[1])) {
                                            String text = "";
                                            for (int i = 1; i < args.length; i++) {
                                                text = text.concat(" " + args[i]);
                                            }
                                            PlayerLogger(player, this.report.report(331, "Reported issue", text, "BookShop", "No stacktrace because of command"), "");
                                            temptime = (System.nanoTime() - temptime) / 1000000L;
                                            Logger("Command: " + cmd.getName() + " " + args.toString() + " executed in " + temptime + "ms", "Debug");
                                            return true;
                                        }
                                        PlayerLogger(player, this.config.commanderrornoint, "Error");
                                        return false;
                                    }
                                }

                                this.Help.help(player, args);
                                return false;
                        }
                    }
                } else if (cmd.getName().equalsIgnoreCase("BookShop")) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("download")) {
                            String path = "plugins" + File.separator;
                            this.upd.download(path);
                            Logger("Downloaded new Version!", "Warning");
                            Logger("BookShop will be updated on the next restart!", "Warning");
                            return true;
                        }
                        if (args[0].equalsIgnoreCase("reload")) {
                            Logger("Please wait: Reloading this plugin!", "Warning");
                            this.plugman.unloadPlugin("BookShop");
                            this.plugman.loadPlugin("BookShop");
                            Logger("Reloaded!", "");
                            return true;
                        }
                        if (args[0].equalsIgnoreCase("debug")) {
                            getConfig().set("debug", Boolean.valueOf(!getConfig().getBoolean("debug")));
                            Logger("debug set to: " + getConfig().getBoolean("debug"), "");
                            saveConfig();
                            Logger("Config saved!", "Debug");
                            reloadConfig();
                            Logger("Config reloaded!", "Debug");
                            Logger("debug reloaded!", "Debug");
                            this.config.reload();
                            Logger("Config reloaded!", "Debug");
                            return true;
                        }
                        if (args[0].equalsIgnoreCase("debugfile")) {
                            getConfig().set("debugfile", Boolean.valueOf(!getConfig().getBoolean("debugfile")));
                            Logger("debugfile set to: " + getConfig().getBoolean("debugfile"), "");
                            saveConfig();
                            Logger("Config saved!", "Debug");
                            reloadConfig();
                            Logger("Config reloaded!", "Debug");
                            Logger("debugfile reloaded!", "Debug");
                            this.config.reload();
                            Logger("Config reloaded!", "Debug");
                            return true;
                        }
                        if (args[0].equalsIgnoreCase("toggle")) {
                            if (this.toggle) {
                                this.toggle = false;
                            } else {
                                this.toggle = true;
                            }
                            Logger("BookShop offline: " + this.toggle, "");
                            return true;
                        }
                        if (args[0].equalsIgnoreCase("autodownload")) {
                            getConfig().set("autodownload", Boolean.valueOf(!getConfig().getBoolean("autodownload")));
                            Logger("autodownload set to: " + getConfig().getBoolean("autodownload"), "");
                            saveConfig();
                            Logger("Config saved!", "Debug");
                            reloadConfig();
                            Logger("Config reloaded!", "Debug");
                            Logger("debug reloaded!", "Debug");
                            this.config.reload();
                            Logger("Config reloaded!", "Debug");
                            return true;
                        }
                        if (args.length == 2) {
                            if (args[0].equalsIgnoreCase("language")) {
                                if(!changeLanguage(args[1])){
                                    Logger("Selected language not found!", "Error");
                                    return true;
                                }
                                getConfig().set("language", args[1]);
                                Logger("language set to: " + args[1], "");
                                saveConfig();
                                Logger("Config saved!", "Debug");
                                reloadConfig();
                                Logger("Config reloaded!", "Debug");
                                Logger("debug reloaded!", "Debug");
                                this.config.reload();
                                Logger("Config reloaded!", "Debug");
                                return true;
                            }
                        } else if (args[0].equalsIgnoreCase("report")) {
                            String text = "";
                            for (int i = 1; i < args.length; i++) {
                                text = text.concat(" " + args[i]);
                            }
                            Logger(this.report.report(331, "Reported issue", text, "BookShop", "No stacktrace because of command"), "");
                            return true;
                        }
                    } else if (args != null) {
                        if (args.length > 0) {
                            if (args[0].equalsIgnoreCase("report")) {
                                String text = "";
                                for (int i = 1; i < args.length; i++) {
                                    text = text.concat(" " + args[i]);
                                }
                                Logger(this.report.report(331, "Reported issue", text, "BookShop", "No stacktrace because of command"), "");
                                return true;
                            }
                        }
                    }
                    return false;
                }
                return false;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    if ((sender instanceof Player)) {
                        Player p = (Player) sender;
                        if (this.PermissionsHandler.checkpermissions(p, "BookShop.admin")) {
                            if (this.toggle) {
                                this.toggle = false;
                            } else {
                                this.toggle = true;
                            }
                            PlayerLogger(p, "BookShop offline: " + this.toggle, "");
                            return true;
                        }
                    } else {
                        if (this.toggle) {
                            this.toggle = false;
                        } else {
                            this.toggle = true;
                        }
                        Logger("BookShop offline: " + this.toggle, "");
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
            this.report.report(3316, "A Command dont work", e1.getMessage(), "BookShop", e1);
            Logger("Uncatched Exeption!", "Error");
            try {
                MetricsHandler.Error += 1;
            } catch (Exception e11) {
            }
        }
        return false;
    }

    public void Logger(String msg, String TYPE) {
        try {
            if ((TYPE.equalsIgnoreCase("Warning")) || (TYPE.equalsIgnoreCase("Error"))) {
                System.err.println(PrefixConsole + TYPE + ": " + msg);
                if (this.config.debugfile) {
                    this.Loggerclass.log("Error: " + msg);
                }
                if (this.playerManager != null) {
                    this.playerManager.BroadcastconsoleMsg("BookShop.consolemsg", " Warning: " + msg);
                }
            } else if (TYPE.equalsIgnoreCase("Debug")) {
                if (this.config.debug) {
                    System.out.println(PrefixConsole + "Debug: " + msg);
                }
                if (this.config.debugfile) {
                    this.Loggerclass.log("Debug: " + msg);
                }
                if (this.playerManager != null) {
                    this.playerManager.BroadcastconsoleMsg("BookShop.consolemsg", " Debug: " + msg);
                }
            } else {
                if (this.playerManager != null) {
                    this.playerManager.BroadcastconsoleMsg("BookShop.consolemsg", msg);
                }
                System.out.println(PrefixConsole + msg);
                if (this.config.debugfile) {
                    this.Loggerclass.log(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[BookShop] Error: Uncatch Exeption!");
            if (this.report != null) {
                this.report.report(3317, "Logger doesnt work", e.getMessage(), "BookShop", e);
            }
            try {
                MetricsHandler.Error += 1;
            } catch (Exception e1) {
            }
        }
    }

    public void PlayerLogger(Player p, String msg, String TYPE) {
        try {
            if (TYPE.equalsIgnoreCase("Error")) {
                if (this.config.UsePrefix) {
                    p.sendMessage(this.config.Prefix + Prefix + ChatColor.RED + "Error: " + this.config.Text + msg);
                    if (this.config.debugfile) {
                        this.Loggerclass.log("Player: " + p.getName() + " Error: " + msg);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Error: " + this.config.Text + msg);
                    if (this.config.debugfile) {
                        this.Loggerclass.log("Player: " + p.getName() + " Error: " + msg);
                    }
                }
                if (this.playerManager != null) {
                    this.playerManager.BroadcastconsoleMsg("BookShop.gamemsg", "Player: " + p.getName() + " Error: " + msg);
                }
            } else {
                if (this.config.UsePrefix) {
                    p.sendMessage(this.config.Prefix + Prefix + this.config.Text + msg);
                    if (this.config.debugfile) {
                        this.Loggerclass.log("Player: " + p.getName() + " Msg: " + msg);
                    }
                } else {
                    p.sendMessage(this.config.Text + msg);
                    if (this.config.debugfile) {
                        this.Loggerclass.log("Player: " + p.getName() + " Msg: " + msg);
                    }
                }
                if (this.playerManager != null) {
                    this.playerManager.BroadcastconsoleMsg("BookShop.gamemsg", "Player: " + p.getName() + " " + msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[BookShop] Error: Uncatch Exeption!");
            this.report.report(3317, "PlayerLogger doesnt work", e.getMessage(), "BookShop", e);
            try {
                MetricsHandler.Error += 1;
            } catch (Exception e1) {
            }
        }
    }
}