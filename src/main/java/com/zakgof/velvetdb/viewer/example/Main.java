package com.zakgof.velvetdb.viewer.example;

import com.google.inject.Injector;
import com.zakgof.db.velvet.IVelvet;
import com.zakgof.db.velvet.IVelvetEnvironment;
import com.zakgof.velvetdb.viewer.VelvetViewer;

public class Main {

    private static final String VELVET_PATH = "velvetdb://xodus/D:/Pr/mapdbtest/xodus";

    public static void main(String[] args) throws Throwable {

        Injector injector = VelvetViewer.start(VELVET_PATH, Defs.MODEL, 4567);

        injector.getInstance(IVelvetEnvironment.class).execute(velvet -> initDb(velvet));

    }

    private static void initDb(IVelvet velvet) {

        if (Defs.BOOK.size(velvet) > 0)
            return;

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

        for (int i = 6; i < 1000; i++) {
            Book book = new Book("isbn-" + i, "Book name " + i);
            Defs.BOOK.put(velvet, book);
        }

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

        addPassport(velvet, wayne, "JW12345");
        addPassport(velvet, obama, "BO99999");
        addPassport(velvet, bush, "GB66666");
        addPassport(velvet, bill, "BC43424");
        addPassport(velvet, hillary, "HC98765");

        Defs.AUTHOR_BOOK.connect(velvet, obama, book1);
        Defs.AUTHOR_BOOK.connect(velvet, obama, book2);
        Defs.AUTHOR_BOOK.connect(velvet, obama, book3);

        Defs.AUTHOR_BOOK.connect(velvet, bill, book4);

        for (int i = 1; i < 1000; i++) {
            Person person = new Person("Name-" + i, "LastName-" + i);
            Defs.PERSON.put(velvet, person);
        }

    }

    private static void addPassport(IVelvet velvet, Person person, String passportNo) {
        Passport passport = new Passport(passportNo);
        Defs.PASSPORT.put(velvet, passport);
        Defs.PERSON_PASSPORT.connect(velvet, person, passport);
    }
}