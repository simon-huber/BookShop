/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.BookShop;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a CraftWrittenBook
 */
public class BookHandler {

    private NBTTagCompound tag;
// private String title;
// private String author;
// private ArrayList<String> pages;

    public BookHandler(String title, String author, List<String> pages) throws InvalidBookException {
        tag = new NBTTagCompound();
        setTitle(title);
        setAuthor(author);
        setPages(pages);
// this.title = title;
// this.author = author;
// this.pages = pages;
    }

    public BookHandler(ItemStack itemStack) throws InvalidBookException {
        this((CraftItemStack) itemStack);
    }

    public BookHandler(CraftItemStack itemStack) throws InvalidBookException {

        if (itemStack.getTypeId() != 387) {
            throw new InvalidBookException("The book must be a written book!");
        }
        tag = itemStack.getHandle().getTag();
        if (tag == null) {
            System.out.print("nul nulldskfndlf");
            tag = new NBTTagCompound();
        }

// author = tag.getString("author");
// title = tag.getString("title");
//
// NBTTagList pages = tag.getList("pages");
// ArrayList<String> realPages = new ArrayList<String>();
//
// for (int i = 0; i < pages.size(); i++) {
// String page = pages.get(i).getName();
// if (page.length() > 256) {
// throw new InvalidBookException("The maximum size of a page is 256!");
// }
// realPages.add(page);
// }
//
// this.pages = realPages;
    }

    public String getTitle() {
        return tag.getString("title");
    }

    public String getAuthor() {
        return tag.getString("author");
    }

    public ArrayList<String> getPages() {
        ArrayList<String> out = new ArrayList<String>();

        NBTTagList pages = tag.getList("pages");

        for (int i = 0; i < pages.size(); i++) {
            out.add(((NBTTagString) pages.get(i)).data);
        }

        return out;
    }

    public void setTitle(String title) {
        tag.setString("title", title);
// this.title = title;
    }

    public void setAuthor(String author) {
        tag.setString("author", author);
// this.author = author;
    }

    public void setPages(List<String> pages) throws InvalidBookException {
        NBTTagList list = new NBTTagList();
        for (String page : pages) {

            if (page.length() > 256) {
                throw new InvalidBookException("The lenght of a page is too long!");
            }

            NBTTagString nbtPage = new NBTTagString(page);
            nbtPage.data = page;

            list.add(nbtPage);
        }
        tag.set("pages", list);
// this.pages = pages;
    }

    public boolean unsign() {
        if (tag.get("author") == null || tag.getString("title") == null) {
            return false;
        }

        tag.remove("author");
        tag.remove("title");

        return true;
    }

    public ItemStack toItemStack(int amount) throws InvalidBookException {
        CraftItemStack item = new CraftItemStack(Material.WRITTEN_BOOK, amount);
// NBTTagCompound newBookData = new NBTTagCompound();
//
// newBookData.setString("author", this.getAuthor());
// newBookData.setString("title", this.getTitle());
//
// NBTTagList pages = new NBTTagList();
//
// List<String> bookPages = this.getPages();
//
// for (int i = 0; i < bookPages.size(); i++) {
// String page = bookPages.get(i);
// if (page.length() > 256) {
// throw new InvalidBookException("The maximum size of a page is 256!");
// }
// pages.add(new NBTTagString(String.valueOf(i), page));
// }
//
// newBookData.set("pages", pages);

        item.getHandle().tag = tag;

        return item;
    }
}