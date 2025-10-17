package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** One administered dose for a resident. */
public class AdministrationRecord implements Serializable {
    private final String medicine;
    private final String dosage;
    private final LocalDateTime administeredAt;
    private final String nurseId;

    public AdministrationRecord(String medicine, String dosage, LocalDateTime administeredAt, String nurseId) {
        this.medicine = medicine;
        this.dosage = dosage;
        this.administeredAt = administeredAt;
        this.nurseId = nurseId;
    }
    public String getMedicine() { return medicine; }
    public String getDosage() { return dosage; }
    public LocalDateTime getAdministeredAt() { return administeredAt; }
    public String getNurseId() { return nurseId; }

    @Override public String toString() {
        return administeredAt + " - " + medicine + " " + dosage + " (nurse " + nurseId + ")";
    }
}
