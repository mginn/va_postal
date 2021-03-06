package com.vodhanel.minecraft.va_postal.mail;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class Book {
    VA_postal plugin;
    private String author;
    private String title;
    private String[] pages;
    private ItemStack itemstack;
    private BookMeta bookData;
    private boolean new_itemstack = false;
    private boolean valid_book = true;
    private List lpages;

    public Book(VA_postal instance) {
        plugin = instance;
    }

    public Book(ItemStack bookItem) {
        new_itemstack = false;
        itemstack = bookItem;
        bookData = ((BookMeta) itemstack.getItemMeta());
        try {
            author = bookData.getAuthor();
            title = bookData.getTitle();
            lpages = bookData.getPages();
        } catch (Exception e) {
            valid_book = false;
        }

        if (valid_book) {
            Object[] sPages = lpages.toArray();
            pages = new String[sPages.length];
            for (int i = 0; i < sPages.length; i++) {
                pages[i] = sPages[i].toString();
            }
        }
    }

    public Book(String title, String author, String[] pages) {
        new_itemstack = true;
        this.title = title;
        this.author = author;
        this.pages = pages;
    }

    public static String makeFirstMailPage(String town, String address, String attention, String mf1, String mf2, String author, String title, String fdate, Player Author, Player Attention) {
        String spage = "";
        spage += "§7§oTo:\n";
        spage += "§c  " + town + "\n";
        spage += "§9  " + address + "\n";
        spage += "§7§oAttention:\n";
        spage += "§2  " + attention + "\n";
        spage += "§7§oMailed from:\n";
        spage += "§7  " + (mf1 == null || mf1.isEmpty() ? "[not-processed]" : mf1) + "\n";
        spage += "§7  " + (mf2 == null || mf2.isEmpty() ? "[not-processed]" : mf2) + "\n";
        spage += "§7§oWritten by:\n";
        spage += "§8  " + author + "\n";
        spage += "§8  " + title + "\n";
        spage += "\n";
        spage += "§7" + fdate + "\n";
        spage += (Author != null ? Author.getUniqueId() : "") + "\n";
        spage += (Attention != null ? Attention.getUniqueId() : "") + "\n";
        
        Util.cinform(spage);
        
        return spage;
    }

    public String getAuthor() {
        if (author == null) {
            return "null";
        }
        return author;
    }

    public void setAuthor(String sAuthor) {
        author = sAuthor;
        if (!new_itemstack) {
            bookData.setAuthor(sAuthor);
        }
    }

    public String getTitle() {
        if (title == null) {
            return "null";
        }
        return title;
    }

    public boolean setTitle(String title) {
        this.title = title;
        if (!new_itemstack) {
            bookData.setTitle(title);
        }
        return true;
    }

    public int getPagesSize(int page) {
        int result = -1;
        String spage = getPage(page);
        if (spage.trim().length() > 0) {
            result = spage.trim().length();
        }
        return result;
    }

    public int getLineCount(int page) {
        String spage = getPage(page);
        String[] parts = spage.split("\n");
        return parts.length;
    }

    public int getPagesCount() {
        return pages.length;
    }

    public String[] getPages() {
        return pages;
    }

    public String[] getPages_with_blank_last_page() {
        String[] new_array = new String[pages.length + 1];

        for (int i = 0; i < pages.length; i++) {
            new_array[i] = pages[i];
        }
        new_array[(new_array.length - 1)] = "";
        return new_array;
    }

    public String[] getPages_with_blank_first_page() {
        String[] new_array = new String[pages.length + 1];
        new_array[0] = "";
        for (int i = 0; i < pages.length; i++) {
            new_array[(i + 1)] = pages[i];
        }
        return new_array;
    }

    public String getPage(int page) {
        int index = page - 1;
        if (index < pages.length) {
            return pages[index];
        }
        return "null";
    }

    public static String makeFirstLogPage(String sworld, String slocation_mod, String saddress, String sowner, Player owner) {
        String p1 = sworld + "\n";
        p1 += slocation_mod + "\n";
        p1 += saddress + "\n";
        p1 += sowner + "\n";
        p1 += "§2" + "[" + Util.stime_stamp() + "]\n";
        p1 += "\n\n\n\n\n\n\n\n" + (owner != null ? owner.getUniqueId() : "");
        return p1;
    }

    public boolean is_valid() {
        if (!valid_book) {
            return false;
        }
        if (title == null) {
            return false;
        }
        if (title.isEmpty()) {
            return false;
        }
        if (author == null) {
            return false;
        }
        if (author.isEmpty()) {
            return false;
        }
        if (pages == null) {
            return false;
        }
        return pages.length != 0;
    }

    public boolean setPage(int page, String text) {
        int index = page;

        if (index > pages.length) {
            String[] sPages = new String[index];
            for (int i = 0; i < sPages.length; i++) {
                if (i < pages.length) {
                    sPages[i] = pages[i];
                    if (!new_itemstack) {
                        bookData.setPage(i, pages[i]);
                    }
                } else {
                    sPages[i] = " ";
                    if (!new_itemstack) {
                        bookData.addPage(" ");
                    }
                }
            }

            pages = sPages;
            sPages = null;
        } else {
            pages[index] = text;
            if (!new_itemstack) {
                bookData.setPage(index, text);
            }
        }
        return true;
    }

    public ItemStack generateItemStack() {
        if (!new_itemstack) {
            return itemstack;
        }

        ItemStack newbook = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta newBookData = (BookMeta) newbook.getItemMeta();
        newBookData.setAuthor(author);
        newBookData.setTitle(title);
        for (int i = 0; i < pages.length; i++) {
            newBookData.addPage(pages[i]);
        }
        newbook.setItemMeta(newBookData);
        return newbook;
    }

    public Player extractEmbeddedAuthor() {
        String[] parts = getPage(1).split("\n");
        if (parts.length > 13) {
            return Util.UUID2Player(parts[14].trim());
        }
        return null;
    }

    public Player extractEmbeddedOwner() {
        return extractEmbeddedAuthor();
    }

    public Player extractEmbeddedReceipient() {
        String[] parts = getPage(1).split("\n");
        if (parts.length > 14) {
            return Util.UUID2Player(parts[15].trim());
        }
        return null;
    }
}
