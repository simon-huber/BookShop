/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop.Tools;

import me.ibhh.BookShop.BookShop;

/**
 *
 * @author Simon
 */
public class ToolUtility {
    public static Tools getTools(){
        if(BookShop.getRawBukkitVersion().equalsIgnoreCase("1.4.5-R0.3")){
            return new Tools145();
        } else {
            return new Tools132();
        }
    }
}
