package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Nurse in the Resident HealthCare System.
 * Nurses can move residents and administer medication.
 */
public class Nurse extends Staff {

    private List<String> assignedResidents; // list of resident IDs for this nurse

    public Nurse(String id, String name, char gender,
                 String username, String passwordHash) {
        super(id, name, gender, username, passwordHash, "NURSE");
        this.assignedResidents = new ArrayList<>();
    }

    public List<String> getAssignedResidents() {
        return assignedResidents;
    }

    public void addResident(String residentId) {
        assignedResidents.add(residentId);
    }

    public void removeResident(String residentId) {
        assignedResidents.remove(residentId);
    }

    @Override
    public String toString() {
        return super.toString() + " - Assigned residents: " + assignedResidents.size();
    }
}
