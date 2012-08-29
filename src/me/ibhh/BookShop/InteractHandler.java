/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Simon
 */
public class InteractHandler {

    private BookShop plugin;
    private SignHandler signHandler;

    /**
     * Konstruktor of InteractHandler
     *
     * @param pl
     */
    public InteractHandler(BookShop pl) {
        plugin = pl;
        signHandler = new SignHandler(pl);
    }

    /**
     * Handles playerinteracts
     *
     * @param event
     */
    public void InteracteventHandler(PlayerInteractEvent event) {
        if (!plugin.toggle) {
            Player p = event.getPlayer();
            if (plugin.config.debug) {
                plugin.Logger("A interact Event dected by player: " + p.getName(), "Debug");
            }
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (plugin.getConfig().getBoolean("LEFT_CLICK_buy")) {
                    try {
                        LeftInteract(event);
                    } catch (Exception e) {
                        plugin.report.report(3335, "Error on leftInteract", e.getMessage(), "InteractHandler", e.getCause());
                    }
                }
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                try {
                    if (isChest(event.getClickedBlock())) {
                        Sign s = signAbove(event);
                        if (s != null) {
                            if (!isAllowedtoOpenChest(p, s)) {
                                plugin.PlayerLogger(p, plugin.getConfig().getString("Shop.error.notyourshop." + plugin.config.language), "Warning");
                                event.setCancelled(true);
                            } else {
                                plugin.ListenerShop.ChestViewers.put(p, (Chest) event.getClickedBlock().getState());
                            }
                        }
                    }
                } catch (Exception e) {
                    plugin.report.report(3336, "Error on RightInteract", e.getMessage(), "InteractHandler", e.getCause());
                }
                if (!plugin.getConfig().getBoolean("LEFT_CLICK_buy")) {
                    try {
                        LeftInteract(event);
                    } catch (Exception e) {
                        plugin.report.report(3335, "Error on leftInteract", e.getMessage(), "InteractHandler", e.getCause());
                    }
                }
            }
        }
    }

    /**
     * Manages leftklickinteracts
     *
     * @param event
     */
    public void LeftInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (plugin.config.debug) {
            plugin.Logger("A left interact Event dected by player: " + p.getName(), "Debug");
        }
        if ((event.hasBlock()) && ((event.getClickedBlock().getState() instanceof Sign)) && (!p.isSneaking())) { // && !(p.isSneaking())
            Sign s = (Sign) event.getClickedBlock().getState();
            String[] line = s.getLines();
            if (plugin.config.debug) {
                plugin.Logger("Checking first line!", "Debug");
            }
            if (line[0].equalsIgnoreCase("[BookShop]")) {
                signHandler.LinksKlick(event, line, p, s);
            }
        }
    }

    public boolean isChest(Block block) {
        boolean a = false;
        if (block.getType().equals(Material.CHEST)) {
            a = true;
        }
        return a;
    }

    public Sign signAbove(PlayerInteractEvent event) {
        Sign ret = null;
        if (event.hasBlock()) {
            if (event.getClickedBlock() != null) {
                if (event.getClickedBlock().getType().equals(Material.CHEST)) {
                    Block chestblock = event.getClickedBlock();
                    if (chestblock.getRelative(BlockFace.UP).getState() instanceof Sign) {
                        Sign sign = (Sign) chestblock.getRelative(BlockFace.UP).getState();
                        if (sign.getLine(0).equalsIgnoreCase("[BookShop]")) {
                            ret = sign;
                        }
                    }
                }
            }
        }
        return ret;
    }

    public boolean isAllowedtoOpenChest(Player player, Sign sign) {
        boolean a = false;
        if (sign.getLine(0).equalsIgnoreCase("[BookShop]")) {
            if (sign.getLine(1).equalsIgnoreCase(player.getName())) {
                a = true;
            } else if (plugin.PermissionsHandler.checkpermissionssilent(player, "BookShop.admin")) {
                a = true;
            }
        }
        return a;
    }
}
