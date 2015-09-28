package com.zakgof.velvetdb.viewer.example;

import com.zakgof.db.velvet.entity.Entities;
import com.zakgof.db.velvet.entity.IKeylessEntityDef;
import com.zakgof.db.velvet.entity.ISortableEntityDef;
import com.zakgof.db.velvet.link.IBiMultiLinkDef;
import com.zakgof.db.velvet.link.Links;
import com.zakgof.tools.web.pojoeditor.ViewerDataModel;

public class Defs {
  
  
  public static final IKeylessEntityDef<Person> PERSON = Entities.keyless(Person.class);
  public static final ISortableEntityDef<String, Book> BOOK = Entities.sorted(Book.class);
  
  public static final IBiMultiLinkDef<Long, Person, String, Book> AUTHOR_BOOK = Links.biMulti(PERSON, BOOK);
  
  public static final ViewerDataModel MODEL = ViewerDataModel.builder().
      entity(PERSON).
      entity(BOOK).
      link(AUTHOR_BOOK).
      build();
}
