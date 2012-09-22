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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Simon
 */
public class ChestHandler {

    private BookShop plugin;
    private static final BlockFace[] DPChestFaces = {BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public ChestHandler(BookShop pl) {
        plugin = pl;
    }

    public boolean isEmpty(Inventory inv) {
        ItemStack[] i = inv.getContents();
        boolean a = true;
        if (i != null) {
            for (ItemStack item : i) {
                if (item != null) {
                    if (item.getAmount() > 0) {
                        a = false;
                    }
                }
            }
        }
        return a;
    }

    public boolean isChest(Block block) {
        return block.getState() instanceof Chest;
    }

    public boolean isSign(Block block) {
        return block.getState() instanceof Sign;
    }

    public int isProtectedChest(Block block, Player player, String admin) {
        int[] a = new int[5];
        int i = 0;
        for (BlockFace bf : DPChestFaces) {
            Block faceBlock = block.getRelative(bf);
            if (isChest(faceBlock)) {
                a[i] = BookChestInteract(faceBlock, admin, player);
            }
            i++;
        }
        if(containInt(a, -1)){
            plugin.Logger("Not allowed to interact with shop: " + player.getName(), "Debug");
            return -1;
        } else if(containInt(a, 1)){
            plugin.Logger("Is allowed to interact with shop: " + player.getName(), "Debug");
            return 1;
        } else {
            plugin.Logger("No shop found: " + player.getName(), "Debug");
            return 0;
        }
    }

    private boolean containInt(int[] i, int searched) {
        boolean a = false;
        for (int integer : i) {
            if (integer == searched) {
                return true;
            }
        }
        return a;
    }

    private int BookChestInteract(Block block, String admin, Player player) {
        if (block.getRelative(BlockFace.UP) != null) {
            plugin.Logger("Block over chest != null", "Debug");
            if (isSign(block.getRelative(BlockFace.UP))) {
                plugin.Logger("Is sign", "Debug");
                Sign sign = (Sign) block.getRelative(BlockFace.UP).getState();
                if (sign.getLine(0).equalsIgnoreCase(plugin.SHOP_configuration.getString("FirstLineOfEveryShop"))) {
                    plugin.Logger("Is bookshop", "Debug");
                    if (sign.getLine(1).equalsIgnoreCase(player.getName())) {
                        plugin.Logger("Is owner of the shop", "Debug");
                        return 1;
                    } else {
                        if (plugin.PermissionsHandler.checkpermissions(player, admin)) {
                            plugin.Logger("Has Adminpermission to open chest: " + admin, "Debug");
                            return 1;
                        } else {
                            plugin.Logger("Has no Adminpermission to open chest: " + admin, "Debug");
                            return -1;
                        }
                    }
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Sign signAbove(Block block) {
        Sign ret = null;
        if (block != null) {
            if (block.getType().equals(Material.CHEST)) {
                if (block.getRelative(BlockFace.UP).getState() instanceof Sign) {
                    Sign sign = (Sign) block.getRelative(BlockFace.UP).getState();
                    if (sign.getLine(0).equalsIgnoreCase(plugin.SHOP_configuration.getString("FirstLineOfEveryShop"))) {
                        ret = sign;
                    }
                }
            }
        }
        return ret;
    }
}
