package com.zakgof.velvetdb.viewer.example;

import com.zakgof.db.velvet.annotation.Index;
import com.zakgof.db.velvet.annotation.SortedKey;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Book implements Serializable {

    @SortedKey
    private String isbn;

    private String title;

    @Index
    private int year;

    public Book(String isbn, String title, int year) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
    }

    @Override
    public String toString() {
        return isbn + ": " + title + " (" + year + ")";
    }
}
