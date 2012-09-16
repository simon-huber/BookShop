package me.ibhh.BookShop;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Simon
 */
public class BookFile implements Serializable {
    private String author, title;
    private ArrayList<String> pages;
    private int selled;

    public BookFile(String title, String author, ArrayList<String> pages, int selled) {
        this.author = author;
        this.title = title;
        this.pages = pages;
        this.selled = selled;
    }
    
    public String getAuthor(){
        return author;
    }
    public String getTitle(){
        return title;
    }
    public ArrayList<String> getPages(){
        return pages;
    }
    public int getSelled(){
        return selled;
    }
}
