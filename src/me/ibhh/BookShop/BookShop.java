package me.ibhh.BookShop;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.ChatColor;
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
    public String[] commands = {"help", "showdebug", "debugfile", "internet", "version", "reload", "deletedebug", "log", "toggle", "language", "report", "backupbook", "loadbook", "giveall", "give", "setwelcomebook", "removewelcomebook"};
    private String mcversion = "1.4.5";
    
    @Override
    public void onDisable() {
        this.toggle = true;
        long timetemp = System.currentTimeMillis();
        this.metricshandler.saveStatsFiles();
        timetemp = System.currentTimeMillis() - timetemp;
        Logger("disabled in " + timetemp + "ms", "");
    }

    @Override
    public void onEnable() {
        try {
            long timetemp1 = System.nanoTime();
            this.Loggerclass = new Logger(this);
            Logger("*****************************", "Warning");
            Logger("Because of some Bukkitchanges", "Warning");
            Logger("you have to update the plugin", "Warning");
            Logger("manually. The updater was removed!", "Warning");
            Logger("This plugin needs a update every", "Warning");
            Logger("MC-Update!!!!", "Warning");
            Logger("*****************************", "Warning");
            Logger("Your Bukkit version: " + getServer().getBukkitVersion(), "Warning");
            if(getServer().getBukkitVersion().contains(mcversion)){
                Logger("This plugin is compatible to this bukkit-version", "Warning");
            } else {
                Logger("Your plugin-version is NOT compatible!", "Error");
                setEnabled(false);
                return;
            }
            Exception ex1 = null;
            try {
                this.config = new ConfigHandler(this);
                this.config.loadConfigonStart();
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
            
            this.metricshandler = new MetricsHandler(this);
            this.metricshandler.loadStatsFiles();
            getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    BookShop.this.metricshandler.saveStatsFiles();
                }
            }, 200L, 50000L);

            this.metricshandler = new MetricsHandler(this);
            this.metricshandler.loadStatsFiles();
            getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
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
                                            getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (player_final.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
                                                        ItemStack item = player_final.getItemInHand();
                                                        BookShop.this.PlayerLogger(player_final, "Giving book to every player!", "");
                                                        BookShop.this.PlayerLogger(player_final, "Please wait ....", "");
                                                        for (OfflinePlayer off : BookShop.this.getServer().getOfflinePlayers()) {
                                                            Player empfaenger = Tools.getmyOfflinePlayer(BookShop, off.getName());
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
                                            if (this.ActionBookShop.equalsIgnoreCase("log")) {
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
                                                Player empfaenger = Tools.getmyOfflinePlayer(BookShop, args[1]);
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