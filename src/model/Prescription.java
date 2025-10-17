package model;

public class Prescription {

}
package model;

import java.io.Serializable;
import java.time.LocalTime;

/** A single medicine instruction for a resident. */
public class Prescription implements Serializable {
    private final String medicine;
    private final String dosage;        // e.g. "500mg"
    private final LocalTime time;       // e.g. 09:00
    private final String doctorId;      // who created it

    public Prescription(String medicine, String dosage, LocalTime time, String doctorId) {
        this.medicine = medicine;
        this.dosage = dosage;
        this.time = time;
        this.doctorId = doctorId;
    }

    public String getMedicine() { return medicine; }
    public String getDosage() { return dosage; }
    public LocalTime getTime() { return time; }
    public String getDoctorId() { return doctorId; }

    @Override public String toString() { return medicine + " " + dosage + " @ " + time; }
}
