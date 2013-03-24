package me.ibhh.BookShop;

import java.io.File;
import java.util.HashMap;
import me.ibhh.BookShop.BookHandler.BookHandler;
import me.ibhh.BookShop.BookHandler.BookHandlerUtility;
import me.ibhh.BookShop.Tools.ToolUtility;
import me.ibhh.BookShop.Tools.Tools;
import me.ibhh.BookShop.logger.LoggerUtility;
import me.ibhh.BookShop.update.Update;
import me.ibhh.BookShop.update.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BookShop extends JavaPlugin {

    private String ActionBookShop;
    public double getmoney;
    public int SubstractedXP;
    private Utilities pluginmanager;
    private LoggerUtility logger;
    private Help Help;
    private Update update;
    public ConfigHandler config;
    public BookShopListener ListenerShop;
    public BookShop BookShop = this;
    public PermissionsChecker PermissionsHandler;
    public iConomyHandler MoneyHandler;
    public boolean toggle = true;
    public MetricsHandler metricshandler;
    public PlayerManager playerManager;
    private ReportToHost report;
    private static String SHOP_CONFIG_FILE;
    public YamlConfiguration SHOP_configuration;
    private File configurationFile;
    public HashMap<Player, Boolean> commandexec = new HashMap();
    public HashMap<String, Boolean> DebugMsg = new HashMap();
    private HashMap<Player, String> Config = new HashMap();
    private HashMap<Player, String> Set = new HashMap();
    public String[] commands = {"help", "showdebug", "debugfile", "internet", "version", "reload", "toggle", "language", "report", "backupbook", "loadbook", "giveall", "give", "setwelcomebook", "removewelcomebook"};

    public boolean isBukkitVersionCompatible() {
        return Tools.packagesExists("net.minecraft.server.v1_4_5.MinecraftServer")
                || Tools.packagesExists("net.minecraft.server.v1_4_6.MinecraftServer")
                || Tools.packagesExists("net.minecraft.server.v1_4_R1.MinecraftServer")
                || Tools.packagesExists("net.minecraft.server.v1_5_R1.MinecraftServer")
                || Tools.packagesExists("net.minecraft.server.v1_5_R2.MinecraftServer")
                || Tools.packagesExists("net.minecraft.server.MinecraftServer");
    }

    public static String getRawBukkitVersion() {
        String[] a = Bukkit.getServer().getBukkitVersion().split("-");
        return a[0];
    }

    @Override
    public void onDisable() {
        this.toggle = true;
        long timetemp = System.currentTimeMillis();
        if (metricshandler != null) {
            this.metricshandler.saveStatsFiles();
        }
        timetemp = System.currentTimeMillis() - timetemp;
        Logger("disabled in " + timetemp + "ms", "");
    }

    @Override
    public void onEnable() {
        try {
            long timetemp1 = System.nanoTime();
            getLoggerUtility();
            Exception ex1 = null;
            try {
                this.config = new ConfigHandler(this);
                this.config.loadConfigonStart();
            } catch (Exception e1) {
                ex1 = e1;
                Logger("Error on loading config: " + e1.getMessage(), "Error");
                e1.printStackTrace();
            }
            getUpdate().startUpdateTimer();
            Logger("Your Bukkit version: " + getServer().getBukkitVersion(), "Warning");
            if (isBukkitVersionCompatible()) {
                Logger("This plugin is compatible to this bukkit-version", "Warning");
            } else {
                Logger("Your plugin-version is NOT compatible!", "Error");
                Logger("*****************************", "Warning");
                Logger("Because of some Bukkitchanges", "Warning");
                Logger("you have to update the plugin", "Warning");
                Logger("manually.", "Warning");
                Logger("*****************************", "Warning");
                setEnabled(false);
                return;
            }
            getReportHandler();
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
                Logger("Version: " + getVersion() + " failed to enable!", "Error");
            }
            try {
                this.playerManager = new PlayerManager(this);
                getPluginManager();
                this.Help = new Help(this);
                this.MoneyHandler = new iConomyHandler(this);
                this.PermissionsHandler = new PermissionsChecker(this, "BookShop");
                getUpdate();
                this.ListenerShop = new BookShopListener(this);
            } catch (Exception e1) {
                Logger("Error on enabling: " + e1.getMessage(), "Error");
                this.report.report(334, "Error on enabling", e1.getMessage(), "BookShop", e1);
                e1.printStackTrace();
                Logger("Version: " + getVersion() + " failed to enable!", "Error");
                setEnabled(false);
            }

            this.metricshandler = new MetricsHandler(this);
            this.metricshandler.loadStatsFiles();
            getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    BookShop.this.metricshandler.saveStatsFiles();
                }
            }, 200L, 50000L);

            getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                @Override
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

    public ReportToHost getReportHandler() {
        if (report == null) {
            report = new ReportToHost(this);
        }
        return report;
    }

    public Utilities getPluginManager() {
        if (pluginmanager == null) {
            pluginmanager = new Utilities(this);
        }
        return pluginmanager;
    }

    public Update getUpdate() {
        if (update == null) {
            update = new Update(this);
        }
        return update;
    }

    public LoggerUtility getLoggerUtility() {
        if (logger == null) {
            logger = new LoggerUtility(this);
        }
        return logger;
    }

    public float getVersion() {
        try {
            return Float.parseFloat(getDescription().getVersion());
        } catch (Exception e) {
            getLoggerUtility().log("Could not parse version in float", LoggerUtility.Level.INFO);
            getLoggerUtility().log("Error getting version of " + this.getName() + "! Message: " + e.getMessage(), LoggerUtility.Level.ERROR);
            this.report.report(3310, "Error getting version of " + this.getName() + "!", e.getMessage(), "Paypassage", e);
            getLoggerUtility().log("Uncatched Exeption!", LoggerUtility.Level.ERROR);
        }
        return 0;
    }

    public void onReload() {
        onDisable();
        onEnable();
    }

    @Override
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
                                            getPluginManager().unloadPlugin("BookShop");
                                            getPluginManager().loadPlugin("BookShop");
                                            PlayerLogger(player, "Reloaded!", "");
                                        }
                                        return true;
                                    }
                                    if (args[0].equalsIgnoreCase("setwelcomebook")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                BookHandler bookInHand = new BookHandlerUtility(player.getItemInHand()).getBookHandler();
                                                BookHandler loadedBook = BookLoader.load(this, bookInHand.getAuthor(), bookInHand.getTitle());
                                                if (loadedBook != null) {
                                                    getLoggerUtility().log("loadedBook: Author: " + loadedBook.getAuthor(), LoggerUtility.Level.DEBUG);
                                                    bookInHand.setSelled(loadedBook.getSelled());
                                                    BookLoader.delete(this, loadedBook);
                                                }
                                                BookLoader.save(this, bookInHand);
                                                getLoggerUtility().log("BookinHand: Author: " + bookInHand.getAuthor(), LoggerUtility.Level.DEBUG);
                                                getConfig().set("GiveBookToNewPlayers", Boolean.valueOf(true));
                                                getConfig().set("Book", bookInHand.getAuthor() + " - " + bookInHand.getTitle() + ".txt");
                                                saveConfig();
                                                PlayerLogger(player, "Successfully set a welcome book!", "");
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
                                    if (args[0].equalsIgnoreCase("infobook")) {
                                        if (this.PermissionsHandler.checkpermissions(player, "BookShop.infobook")) {
                                            if (player.getItemInHand() != null) {
                                                getLoggerUtility().log("has item", LoggerUtility.Level.DEBUG);
                                                if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                    getLoggerUtility().log("has item written book", LoggerUtility.Level.DEBUG);
                                                    try {
                                                        BookHandler bookInChest = new BookHandlerUtility(player.getItemInHand()).getBookHandler();
                                                        BookHandler loadedBook = BookLoader.load(this, bookInChest.getAuthor(), bookInChest.getTitle());
                                                        if (loadedBook != null) {
                                                            BookLoader.save(this, loadedBook);
                                                        } else {
                                                            BookLoader.save(this, bookInChest);
                                                            loadedBook = bookInChest;
                                                        }
                                                        PlayerLogger(player, String.format(getConfig().getString("Shop.success.bookselled." + config.language), loadedBook.getTitle(), loadedBook.getAuthor()), "");
                                                        PlayerLogger(player, String.format(getConfig().getString("Shop.success.bookselled2." + config.language), loadedBook.selled()), "");
                                                        PlayerLogger(player, String.format(getConfig().getString("Shop.success.bookselled3." + config.language), loadedBook.getPages().size()), "");
                                                    } catch (InvalidBookException ex) {
                                                        ex.printStackTrace();
                                                        PlayerLogger(player, "Something is wrong with the book in the chest :(", "Error");
                                                    }
                                                } else {
                                                    getLoggerUtility().log(player, "You need a book for this commands", LoggerUtility.Level.ERROR);
                                                }
                                            }
                                        }
                                        return true;
                                    }
                                    if (args[0].equalsIgnoreCase("backupbook")) {
                                        if (this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
                                            if (player.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                BookHandler bookInHand = new BookHandlerUtility(player.getItemInHand()).getBookHandler();
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
                                            getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (player_final.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                        ItemStack item = player_final.getItemInHand();
                                                        BookShop.this.PlayerLogger(player_final, "Giving book to every player!", "");
                                                        BookShop.this.PlayerLogger(player_final, "Please wait ....", "");
                                                        for (OfflinePlayer off : BookShop.this.getServer().getOfflinePlayers()) {
                                                            Player empfaenger = ToolUtility.getTools().getmyOfflinePlayer(BookShop, off.getName());
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
//                                        if (this.ActionBookShop.equalsIgnoreCase("update")) {
//                                            if (!this.PermissionsHandler.checkpermissions(player, getConfig().getString("help.commands." + this.ActionBookShop.toLowerCase() + ".permission"))) {
//                                                break;
//                                            }
//                                            install();
//                                            return true;
//                                        } else 
                                        if (this.ActionBookShop.equalsIgnoreCase("toggle")) {
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
                                                Player empfaenger = ToolUtility.getTools().getmyOfflinePlayer(BookShop, args[1]);
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
                                        getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                                            @Override
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
//                        if (args[0].equalsIgnoreCase("download")) {
//                            String path = "plugins" + File.separator;
//                            this.upd.download(path);
//                            Logger("Downloaded new Version!", "Warning");
//                            Logger("BookShop will be updated on the next restart!", "Warning");
//                            return true;
//                        }
                        if (args[0].equalsIgnoreCase("reload")) {
                            Logger("Please wait: Reloading this plugin!", "Warning");
                            getPluginManager().unloadPlugin("BookShop");
                            getPluginManager().loadPlugin("BookShop");
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
            if ((TYPE.equalsIgnoreCase("Warning"))) {
                getLoggerUtility().log(msg, LoggerUtility.Level.WARNING);
            } else if (TYPE.equalsIgnoreCase("Debug")) {
                getLoggerUtility().log(msg, LoggerUtility.Level.DEBUG);
                if (this.playerManager != null) {
                    this.playerManager.BroadcastconsoleMsg("BookShop.consolemsg", " Debug: " + msg);
                }
            } else if ((TYPE.equalsIgnoreCase("Error"))) {
                getLoggerUtility().log(msg, LoggerUtility.Level.WARNING);
            } else {
                if (this.playerManager != null) {
                    this.playerManager.BroadcastconsoleMsg("BookShop.consolemsg", msg);
                }
                getLoggerUtility().log(msg, LoggerUtility.Level.INFO);
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
                getLoggerUtility().log(p, msg, LoggerUtility.Level.ERROR);
                if (this.playerManager != null) {
                    this.playerManager.BroadcastconsoleMsg("BookShop.gamemsg", "Player: " + p.getName() + " Error: " + msg);
                }
            } else if (TYPE.equalsIgnoreCase("Warning")) {
                getLoggerUtility().log(p, msg, LoggerUtility.Level.WARNING);
            } else {
                getLoggerUtility().log(p, msg, LoggerUtility.Level.INFO);
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