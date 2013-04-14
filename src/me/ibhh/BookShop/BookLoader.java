package me.ibhh.BookShop;

import java.io.File;
import me.ibhh.BookShop.intern.BukkitBuildNOTSupportedException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookLoader {

    public static ItemStack load(BookShop plugin, String author, String name) {
        try {
            BookFile book = ObjectManager.load(plugin.getDataFolder() + File.separator + "books" + File.separator + author + " - " + name + ".txt");
            plugin.Logger("Book " + book.getTitle() + " by " + book.getAuthor() + " loaded!", "Debug");
            return BookFileToBookHandler(book);
        } catch (Exception e) {
            plugin.Logger("Cannot load Book statistics!", "Debug");
            if (plugin.config.debug) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ItemStack load(BookShop plugin, String filename) {
        try {
            BookFile book = ObjectManager.load(plugin.getDataFolder() + File.separator + "books" + File.separator + filename);
            plugin.Logger("Book " + book.getTitle() + " by " + book.getAuthor() + " loaded!", "Debug");
            return BookFileToBookHandler(book);
        } catch (Exception e) {
            plugin.Logger("Cannot load Book statistics!", "Debug");
            if (plugin.config.debug) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean save(BookShop plugin, ItemStack book) {
        try {
            String path = plugin.getDataFolder() + File.separator + "books" + File.separator;
            File pathFile = new File(path);
            pathFile.mkdirs();
            BookMeta bm = (BookMeta) book.getItemMeta();
            ObjectManager.save(BookHandlerToBookFile(book), path + bm.getAuthor() + " - " + bm.getTitle() + ".txt");
            plugin.Logger("Book " + bm.getTitle() + " by " + bm.getAuthor() + " saved!", "Debug");
            return true;
        } catch (Exception e) {
            plugin.Logger("Cannot save book!", "Debug");
            if (plugin.config.debug) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static void delete(BookShop plugin, ItemStack book) {
        if (book != null) {
            BookMeta bm = (BookMeta) book.getItemMeta();
            String path = plugin.getDataFolder() + File.separator + "books" + File.separator;
            File bookfile = new File(path + bm.getAuthor() + " - " + bm.getTitle() + ".txt");
            if (bookfile.exists()) {
                bookfile.delete();
            }
        }
    }

    public static ItemStack BookFileToBookHandler(BookFile file) throws BukkitBuildNOTSupportedException {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setAuthor(file.getAuthor());
        bm.setTitle(file.getTitle());
        bm.setPages(file.getPages());
        book.setItemMeta(bm);
        return book;
    }

    public static BookFile BookHandlerToBookFile(ItemStack book) {
        BookMeta bm = (BookMeta) book.getItemMeta();
        return new BookFile(bm.getTitle(), bm.getAuthor(), bm.getPages(), 0);
    }
}
