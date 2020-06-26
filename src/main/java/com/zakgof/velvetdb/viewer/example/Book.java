package com.zakgof.velvetdb.viewer.example;

import com.zakgof.db.velvet.annotation.SortedKey;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Book implements Serializable {

    @SortedKey
    private String isbn;

    private String title;

    public Book(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
    }

    @Override
    public String toString() {
        return title + "(" + isbn + ")";
    }
}
