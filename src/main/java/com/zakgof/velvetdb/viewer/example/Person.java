package com.zakgof.velvetdb.viewer.example;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Person implements Serializable {

    private String firstName;

    private String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return lastName + " " + firstName;
    }
}
