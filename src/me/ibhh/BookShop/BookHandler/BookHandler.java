package me.ibhh.BookShop.BookHandler;

import java.util.ArrayList;
import java.util.List;
import me.ibhh.BookShop.InvalidBookException;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a CraftWrittenBook
 */
public abstract class BookHandler {

    public abstract String getTitle();

    public abstract String getAuthor();
    
    public abstract ArrayList<String> getPages();

    public abstract void setTitle(String title);

    public abstract void setAuthor(String author);
    
    public abstract void setPages(List<String> pages) throws InvalidBookException;

    public abstract boolean unsign();

    public abstract ItemStack toItemStack(int amount) throws InvalidBookException;
    
    public abstract int selled();
    
    public abstract int getSelled();
    
    public abstract int increaseSelled();
    
    public abstract void setSelled(int i);
}