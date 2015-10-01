package com.zakgof.velvetdb.viewer.example;

import com.zakgof.db.velvet.entity.Entities;
import com.zakgof.db.velvet.entity.IKeylessEntityDef;
import com.zakgof.db.velvet.entity.ISortableEntityDef;
import com.zakgof.db.velvet.link.IBiMultiLinkDef;
import com.zakgof.db.velvet.link.IBiSingleLinkDef;
import com.zakgof.db.velvet.link.Links;
import com.zakgof.velvetdb.viewer.ViewerDataModel;

public class Defs {
  
  public static final IKeylessEntityDef<Person> PERSON = Entities.keyless(Person.class);
  public static final IKeylessEntityDef<Passport> PASSPORT = Entities.keyless(Passport.class);
  public static final ISortableEntityDef<String, Book> BOOK = Entities.sorted(Book.class);
  
  public static final IBiMultiLinkDef<Long, Person, String, Book> AUTHOR_BOOK = Links.biMulti(PERSON, BOOK);
  public static final IBiSingleLinkDef<Long, Person, Long, Passport> PERSON_PASSPORT = Links.biSingle(PERSON, PASSPORT);
  
  public static final ViewerDataModel MODEL = ViewerDataModel.builder().
      entity(PERSON).
      entity(BOOK).
      entity(PASSPORT).
      link(AUTHOR_BOOK).
      link(PERSON_PASSPORT).
      build();
}
