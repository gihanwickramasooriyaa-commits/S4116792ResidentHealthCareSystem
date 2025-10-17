package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a resident (patient) in the Care Home.
 * Holds current bed assignment, prescriptions, and administered doses.
 */
public class Resident extends Person implements Serializable {

    private int age;
    private Bed bed;                               // null if not assigned yet
    private final List<Prescription> prescriptions = new ArrayList<>();
    private final List<AdministrationRecord> administrations = new ArrayList<>(); // ✅ new

    public Resident(String id, String name, char gender, int age) {
        super(id, name, gender);
        this.age = age;
    }

    // --- getters/setters ---
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Bed getBed() { return bed; }
    /* package-private */ void setBed(Bed bed) { this.bed = bed; }

    public List<Prescription> getPrescriptions() { return prescriptions; }

    /** All administered medication records for this resident. */
    public List<AdministrationRecord> getAdministrations() { return administrations; }

    // --- domain helpers ---
    public void addPrescription(Prescription p) {
        prescriptions.add(p);
    }

    /** Record a single administered dose. */
    public void addAdministration(AdministrationRecord rec) {
        administrations.add(rec);
    }

    @Override
    public String toString() {
        String bedId = (bed == null ? "No bed" : bed.getBedId());
        return String.format("%s, %d yrs, %s", super.toString(), age, bedId);
    }
}
