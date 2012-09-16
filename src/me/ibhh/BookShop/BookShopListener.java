package me.ibhh.BookShop;

import java.io.File;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BookShopListener implements Listener {

    private final BookShop plugin;
    private InteractHandler interactHandler;
    private CreateHandler createHandler;
    public ChestHandler chestHandler;
    public HashMap<Player, Chest> ChestViewers = new HashMap<Player, Chest>();
    private static final BlockFace[] shopFaces = {BlockFace.SELF, BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public BookShopListener(BookShop BookShop) {
        this.plugin = BookShop;
        try {
            interactHandler = new InteractHandler(plugin);
            createHandler = new CreateHandler(plugin);
            chestHandler = new ChestHandler(plugin);
            BookShop.getServer().getPluginManager().registerEvents(this, BookShop);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[BookShop] Error: Uncatch Exeption!");
            plugin.report.report(3318, "Logger doesnt work", e.getMessage(), "BookShopListener", e);
            try {
                plugin.metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void invClose(InventoryCloseEvent event) {
        final InventoryCloseEvent ev = event;
        if (ChestViewers.containsKey((Player) event.getPlayer())) {
            plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        if (ChestViewers.get((Player) ev.getPlayer()).getBlockInventory().contains(Material.WRITTEN_BOOK)) {
                            plugin.Logger("Chest contains Written_Book", "Debug");
                            Block chestblock = ChestViewers.get((Player) ev.getPlayer()).getBlock();
                            if (chestblock.getRelative(BlockFace.UP).getState() instanceof Sign) {
                                plugin.Logger("Block Relative UP = Sign", "Debug");
                                Sign sign = (Sign) chestblock.getRelative(BlockFace.UP).getState();
                                if (sign.getLine(0).equalsIgnoreCase("[BookShop]")) {
                                    plugin.Logger("Sign is BookShop", "Debug");
                                    int slot = ChestViewers.get((Player) ev.getPlayer()).getBlockInventory().first(Material.WRITTEN_BOOK);
                                    ItemStack item = ChestViewers.get((Player) ev.getPlayer()).getBlockInventory().getItem(slot);
                                    BookHandler bookInChest = new BookHandler(item);
                                    if (bookInChest != null) {
                                        plugin.Logger("Book != null", "Debug");
                                        BookHandler loadedBook = BookLoader.load(plugin, bookInChest.getAuthor(), bookInChest.getTitle());
                                        if (!loadedBook.getAuthor().equals(bookInChest.getAuthor()) || !loadedBook.getTitle().equals(bookInChest.getTitle())) {
                                            BookLoader.save(plugin, bookInChest);
                                        }
                                        sign.setLine(2, bookInChest.getTitle());
                                        sign.update();
                                    }
                                }
                            }
                        }
                        ChestViewers.remove((Player) ev.getPlayer());
                    } catch (Exception e) {
                        e.printStackTrace();
                        plugin.report.report(3319, "Error on closing chest", e.getMessage(), "BookShopListener", e);
                        MetricsHandler.Error++;
                        plugin.Logger("uncatched Exception!", "Error");
                    }
                }
            }, 2);
            plugin.Logger("Player from ChestViewers removed: " + event.getPlayer().getName(), "Debug");

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (plugin.toggle) {
            return;
        }
        /**
         * Block piston = event.getBlock(); BlockState state =
         * piston.getState(); MaterialData data = state.getData(); BlockFace
         * direction = null;
         *
         * if ((data instanceof PistonBaseMaterial)) { direction =
         * ((PistonBaseMaterial) data).getFacing(); }
         *
         * if (direction == null) { return; }
         *
         * Block moved = piston.getRelative(direction, 2);
         *
         * if ((moved.getType() == Material.CHEST) || (moved.getType() ==
         * Material.SIGN)) { }
         *
         */
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (plugin.toggle) {
            return;
        }
        /**
         * Block piston = event.getBlock(); BlockState state =
         * piston.getState(); MaterialData data = state.getData(); BlockFace
         * direction = null;
         *
         * if ((data instanceof PistonBaseMaterial)) { direction =
         * ((PistonBaseMaterial) data).getFacing(); Block block =
         * event.getBlock().getRelative(direction); }
         *
         * if (direction == null) { return; }
         *
         * for (int i = 0; i < event.getLength() + 2; i++) { Block block =
         * piston.getRelative(direction, i); Protection protection =
         * lwc.findProtection(block);
         *
         * if (block.getType() == Material.AIR) { break; } }
         *
         */
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void invClick(InventoryClickEvent event) {
        try {
            final Player player = (Player) event.getWhoClicked();
            if (ChestViewers.containsKey(player)) {
                plugin.Logger("Slot: " + event.getSlot(), "Debug");
                plugin.Logger("Player " + player.getName() + " clicked on " + event.getInventory().getType().name() + "!", "Debug");
                if (event.getInventory().getType().equals(InventoryType.CHEST)) {
                    if (event.getCurrentItem() == null) {
                        return;
                    }
                    BookHandler book = null;
                    try {
                        book = new BookHandler(event.getCurrentItem());
                    } catch (InvalidBookException ex) {
                    }
                    if (book != null) {
                        if (!book.getAuthor().equalsIgnoreCase(player.getName())) {
                            if (!plugin.PermissionsHandler.checkpermissionssilent(player, "BookShop.sell.other")) {
                                plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.onlyyourbooks." + plugin.getConfig().getString("language")), "Error");
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                    if (!event.getCurrentItem().getType().equals(Material.WRITTEN_BOOK) && !event.getCurrentItem().getType().equals(Material.AIR)) {
                        if (plugin.getConfig().getBoolean("useBookandQuill")) {
                            if (!event.getCurrentItem().getType().equals(Material.BOOK_AND_QUILL)) {
                                plugin.Logger("Item is " + event.getCurrentItem().getType().name(), "Debug");
                                plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.wrongItem." + plugin.config.language), "Error");
                                event.setCancelled(true);
                            }
                        } else {
                            plugin.Logger("Item is " + event.getCurrentItem().getType().name(), "Debug");
                            plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.wrongItem." + plugin.config.language), "Error");
                            event.setCancelled(true);
                        }
                    } else if (((countWrittenBooks(((Chest) this.ChestViewers.get(player)).getInventory()) <= 0) || (!((Chest) this.ChestViewers.get(player)).getInventory().contains(event.getCurrentItem())))
                            && (countWrittenBooks(((Chest) this.ChestViewers.get(player)).getInventory()) > 0)) {
                        if (plugin.getConfig().getBoolean("useBookandQuill")) {
                            plugin.Logger("UseBooksandQuill = true", "Debug");
                            if (!event.getCursor().getType().equals(Material.BOOK_AND_QUILL) && !event.getCurrentItem().getType().equals(Material.BOOK_AND_QUILL)) {
                                plugin.Logger("!event.getCursor().getType().equals(Material.BOOK) || !event.getCurrentItem().getType().equals(Material.BOOK)", "Debug");
                                plugin.Logger("Item is " + event.getCurrentItem().getType().name(), "Debug");
                                plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.wrongItem." + plugin.config.language), "Error");
                                event.setCancelled(true);
                            }
                        } else {
                            plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.onebook." + plugin.config.language), "Error");
                            event.setCancelled(true);
                        }
                    } else if (event.isShiftClick() && countWrittenBooks(((Chest) this.ChestViewers.get(player)).getInventory()) > 0) {
                        plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.onebook." + plugin.config.language), "Error");
                        event.setCancelled(true);
                    }
                } else {
                    plugin.Logger("Player " + player.getName() + " clicked on own Inventory!", "Debug");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.report.report(3320, "invClick doesnt work", e.getMessage(), "BookShopListener", e);
            System.out.println("[BookShop] Error: Uncatch Exeption!");
            try {
                plugin.metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void join(PlayerJoinEvent event) {
        try {
            if (!plugin.toggle) {
                if (plugin.PermissionsHandler.checkpermissionssilent(event.getPlayer(), "BookShop.admin")) {
                    if (plugin.updateaviable) {
                        plugin.PlayerLogger(event.getPlayer(), "installed BookShop version: " + plugin.Version + ", latest version: " + plugin.newversion, "Warning");
                        plugin.PlayerLogger(event.getPlayer(), "New BookShop update aviable: type \"/BookShop update\" to install!", "Warning");
                        if (!plugin.getConfig().getBoolean("installondownload")) {
                            plugin.PlayerLogger(event.getPlayer(), "Please edit the config.yml if you wish that the plugin updates itself atomatically!", "Warning");
                        }
                    }
                    File file = new File("plugins" + File.separator + "BookShop" + File.separator + "debug.txt");
                    if (file.exists()) {
                        if (file.length() > 100000000) {
                            plugin.PlayerLogger(event.getPlayer(), "debug.txt is " + file.length() + "Byte big!", "Warning");
                            plugin.PlayerLogger(event.getPlayer(), "Type /BookShop deletedebug to delete the debug.txt!", "Warning");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[BookShop] Error: Uncatch Exeption!");
            plugin.report.report(3321, "Player join throws error", e.getMessage(), "BookShopListener", e);
            try {
                plugin.metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void precommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.toggle) {
            if (event.getMessage().toLowerCase().startsWith(("/BookShop".toLowerCase()))) {
                if (plugin.config.debugfile) {
                    plugin.Loggerclass.log("Player: " + event.getPlayer().getName() + " command: " + event.getMessage());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void aendern(SignChangeEvent event) {
        if (!plugin.toggle) {
            if (plugin.config.debug) {
                plugin.Logger("First Line " + event.getLine(0), "Debug");
            }
            if (event.getLine(0).equalsIgnoreCase("[BookShop]")) {
                createHandler.CreateBookShop(event);
            }
        }
    }

    public static Block getAttachedFace(org.bukkit.block.Sign sign) {
        return sign.getBlock().getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
    }

    private static boolean isCorrectSign(org.bukkit.block.Sign sign, Block block) {
        return (sign != null) && ((sign.getBlock().equals(block)) || (getAttachedFace(sign).equals(block)));
    }

    public static boolean isSign(Block block) {
        return block.getState() instanceof Sign;
    }

    public Sign findSignBook(Block block) {
        for (BlockFace bf : shopFaces) {
            Block faceBlock = block.getRelative(bf);
            if (isSign(faceBlock)) {
                Sign sign = (Sign) faceBlock.getState();
                if ((blockIsValid(sign)) && ((faceBlock.equals(block)) || (getAttachedFace(sign).equals(block)))) {
                    return sign;
                }
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        try {
            if (!plugin.toggle) {
                Player p = event.getPlayer();
                if (!(event.getBlock().getState() instanceof Sign)) {
                    if (plugin.config.debug) {
                        plugin.Logger("Block dedected", "Debug");
                    }
                    org.bukkit.block.Sign sign = findSignBook(event.getBlock());
                    if (isCorrectSign(sign, event.getBlock())) {
                        if (sign.getLine(0).equalsIgnoreCase("[BookShop]")) {
                            String[] line = sign.getLines();
                            if (blockIsValid(sign)) {
                                if (line[1].equalsIgnoreCase(p.getName()) && plugin.PermissionsHandler.checkpermissions(p, "BookShop.create")) {
                                    plugin.PlayerLogger(p, "Destroying BookShop!", "");
                                    MTLocation loc = MTLocation.getMTLocationFromLocation(sign.getLocation());
                                    if (plugin.metricshandler.Shop.containsKey(loc)) {
                                        plugin.metricshandler.Shop.remove(loc);
                                        plugin.Logger("Removed Shop from list!", "Debug");
                                    }
                                    sign.getBlock().getRelative(BlockFace.DOWN).breakNaturally();
                                } else if (plugin.PermissionsHandler.checkpermissions(p, "BookShop.create.admin")) {
                                    plugin.PlayerLogger(p, "Destroying BookShop (Admin)!", "");
                                    MTLocation loc = MTLocation.getMTLocationFromLocation(sign.getLocation());
                                    if (line[1].equalsIgnoreCase("AdminShop")) {
                                        if (plugin.metricshandler.AdminShop.containsKey(loc)) {
                                            plugin.metricshandler.AdminShop.remove(loc);
                                            plugin.Logger("Removed AdminShop from list!", "Debug");
                                        }
                                    }
                                    if (plugin.metricshandler.Shop.containsKey(loc)) {
                                        plugin.metricshandler.Shop.remove(loc);
                                        plugin.Logger("Removed Shop from list!", "Debug");
                                    }
                                    sign.getBlock().getRelative(BlockFace.DOWN).breakNaturally();
                                } else {
                                    event.setCancelled(true);
                                    plugin.Logger("Event canceled! 1", "Debug");
                                }
                            }
                        }
                    } else {
                        int protection = chestHandler.isProtectedChest(event.getBlock(), p, "BookShop.create.admin");
                        if (protection == 1) {
                            plugin.PlayerLogger(p, "Destroying BookShop!", "");
                            MTLocation loc = MTLocation.getMTLocationFromLocation(sign.getLocation());
                            if (plugin.metricshandler.Shop.containsKey(loc)) {
                                plugin.metricshandler.Shop.remove(loc);
                                plugin.Logger("Removed Shop from list!", "Debug");
                            }
                            event.getBlock().getRelative(BlockFace.UP).breakNaturally();
                            if (sign.getLine(1).equalsIgnoreCase("AdminShop")) {
                                if (plugin.metricshandler.AdminShop.containsKey(loc)) {
                                    plugin.metricshandler.AdminShop.remove(loc);
                                    plugin.Logger("Removed AdminShop from list!", "Debug");
                                }
                            }
                            if (plugin.metricshandler.Shop.containsKey(loc)) {
                                plugin.metricshandler.Shop.remove(loc);
                                plugin.Logger("Removed Shop from list!", "Debug");
                            }
                            event.getBlock().getRelative(BlockFace.UP).breakNaturally();
                        } else if (protection == -1) {
                            event.setCancelled(true);
                            plugin.Logger("Event canceled! 2", "Debug");
                        }
                    }
                } else {
                    Sign s = (Sign) event.getBlock().getState();
                    String[] line = s.getLines();
                    plugin.Logger("Line 0: " + line[0], "Debug");
                    plugin.Logger("Sign dedected", "Debug");
                    if (line[0].equalsIgnoreCase("[BookShop]")) {
                        if (this.blockIsValid(line, "break", p)) {
                            if (s.getLine(1).equalsIgnoreCase(p.getName()) && plugin.PermissionsHandler.checkpermissions(p, "BookShop.create")) {
                                plugin.PlayerLogger(p, "Destroying BookShop!", "");
                                MTLocation loc = MTLocation.getMTLocationFromLocation(s.getLocation());
                                if (plugin.metricshandler.Shop.containsKey(loc)) {
                                    plugin.metricshandler.Shop.remove(loc);
                                    plugin.Logger("Removed Shop from list!", "Debug");
                                }
                                s.getBlock().getRelative(BlockFace.DOWN).breakNaturally();
                            } else if (plugin.PermissionsHandler.checkpermissions(p, "BookShop.create.admin")) {
                                plugin.PlayerLogger(p, "Destroying BookShop (Admin)!", "");
                                MTLocation loc = MTLocation.getMTLocationFromLocation(s.getLocation());
                                if (line[1].equalsIgnoreCase("AdminShop")) {
                                    if (plugin.metricshandler.AdminShop.containsKey(loc)) {
                                        plugin.metricshandler.AdminShop.remove(loc);
                                        plugin.Logger("Removed AdminShop from list!", "Debug");
                                    }
                                }
                                if (plugin.metricshandler.Shop.containsKey(loc)) {
                                    plugin.metricshandler.Shop.remove(loc);
                                    plugin.Logger("Removed Shop from list!", "Debug");
                                }
                                s.getBlock().getRelative(BlockFace.DOWN).breakNaturally();
                            } else {
                                event.setCancelled(true);
                                plugin.Logger("Event canceled! 3", "Debug");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.report.report(3322, "Breaking BookShop throws error", e.getMessage(), "BookShopListener", e);
            System.out.println("[BookShop] Error: Uncatch Exeption!");
            try {
                plugin.metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        try {
            interactHandler.InteracteventHandler(event);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[BookShop] Error: Uncatch Exeption!");
            plugin.report.report(3323, "BookShop interact throws error", e.getMessage(), "BookShopListener", e);
            try {
                plugin.metricshandler.Error++;
            } catch (Exception e1) {
            }
        }
    }

    public double getPrice(Sign s, Player p, boolean BookandQuill) {
        double doubleline3 = 0;
        try {
            doubleline3 = Double.parseDouble(s.getLine(3));
        } catch (Exception e) {
            if (plugin.getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
            try {
                String[] a = s.getLine(3).split(":");
                if (plugin.getConfig().getBoolean("debug")) {
                    plugin.Logger("getPrice: a1: " + a[0] + " a2: " + a[1], "Debug");
                }
                double[] b = new double[2];
                b[0] = Double.parseDouble(a[0]);
                b[1] = Double.parseDouble(a[1]);
                if (BookandQuill) {
                    doubleline3 = b[1];
                } else {
                    doubleline3 = b[0];
                }
            } catch (Exception e1) {
                if (plugin.getConfig().getBoolean("debug")) {
                    e.printStackTrace();
                }
            }
        }
        return doubleline3;
    }

    public double getPrice(String s, Player p, boolean BookandQuill) {
        double doubleline3 = 0;
        try {
            doubleline3 = Double.parseDouble(s);
        } catch (Exception e) {
            if (plugin.getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
            try {
                String[] a = s.split(":");
                if (plugin.getConfig().getBoolean("debug")) {
                    plugin.Logger("getPrice: a1: " + a[0] + " a2: " + a[1], "Debug");
                }
                double[] b = new double[2];
                b[0] = Double.parseDouble(a[0]);
                b[1] = Double.parseDouble(a[1]);
                if (BookandQuill) {
                    doubleline3 = b[1];
                } else {
                    doubleline3 = b[0];
                }
            } catch (Exception e1) {
                if (plugin.getConfig().getBoolean("debug")) {
                    e.printStackTrace();
                }
            }
        }
        return doubleline3;
    }

    public boolean blockIsValid(String[] lines, String von, Player p) {
        boolean a = false;
        plugin.Logger("Checking if block is valid!", "Debug");
        double temp = 0;
        double[] b = new double[2];
        try {
            temp = Double.parseDouble(lines[3]);
        } catch (Exception e) {
            String[] a1 = lines[3].split(":");
            b[0] = Double.parseDouble(a1[0]);
            b[1] = Double.parseDouble(a1[1]);
        }
        if (temp >= 0 || (b[0] >= 0 && b[1] >= 0 && b[0] >= b[1])) {
            plugin.Logger("amount greater than 0", "Debug");
            a = true;
        } else {
            plugin.Logger("amount is smaller than 0! ", "Debug");
        }
        return a;
    }

    public boolean blockIsValid(Sign sign) {
        boolean a = false;
        plugin.Logger("Checking if block is valid!", "Debug");
        double temp = 0;
        try {
            temp = Double.parseDouble(sign.getLine(3));
            plugin.Logger("Line 3 is: " + sign.getLine(3), "Debug");
        } catch (Exception e) {
            plugin.Logger("Contains no amount ", "Debug");
            return false;
        }
        if (temp >= 0) {
            plugin.Logger("amount greater than 0", "Debug");
            a = true;
        } else {
            plugin.Logger("amount is smaller than 0! ", "Debug");
        }

        return a;
    }

    public int countWrittenBooks(Inventory inv) {
        int a = 0;
        for (ItemStack i : inv.getContents()) {
            if (i != null) {
                if (i.getType().equals(Material.WRITTEN_BOOK)) {
                    plugin.Logger("Item " + i.getType().name() + " Slotid: " + inv.contains(i), "Debug");
                    a++;
                }
            }
        }
        return a;
    }
}
