/*
 * To change this template, choose Tools145 | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop;

import me.ibhh.BookShop.BookHandler.BookHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ibhh.BookShop.BookHandler.BookHandlerUtility;
import me.ibhh.BookShop.Tools.ToolUtility;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Simon
 */
public class SignHandler {

    private BookShop plugin;
    private static final BlockFace[] shopFaces = {BlockFace.SELF, BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public SignHandler(BookShop pl) {
        plugin = pl;
    }

    public boolean isChest(Block block) {
        boolean a = false;
        if (block.getType().equals(Material.CHEST)) {
            a = true;
        }
        return a;
    }

    public void Create(SignChangeEvent event) {
        Player p = event.getPlayer();
        String[] line = event.getLines();
        plugin.Logger("First Line [BookShop]", "Debug");
        try {
            if (plugin.ListenerShop.blockIsValid(line, "create", p)) {
                plugin.Logger("Createing Shop: ", "Debug");
                plugin.Logger("Line 1: " + line[0], "Debug");
                plugin.Logger("Line 2: " + line[1], "Debug");
                plugin.Logger("Line 3: " + line[2], "Debug");
                plugin.Logger("Line 4: " + line[3], "Debug");
                plugin.Logger("Sign is valid", "Debug");
                if (p.getName().length() >= 16) {
                    plugin.PlayerLogger(event.getPlayer(), "BookShop creation failed! Username too long!", "Error");
                    event.setCancelled(true);
                    return;
                }
                plugin.Logger("First line != null", "Debug");
                if (event.getLine(1).equalsIgnoreCase(plugin.SHOP_configuration.getString("AdminShop"))) {
                    if (!plugin.PermissionsHandler.checkpermissions(p, "BookShop.create.admin")) {
                        plugin.PlayerLogger(event.getPlayer(), "BookShop creation failed!", "Error");
                        event.setCancelled(true);
                        return;
                    }
                } else if (event.getLine(1).equalsIgnoreCase(plugin.SHOP_configuration.getString("Newspapers"))) {
                    if (!plugin.PermissionsHandler.checkpermissions(p, "BookShop.create.admin")) {
                        plugin.PlayerLogger(event.getPlayer(), "BookShop creation failed!", "Error");
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    if (!plugin.PermissionsHandler.checkpermissions(p, "BookShop.create")) {
                        plugin.PlayerLogger(event.getPlayer(), "BookShop creation failed!", "Error");
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getLine(1).equalsIgnoreCase(event.getPlayer().getName()) || event.getLine(1).equalsIgnoreCase("")) {
                        event.setLine(1, event.getPlayer().getName());
                    } else if (!plugin.PermissionsHandler.checkpermissions(p, "BookShop.create.other")) {
                        plugin.PlayerLogger(event.getPlayer(), "BookShop creation failed!", "Error");
                    }
                }
                if (!isChest(event.getBlock().getRelative(BlockFace.DOWN))) {
                    plugin.PlayerLogger(p, plugin.getConfig().getString("Shop.error.nochest." + plugin.config.language), "Error");
                    event.setCancelled(true);
                    return;
                }
                Block chestblock = event.getBlock().getRelative(BlockFace.DOWN);
                Chest chest = (Chest) chestblock.getState();
                if (!plugin.ListenerShop.chestHandler.isEmpty(chest.getInventory())) {
                    plugin.PlayerLogger(p, plugin.getConfig().getString("Shop.error.nochest." + plugin.config.language), "Error");
                    event.setCancelled(true);
                    return;
                }
                if (!event.getLine(1).equalsIgnoreCase(plugin.SHOP_configuration.getString("Newspapers")) && event.getLine(1).equalsIgnoreCase(plugin.SHOP_configuration.getString("AdminShop"))) {
                    if (plugin.ListenerShop.getPrice(event.getLine(3), event.getPlayer(), false) < plugin.getConfig().getDouble("BookBaseCost")) {
                        if (!event.getLine(1).equalsIgnoreCase(plugin.SHOP_configuration.getString("AdminShop")) || !event.getLine(1).equalsIgnoreCase(plugin.SHOP_configuration.getString("Newspapers"))) {
                            boolean a = false;
                            String[] c = new String[2];
                            try {
                                double b = Double.parseDouble(event.getLine(3));
                                a = true;
                            } catch (Exception e) {
                                c = event.getLine(3).split(":");
                            }
                            if (plugin.getConfig().getBoolean("useBookandQuill")) {
                                if (!a) {
                                    event.setLine(3, plugin.getConfig().getString("BookBaseCost") + ":" + ((int) (plugin.getConfig().getDouble("BookBaseCost") - (plugin.getConfig().getDouble("BookBaseCost") * 0.3))));
                                } else {
                                    event.setLine(3, plugin.getConfig().getString("BookBaseCost"));
                                }
                            }
                            plugin.PlayerLogger(p, String.format(plugin.getConfig().getString("Shop.error.basecost." + plugin.config.language), plugin.getConfig().getString("BookBaseCost")), "Warning");
                        }
                    }
                }
                if (line[1].equalsIgnoreCase(plugin.SHOP_configuration.getString("AdminShop"))) {
                    MTLocation loc = MTLocation.getMTLocationFromLocation(event.getBlock().getLocation());
                    if (!plugin.metricshandler.AdminShop.containsKey(loc)) {
                        plugin.metricshandler.AdminShop.put(loc, event.getPlayer().getName());
                        plugin.Logger("Added AdminShop to list!", "Debug");
                    }
                } else {
                    MTLocation loc = MTLocation.getMTLocationFromLocation(event.getBlock().getLocation());
                    if (!plugin.metricshandler.Shop.containsKey(loc)) {
                        plugin.metricshandler.Shop.put(loc, event.getPlayer().getName());
                        plugin.Logger("Added Shop to list!", "Debug");
                    }
                }
                if (plugin.getConfig().getBoolean("ShopCreateMessage")) {
                    plugin.playerManager.BroadcastMsg("BookShop.admin", "The player " + p.getName() + " created a BookShop: " + p.getLocation());
                }
                if (plugin.getConfig().getBoolean("useBookandQuill")) {
                    plugin.PlayerLogger(p, plugin.getConfig().getString("Shop.success.books." + plugin.config.language), "Warning");
                }
                plugin.PlayerLogger(event.getPlayer(), plugin.getConfig().getString("Shop.success.create." + plugin.config.language), "");
                if (plugin.getConfig().getInt("tax") != 0) {
                    plugin.PlayerLogger(event.getPlayer(), String.format(plugin.getConfig().getString("Shop.success.tax." + plugin.config.language), plugin.getConfig().getInt("tax")), "Warning");
                }
            } else {
                plugin.Logger("Sign is not valid", "Debug");
                plugin.PlayerLogger(event.getPlayer(), "BookShop creation failed!", "Error");
                event.setCancelled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getReportHandler().report(3336, "Error BookShop create", e.getMessage(), "SignHandler", e);
            event.setCancelled(true);
            plugin.PlayerLogger(event.getPlayer(), "BookShop creation failed!", "Error");
        }
    }

    public void LinksKlick(PlayerInteractEvent event, String[] line, Player p, Sign s) {
        BookShopSignLinks(event, line, p, s);
    }

    public void BookShopSignLinks(PlayerInteractEvent event, String[] line, Player p, Sign s) {
        if (plugin.config.debug) {
            plugin.Logger(" first line [BookShop] and leftklick!", "Debug");
        }
        try {
            plugin.Logger(" not blacklisted!", "Debug");
            if (plugin.ListenerShop.blockIsValid(line, "Interact", p)) {
                plugin.Logger("LinksShop: ", "Debug");
                plugin.Logger("Line 1: " + line[0], "Debug");
                plugin.Logger("Line 2: " + line[1], "Debug");
                plugin.Logger("Line 3: " + line[2], "Debug");
                plugin.Logger("Line 4: " + line[3], "Debug");
                plugin.Logger(" Block is valid!", "Debug");
                Player player = event.getPlayer();
                MTLocation loc = MTLocation.getMTLocationFromLocation(event.getClickedBlock().getLocation());
                if (line[1].equalsIgnoreCase(plugin.SHOP_configuration.getString("AdminShop"))) {
                    if (!plugin.metricshandler.AdminShop.containsKey(loc)) {
                        plugin.metricshandler.AdminShop.put(loc, event.getPlayer().getName());
                        plugin.Logger("Added AdminShop to list!", "Debug");
                    }
                } else {
                    if (!plugin.metricshandler.Shop.containsKey(loc)) {
                        plugin.metricshandler.Shop.put(loc, event.getPlayer().getName());
                        plugin.Logger("Added Shop to list!", "Debug");
                    }
                }
                if (plugin.PermissionsHandler.checkpermissions(p, "BookShop.use")) {
                    plugin.Logger("Player: " + p.getName() + " has the permission: BookShop.use", "Debug");
                    BookShopSignBuy(player, line, s);
                }
            }
        } catch (Exception e) {
            plugin.getReportHandler().report(3337, "Error BookShopSignLinks", e.getMessage(), "SignHandler", e);
        }
    }

    public void BookShopSignBuy(Player player, String[] line, Sign s) {
        String playername = player.getName();
        plugin.Logger("ShopBuy: ", "Debug");
        plugin.Logger("Line 1: " + line[0], "Debug");
        plugin.Logger("Line 2: " + line[1], "Debug");
        plugin.Logger("Line 3: " + line[2], "Debug");
        plugin.Logger("Line 4: " + line[3], "Debug");
        try {
            if (!playername.equalsIgnoreCase(line[1])) {
                if (player.getInventory().firstEmpty() != -1) {
                    if (line[1].equalsIgnoreCase(plugin.SHOP_configuration.getString("AdminShop"))) {
                        Chest chest = null;
                        try {
                            chest = (Chest) s.getBlock().getRelative(BlockFace.DOWN).getState();
                        } catch (Exception e) {
                        }
                        if (chest != null) {
                            if (chest.getInventory().contains(Material.WRITTEN_BOOK)) {
                                int Slot = chest.getInventory().first(Material.WRITTEN_BOOK);
                                ItemStack item = chest.getInventory().getItem(Slot);
                                if (item != null) {
                                    double price = 0;
                                    price = plugin.ListenerShop.getPrice(s, player, false);
                                    if (price >= 0) {
                                        if ((plugin.MoneyHandler.getBalance(player) - price) >= 0) {
                                            BookHandler bookInChest = new BookHandlerUtility(item).getBookHandler();
                                            BookHandler loadedBook = BookLoader.load(plugin, bookInChest.getAuthor(), bookInChest.getTitle());
                                            if (loadedBook != null) {
                                                loadedBook.increaseSelled();
                                                BookLoader.save(plugin, loadedBook);
                                            } else {
                                                bookInChest.increaseSelled();
                                                BookLoader.save(plugin, bookInChest);
                                            }
                                            plugin.MoneyHandler.substract(price, player);
                                            player.getInventory().addItem(item.clone());
                                            plugin.PlayerLogger(player, String.format(plugin.config.Shopsuccessbuy, s.getLine(2), s.getLine(1), price), "");
                                            player.updateInventory();
                                            player.saveData();
                                            plugin.metricshandler.BookShopAdminSignBuy++;
                                        } else {
                                            plugin.PlayerLogger(player, plugin.config.Shoperrornotenoughmoneyconsumer, "Error");
                                        }
                                    } else {
                                        plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.wrongPrice." + plugin.config.language), "Error");
                                    }
                                }
                            } else {
                                plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.nobook." + plugin.config.language), "Error");
                            }
                        } else {
                            plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.nobook." + plugin.config.language), "Error");
                        }
                    } else if (line[1].equalsIgnoreCase(plugin.SHOP_configuration.getString("Newspapers"))) {
                        Chest chest = null;
                        try {
                            chest = (Chest) s.getBlock().getRelative(BlockFace.DOWN).getState();
                        } catch (Exception e) {
                        }
                        if (chest != null) {
                            if (chest.getInventory().contains(Material.WRITTEN_BOOK)) {
                                double price = 0;
                                price = plugin.ListenerShop.getPrice(s, player, false);
                                if (price >= 0) {
                                    if ((plugin.MoneyHandler.getBalance(player) - price) >= 0) {
                                        for (ItemStack item : chest.getInventory().getContents()) {
                                            if (item != null) {
                                                if (item.getType().equals(Material.WRITTEN_BOOK)) {
                                                    BookHandler bookInChest = new BookHandlerUtility(item).getBookHandler();
                                                    BookHandler loadedBook = BookLoader.load(plugin, bookInChest.getAuthor(), bookInChest.getTitle());
                                                    if (loadedBook != null) {
                                                        loadedBook.increaseSelled();
                                                        BookLoader.save(plugin, loadedBook);
                                                    } else {
                                                        bookInChest.increaseSelled();
                                                        BookLoader.save(plugin, bookInChest);
                                                    }
                                                    player.getInventory().addItem(item.clone());
                                                    plugin.PlayerLogger(player, String.format(plugin.config.Shopsuccessbuy, bookInChest.getTitle(), bookInChest.getAuthor(), 0), "");
                                                }
                                            }
                                        }
                                        plugin.MoneyHandler.substract(price, player);
                                        plugin.PlayerLogger(player, "You purchased the collection for " + price, "");
                                        player.updateInventory();
                                        player.saveData();
                                        plugin.metricshandler.BookShopAdminSignBuy++;
                                    } else {
                                        plugin.PlayerLogger(player, plugin.config.Shoperrornotenoughmoneyconsumer, "Error");
                                    }
                                } else {
                                    plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.wrongPrice." + plugin.config.language), "Error");
                                }
                            }
                        } else {
                            plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.nobook." + plugin.config.language), "Error");
                        }
                    } else {
                        Player empfaenger = ToolUtility.getTools().getmyOfflinePlayer(plugin, line, 1);
                        if (empfaenger != null) {
                            Chest chest = null;
                            try {
                                chest = (Chest) s.getBlock().getRelative(BlockFace.DOWN).getState();
                            } catch (Exception e) {
                            }
                            if (chest != null) {
                                if (chest.getInventory().contains(Material.WRITTEN_BOOK)) {
                                    int Slot = chest.getInventory().first(Material.WRITTEN_BOOK);
                                    ItemStack item = chest.getInventory().getItem(Slot);
                                    if (item != null) {
                                        plugin.Logger(player.getName() + " has Item " + player.getItemInHand().getType().name() + " in the hand!", "Debug");
                                        if (plugin.getConfig().getBoolean("useBookandQuill") && countBooks(chest.getInventory()) == 0) {
                                            if (!player.getItemInHand().getType().equals(Material.BOOK_AND_QUILL) && plugin.getConfig().getBoolean("useBookandQuill")) {
                                                plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.takeBookandQuill." + plugin.config.language), "Error");
                                                return;
                                            }
                                        }
                                        if (plugin.config.getPlayerConfig(empfaenger, player)) {
                                            double price = 0;
                                            if (player.getItemInHand().getType().equals(Material.BOOK_AND_QUILL)) {
                                                price = plugin.ListenerShop.getPrice(s, player, true);
                                            } else {
                                                price = plugin.ListenerShop.getPrice(s, player, false);
                                            }
                                            if (price >= 0) {
                                                if ((plugin.MoneyHandler.getBalance(player) - price) >= 0) {
                                                    if (player.getItemInHand().getType().equals(Material.BOOK_AND_QUILL)) {
                                                        player.getInventory().clear(player.getInventory().getHeldItemSlot());
                                                        player.getInventory().addItem(item.clone());
                                                        player.updateInventory();
                                                        player.saveData();
                                                    } else if (plugin.getConfig().getBoolean("useBookandQuill") && countBooks(chest.getInventory()) > 0) {
                                                        plugin.Logger("Books in Chest", "Debug");
                                                        int Slotbook = chest.getInventory().first(Material.BOOK_AND_QUILL);
                                                        ItemStack itembook = chest.getInventory().getItem(Slotbook);
                                                        if (itembook.getAmount() > 1) {
                                                            itembook.setAmount(itembook.getAmount() - 1);
                                                        } else {
                                                            chest.getInventory().clear(Slotbook);
                                                        }
                                                        player.getInventory().addItem(item.clone());
                                                    } else {
                                                        if (!plugin.getConfig().getBoolean("useBookandQuill")) {
                                                            player.getInventory().addItem(item.clone());
                                                        } else {
                                                            plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.takeBookandQuill." + plugin.config.language), "Error");
                                                            return;
                                                        }
                                                    }
                                                    plugin.MoneyHandler.substract(price, player);
                                                    double amount = price;
                                                    if (plugin.getConfig().getInt("tax") != 0) {
                                                        amount = price * plugin.getConfig().getInt("tax") / 100;
                                                    }
                                                    plugin.MoneyHandler.addmoney(amount, empfaenger);
                                                    plugin.PlayerLogger(player, String.format(plugin.config.Shopsuccessbuy, s.getLine(2), s.getLine(1), price), "");
                                                    player.updateInventory();
                                                    empfaenger.saveData();
                                                    player.saveData();
                                                    BookHandler bookInChest = new BookHandlerUtility(item).getBookHandler();
                                                    BookHandler loadedBook = BookLoader.load(plugin, bookInChest.getAuthor(), bookInChest.getTitle());
                                                    if (loadedBook != null) {
                                                        loadedBook.increaseSelled();
                                                        BookLoader.save(plugin, loadedBook);
                                                    } else {
                                                        bookInChest.increaseSelled();
                                                        BookLoader.save(plugin, bookInChest);
                                                    }
                                                    plugin.metricshandler.BookShopSignBuy++;
                                                    plugin.PlayerLogger(empfaenger, String.format(plugin.config.Shopsuccesssellerbuy, s.getLine(2), playername, price), "");
                                                    if (plugin.getConfig().getInt("tax") != 0) {
                                                        plugin.PlayerLogger(empfaenger, String.format(plugin.getConfig().getString("Shop.success.tax." + plugin.config.language), plugin.getConfig().getInt("tax")), "Warning");
                                                    }
                                                } else {
                                                    plugin.PlayerLogger(player, plugin.config.Shoperrornotenoughmoneyconsumer, "Error");
                                                }
                                            } else {
                                                plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.wrongPrice." + plugin.config.language), "Error");
                                            }
                                        }
                                    } else {
                                        plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.nobook." + plugin.config.language), "Error");
                                    }
                                } else {
                                    plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.nobook." + plugin.config.language), "Error");
                                }
                            } else {
                                plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.nochest2." + plugin.config.language), "Error");
                            }
                        } else {
                            plugin.PlayerLogger(player, line[1] + " " + plugin.config.playerwasntonline, "Error");
                        }
                    }
                } else {
                    plugin.PlayerLogger(player, plugin.getConfig().getString("Shop.error.inventoryfull." + plugin.config.language), "Error");
                }
            } else {
                plugin.PlayerLogger(player, "That is your Shop", "Error");
            }
        } catch (InvalidBookException ex) {
            Logger.getLogger(SignHandler.class.getName()).log(Level.SEVERE, null, ex);
            plugin.PlayerLogger(player, "Something is wrong with this book!", "Error");
            plugin.getReportHandler().report(3338, "Error BookShopSignBuy", ex.getMessage(), "SignHandler", ex);
        }
    }

    public boolean scanAreaforSigns(Block block) {
        boolean a = false;
        for (BlockFace bf : shopFaces) {
            Block faceBlock = block.getRelative(bf);
            if (isSign(faceBlock)) {
                Sign sign = (Sign) faceBlock.getState();
                if (blockIsValid(sign)) {
                    a = true;
                }
            }
        }
        return a;
    }

    public int countBooks(Inventory inv) {
        int a = 0;
        for (ItemStack i : inv.getContents()) {
            if (i != null) {
                if (i.getType().equals(Material.BOOK_AND_QUILL)) {
                    plugin.Logger("Item " + i.getType().name() + " Slotid: " + inv.contains(i), "Debug");
                    a++;
                }
            }
        }
        plugin.Logger("BookandQuill: " + a, "Debug");
        return a;
    }

    public static boolean isSign(Block block) {
        return block.getState() instanceof Sign;
    }

    public boolean blockIsValid(Sign sign) {
        boolean a = false;
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
            if (sign.getLine(0).equalsIgnoreCase("[BookShop]")) {
                a = true;
            }
        } else {
            plugin.Logger("amount is smaller than 0! ", "Debug");
        }
        return a;
    }
}