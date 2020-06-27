package com.zakgof.velvetdb.viewer.example;

import com.zakgof.db.velvet.annotation.Key;

public class Person {

    @Key
    private String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
