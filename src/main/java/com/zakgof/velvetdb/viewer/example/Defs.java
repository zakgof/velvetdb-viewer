package com.zakgof.velvetdb.viewer.example;

import com.zakgof.db.velvet.entity.Entities;
import com.zakgof.db.velvet.entity.ISortableEntityDef;
import com.zakgof.db.velvet.entity.ISortableSetEntityDef;
import com.zakgof.db.velvet.link.IBiManyToManyLinkDef;
import com.zakgof.db.velvet.link.Links;
import com.zakgof.velvetdb.viewer.ViewerDataModel;

public class Defs {


  public static final ISortableEntityDef<String, Book> BOOK = Entities.sorted(Book.class);
  public static final ISortableEntityDef<String, Person> AUTHOR = Entities.from(Person.class).kind("author").makeSorted();
  public static final ISortableSetEntityDef<String> PUBLISHER = Entities.from(String.class).kind("publisher").makeSortedSet();

  public static final IBiManyToManyLinkDef<String, Person, String, Book> AUTHOR_BOOK = Links.biManyToMany(AUTHOR, BOOK);
  public static final IBiManyToManyLinkDef<String, String, String, Book> PUBLISHER_BOOK = Links.biManyToMany(PUBLISHER, BOOK);

  public static final ViewerDataModel MODEL = ViewerDataModel.builder().
      entity(BOOK).
      entity(AUTHOR).
      entity(PUBLISHER).
      link(AUTHOR_BOOK).
      link(PUBLISHER_BOOK).
      build();
}
