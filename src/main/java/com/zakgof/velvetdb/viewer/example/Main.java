package com.zakgof.velvetdb.viewer.example;

import com.google.inject.Injector;
import com.zakgof.db.velvet.IVelvet;
import com.zakgof.db.velvet.IVelvetEnvironment;
import com.zakgof.db.velvet.xodus.XodusVelvetFactory;
import com.zakgof.velvetdb.viewer.VelvetViewer;

public class Main {
  
  private static final String VELVET_PATH = "velvetdb://xodus/D:/Pr/mapdbtest/xodus";

  public static void main(String[] args) throws Throwable {   
    
    Class.forName(XodusVelvetFactory.class.getName());
    Injector injector = VelvetViewer.start(VELVET_PATH, Defs.MODEL);
    
    injector.getInstance(IVelvetEnvironment.class).execute(velvet -> initDb(velvet));
    
  }

  private static void initDb(IVelvet velvet) {
    
    Book book1 = new Book("isbn-1-1-1", "Book name 1");
    Book book2 = new Book("isbn-1-1-2", "Book name 2");
    Book book3 = new Book("isbn-1-1-3", "Book name 3");
    Book book4 = new Book("isbn-1-1-4", "Book name 4");
    Book book5 = new Book("isbn-1-1-5", "Book name 5");
    
    Defs.BOOK.put(velvet, book1);
    Defs.BOOK.put(velvet, book2);
    Defs.BOOK.put(velvet, book3);
    Defs.BOOK.put(velvet, book4);
    Defs.BOOK.put(velvet, book5);
    
    Person wayne = new Person("John", "Wayne");
    Person obama = new Person("Barack", "Obama");
    Person bush = new Person("George", "Bush");
    Person bill = new Person("Bill", "Clinton");
    Person hillary = new Person("Hillary", "Clinton");
    
    Defs.PERSON.put(velvet, wayne);
    Defs.PERSON.put(velvet, obama);
    Defs.PERSON.put(velvet, bush);
    Defs.PERSON.put(velvet, bill);
    Defs.PERSON.put(velvet, hillary);
    
    Defs.AUTHOR_BOOK.connect(velvet, obama, book1);
    Defs.AUTHOR_BOOK.connect(velvet, obama, book2);
    Defs.AUTHOR_BOOK.connect(velvet, obama, book3);
    
    Defs.AUTHOR_BOOK.connect(velvet, bill, book4);
    
  }
}