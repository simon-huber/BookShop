/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Simon
 */
public class ChestHandler {

    private BookShop plugin;
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
}
