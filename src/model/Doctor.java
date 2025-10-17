package model;

/**
 * Represents a Doctor in the Resident HealthCare System.
 * Doctors can create prescriptions for residents.
 */
public class Doctor extends Staff {

    private String specialization;

    public Doctor(String id, String name, char gender,
                  String username, String passwordHash, String specialization) {
        super(id, name, gender, username, passwordHash, "DOCTOR");
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + specialization;
    }
}
