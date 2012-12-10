package me.ibhh.BookShop;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Simon
 */
public class BookLoader {

    public static BookHandler load(BookShop plugin, String author, String name) {
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

    public static BookHandler load(BookShop plugin, String filename) {
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

    public static boolean save(BookShop plugin, BookHandler book) {
        try {
            String path = plugin.getDataFolder() + File.separator + "books" + File.separator;
            File pathFile = new File(path);
            BookFile FileToSave = BookHandlerToBookFile(book);
            pathFile.mkdirs();
            ObjectManager.save(FileToSave, path + book.getAuthor() + " - " + book.getTitle() + ".txt");
            plugin.Logger("Book " + book.getTitle() + " by " + book.getAuthor() + " saved!", "Debug");
            return true;
        } catch (Exception e) {
            plugin.Logger("Cannot save Shop statistics!", "Debug");
            if (plugin.config.debug) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static void delete(BookShop plugin, BookHandler book) {
        if (book != null) {
            String path = plugin.getDataFolder() + File.separator + "books" + File.separator;
            File bookfile = new File(path + book.getAuthor() + " - " + book.getTitle() + ".txt");
            if (bookfile.exists()) {
                bookfile.delete();
            }
        }
    }

    public static BookHandler BookFileToBookHandler(BookFile file) {
        try {
            return new BookHandler(file.getTitle(), file.getAuthor(), file.getPages(), file.getSelled());
        } catch (InvalidBookException ex) {
            Logger.getLogger(BookLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static BookFile BookHandlerToBookFile(BookHandler book) {
        return new BookFile(book.getTitle(), book.getAuthor(), book.getPages(), book.getSelled());
    }
}
