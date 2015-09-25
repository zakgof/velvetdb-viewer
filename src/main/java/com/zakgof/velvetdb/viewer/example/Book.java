package com.zakgof.velvetdb.viewer.example;

import com.zakgof.db.velvet.annotation.SortedKey;

public class Book {
  
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
