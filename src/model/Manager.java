package model;

/**
 * Represents a Manager who can add or edit staff and residents.
 */
public class Manager extends Staff {

    public Manager(String id, String name, char gender,
                   String username, String passwordHash) {
        super(id, name, gender, username, passwordHash, "MANAGER");
    }

    @Override
    public String toString() {
        return super.toString() + " (Manager Access)";
    }
}
