package me.ibhh.BookShop.BookHandler;

import java.util.List;
import me.ibhh.BookShop.BookShop;
import me.ibhh.BookShop.InvalidBookException;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a CraftWrittenBook
 */
public class BookHandlerUtility {

    private BookHandler132 handler132;
    private BookHandler145 handler145;

    public BookHandlerUtility(String title, String author, List<String> pages, int selled) throws InvalidBookException  {
        if(BookShop.getRawBukkitVersion().equalsIgnoreCase("1.4.5-R0.3")){
            handler145 = new BookHandler145(title, author, pages, selled);
        } else {
            handler132 = new BookHandler132(title, author, pages, selled);
        }
    }
    
    public BookHandlerUtility(ItemStack itemStack) throws InvalidBookException {
        if(BookShop.getRawBukkitVersion().equalsIgnoreCase("1.4.5-R0.3")){
            handler145 = new BookHandler145(itemStack);
        } else {
            handler132 = new BookHandler132(itemStack);
        }
    }
    
    public BookHandler getBookHandler(){
        if (handler132 != null) {
            return handler132;
        } else if(handler145 != null){
            return handler145;
        } else {
            return null;
        }
    }

    public void setBookHandler(BookHandler handler) {
        if(BookShop.getRawBukkitVersion().equalsIgnoreCase("1.4.5")){
            handler145 = (BookHandler145) handler;
        } else if(BookShop.getRawBukkitVersion().equalsIgnoreCase("1.3")){
            handler132 = (BookHandler132) handler;
        }
    }
}