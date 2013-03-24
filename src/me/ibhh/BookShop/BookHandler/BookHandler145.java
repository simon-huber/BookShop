
package me.ibhh.BookShop.BookHandler;

import java.util.ArrayList;
import java.util.List;
import me.ibhh.BookShop.InvalidBookException;
import net.minecraft.server.v1_4_5.NBTTagCompound;
import net.minecraft.server.v1_4_5.NBTTagList;
import net.minecraft.server.v1_4_5.NBTTagString;
import net.minecraft.server.v1_4_5.Item;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_4_5.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Simon
 */
public class BookHandler145 extends BookHandler{

    private NBTTagCompound tag;
    private int selled = 0;
// private String title;
// private String author;
// private ArrayList<String> pages;

    public BookHandler145(String title, String author, List<String> pages, int selled) throws InvalidBookException {
        tag = new NBTTagCompound();
        setTitle(title);
        setAuthor(author);
        setPages(pages);
        setSelled(selled);
// this.title = title;
// this.author = author;
// this.pages = pages;
    }

    public BookHandler145(ItemStack itemStack) throws InvalidBookException {
        this((CraftItemStack) itemStack);
    }

    public BookHandler145(CraftItemStack itemStack) throws InvalidBookException {

        if (itemStack.getTypeId() != 387) {
            throw new InvalidBookException("The book must be a written book!");
        }
        tag = CraftItemStack.asNMSCopy(itemStack).tag;
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

    @Override
    public String getTitle() {
        return tag.getString("title");
    }

    @Override
    public String getAuthor() {
        return tag.getString("author");
    }

    @Override
    public ArrayList<String> getPages() {
        ArrayList<String> out = new ArrayList<String>();

        NBTTagList pages = tag.getList("pages");

        for (int i = 0; i < pages.size(); i++) {
            out.add(((NBTTagString) pages.get(i)).data);
        }

        return out;
    }

    @Override
    public void setTitle(String title) {
        tag.setString("title", title);
// this.title = title;
    }

    @Override
    public void setAuthor(String author) {
        tag.setString("author", author);
// this.author = author;
    }

    @Override
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

    @Override
    public boolean unsign() {
        if (tag.get("author") == null || tag.getString("title") == null) {
            return false;
        }
        tag.setString("author", null);
        tag.setString("title", null);
        return true;
    }

    @Override
    public ItemStack toItemStack(int amount) throws InvalidBookException {
        net.minecraft.server.v1_4_5.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(Material.WRITTEN_BOOK));
        nmsStack.setTag(tag);
//Zurück umwandeln
        return CraftItemStack.asCraftMirror(nmsStack);
    }
    
    @Override
    public int selled(){
        return selled;
    }
    
    @Override
    public int getSelled(){
        return selled;
    }
    
    @Override
    public int increaseSelled(){
        return ++selled;
    }
    
    @Override
    public void setSelled(int i){
        selled = i;
    }
}
