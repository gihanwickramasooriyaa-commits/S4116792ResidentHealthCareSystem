package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Staff class extends Person.
 * This class is the base for Manager, Doctor, and Nurse.
 * Demonstrates inheritance, encapsulation, and polymorphism.
 */
public abstract class Staff extends Person {

    private String username;
    private String passwordHash;   // hashed password (not plain text)
    private String role;           // MANAGER, DOCTOR, NURSE
    private List<Shift> shifts;    // daily/weekly work shifts

    public Staff(String id, String name, char gender,
                 String username, String passwordHash, String role) {
        super(id, name, gender);
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.shifts = new ArrayList<>();
    }

    // Getters and setters
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public List<Shift> getShifts() { return shifts; }

    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }

    /**
     * Adds a work shift to this staff member.
     */
    public void addShift(Shift shift) {
        shifts.add(shift);
    }

    @Override
    public String toString() {
        return getName() + " [" + role + "]";
    }
}
