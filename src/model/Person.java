package model;

import java.io.Serializable;

/**
 * Base abstract class for all people in the system (staff and residents).
 * Demonstrates encapsulation and inheritance.
 */
public abstract class Person implements Serializable {
    private String id;
    private String name;
    private char gender;   // 'M' or 'F'

    public Person(String id, String name, char gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public char getGender() { return gender; }

    public void setName(String name) { this.name = name; }
    public void setGender(char gender) { this.gender = gender; }

    @Override
    public String toString() {
        return String.format("%s (%c) - %s", name, gender, id);
    }
}
