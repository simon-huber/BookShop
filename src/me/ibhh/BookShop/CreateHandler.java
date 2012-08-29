/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop;

import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author Simon
 */
public class CreateHandler {

    //define variable
    private BookShop plugin;
    private SignHandler signHandler;

    /**
     * Konstruktor
     *
     * @param pl
     */
    public CreateHandler(BookShop pl) {
        plugin = pl;
        signHandler = new SignHandler(pl);
    }

    /**
     * Manages the creation of shops
     *
     * @param event
     */
    public void CreateBookShop(SignChangeEvent event) {
        plugin.Logger("Creating BookShop!", "Debug");
        signHandler.Create(event);
    }
}
