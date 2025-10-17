package model;

import java.io.*;
import java.time.DayOfWeek;
import java.util.*;

import exceptions.*;

/**
 * Main system class that manages staff, residents, beds, and actions.
 * Demonstrates use of collections, exceptions, and serialization.
 */
public class CareHome implements Serializable {

    private Map<String, Staff> staffList;
    private Map<String, Resident> residents;
    private Map<String, Bed> beds;
    private List<String> auditLog;

    public CareHome() {
        staffList = new HashMap<>();
        residents = new HashMap<>();
        beds = new LinkedHashMap<>();
        auditLog = new ArrayList<>();

        // --- build 2 wards × 6 rooms with 1–4 beds each ---
        for (int w = 1; w <= 2; w++) {
            for (int r = 1; r <= 6; r++) {
                int bedCount = switch (r) {
                    case 1 -> 1;
                    case 2, 3 -> 2;
                    case 4 -> 3;
                    default -> 4;
                };
                for (int b = 1; b <= bedCount; b++) {
                    String id = "W" + w + "-R" + r + "-B" + b;
                    beds.put(id, new Bed(id));
                }
            }
        }
    }

    // -------------------------------------------------------------------
    // STAFF MANAGEMENT
    // -------------------------------------------------------------------
    public void addStaff(Manager manager, Staff staff) throws UnauthorizedActionException {
        if (manager == null)
            throw new UnauthorizedActionException("Only manager can add staff!");
        staffList.put(staff.getUsername(), staff);
        log(manager.getName() + " added staff: " + staff.getName());
    }

    // -------------------------------------------------------------------
    // RESIDENT MANAGEMENT
    // -------------------------------------------------------------------
    public void addResident(Manager manager, Resident r, String bedId)
            throws UnauthorizedActionException, BedOccupiedException {
        if (manager == null)
            throw new UnauthorizedActionException("Only manager can add residents!");
        Bed bed = beds.get(bedId);
        if (bed == null)
            throw new IllegalArgumentException("Invalid bed ID: " + bedId);
        if (bed.isOccupied())
            throw new BedOccupiedException("Bed already occupied!");
        bed.assign(r);
        r.setBed(bed);
        residents.put(r.getId(), r);
        log(manager.getName() + " added resident " + r.getName() + " to " + bedId);
    }

    public void moveResident(Nurse nurse, String residentId, String toBedId)
            throws UnauthorizedActionException, BedOccupiedException {
        if (nurse == null)
            throw new UnauthorizedActionException("Only nurse can move residents!");
        Resident r = residents.get(residentId);
        Bed to = beds.get(toBedId);
        if (to.isOccupied())
            throw new BedOccupiedException("Destination bed occupied!");
        if (r.getBed() != null)
            r.getBed().vacate();
        to.assign(r);
        r.setBed(to);
        log(nurse.getName() + " moved resident " + r.getName() + " to " + toBedId);
    }

    // -------------------------------------------------------------------
    // PRESCRIPTIONS
    // -------------------------------------------------------------------
    public void addPrescription(Doctor doc, String residentId, Prescription p)
            throws UnauthorizedActionException {
        if (doc == null)
            throw new UnauthorizedActionException("Only doctors can add prescriptions!");
        Resident r = residents.get(residentId);
        if (r != null) {
            r.addPrescription(p);
            log(doc.getName() + " added prescription for " + r.getName() + ": " + p);
        }
    }

    // -------------------------------------------------------------------
    // COMPLIANCE CHECK
    // -------------------------------------------------------------------
    public void checkCompliance() throws ShiftViolationException {
        for (Staff s : staffList.values()) {
            if (s instanceof Nurse nurse) {
                Map<DayOfWeek, Long> hoursPerDay = new EnumMap<>(DayOfWeek.class);
                for (Shift shift : nurse.getShifts()) {
                    hoursPerDay.merge(shift.getDay(), shift.getHours(), Long::sum);
                }
                for (var e : hoursPerDay.entrySet()) {
                    if (e.getValue() > 8)
                        throw new ShiftViolationException("Nurse " + nurse.getName()
                                + " exceeds 8 hours on " + e.getKey());
                }
            }
        }
    }

    // -------------------------------------------------------------------
    // FILE SAVE / LOAD (Serialization)
    // -------------------------------------------------------------------
    public void saveData(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    public static CareHome loadData(String filename)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (CareHome) in.readObject();
        }
    }

    // -------------------------------------------------------------------
    // UTILS
    // -------------------------------------------------------------------
    private void log(String msg) {
        auditLog.add(new Date() + " - " + msg);
    }

    public List<String> getAuditLog() {
        return auditLog;
    }

    public Collection<Bed> getBeds() {
        return beds.values();
    }

    public Map<String, Staff> getStaffList() {
        return staffList;
    }

    public Map<String, Resident> getResidents() {
        return residents;
    }
}
