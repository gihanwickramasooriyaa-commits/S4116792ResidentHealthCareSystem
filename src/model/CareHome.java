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
        log(manager.getName() + " (" + manager.getId() + ") added staff: "
                + staff.getName() + " [" + staff.getUsername() + "]");
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
        log(manager.getName() + " (" + manager.getId() + ") added resident "
                + r.getName() + " to " + bedId);
    }

    public void moveResident(Nurse nurse, String residentId, String toBedId)
            throws UnauthorizedActionException, BedOccupiedException {
        if (nurse == null)
            throw new UnauthorizedActionException("Only nurse can move residents!");
        Resident r = residents.get(residentId);
        Bed to = beds.get(toBedId);
        if (to == null)
            throw new IllegalArgumentException("Invalid bed ID: " + toBedId);  // <-- added guard
        if (to.isOccupied())
            throw new BedOccupiedException("Destination bed occupied!");
        if (r.getBed() != null)
            r.getBed().vacate();
        to.assign(r);
        r.setBed(to);
        log(nurse.getName() + " (" + nurse.getId() + ") moved resident "
                + r.getName() + " to " + toBedId);
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
            log(doc.getName() + " (" + doc.getId() + ") added prescription for "
                    + r.getName() + ": " + p);
        }
    }

    // -------------------------------------------------------------------
    // MEDICATION ADMINISTRATION (Nurse)
    // -------------------------------------------------------------------
    public void administerMedication(Nurse nurse,
                                     String residentId,
                                     String medicine,
                                     String dosage,
                                     java.time.LocalDateTime when)
            throws UnauthorizedActionException {
        if (nurse == null)
            throw new UnauthorizedActionException("Only a nurse can administer medication.");
        Resident r = residents.get(residentId);
        if (r == null)
            throw new IllegalArgumentException("Resident not found: " + residentId);

        r.addAdministration(new AdministrationRecord(medicine, dosage, when, nurse.getId()));
        log(nurse.getName() + " (" + nurse.getId() + ") administered "
                + medicine + " " + dosage + " to " + r.getName() + " at " + when);
    }

    // -------------------------------------------------------------------
    // DISCHARGE + ARCHIVE (Manager)
    // -------------------------------------------------------------------
    /** Discharge a resident and archive their details to a CSV file. */
    public void dischargeResident(Manager manager, String residentId, String outFile)
            throws UnauthorizedActionException, IOException {
        if (manager == null) throw new UnauthorizedActionException("Only manager can discharge!");
        Resident r = residents.get(residentId);
        if (r == null) throw new IllegalArgumentException("Resident not found: " + residentId);

        // write archive CSV (resident + prescriptions + administered doses)
        try (java.io.PrintWriter w = new java.io.PrintWriter(outFile)) {
            w.println("Resident," + r.getId() + "," + r.getName() + "," + r.getGender() + "," + r.getAge());
            w.println("Prescriptions");
            for (var p : r.getPrescriptions()) {
                w.println(p.getMedicine() + "," + p.getDosage() + "," + p.getTime());
            }
            w.println("Administered");
            for (var a : r.getAdministrations()) {
                w.println(a.getAdministeredAt() + "," + a.getMedicine() + "," + a.getDosage() + "," + a.getNurseId());
            }
        }

        // free bed and remove from active residents
        if (r.getBed() != null) { r.getBed().vacate(); r.setBed(null); }
        residents.remove(residentId);

        log(manager.getName() + " (" + manager.getId() + ") discharged "
                + r.getName() + " (archived: " + outFile + ")");
    }

    // -------------------------------------------------------------------
    // STAFF ADMIN (Manager-only)
    // -------------------------------------------------------------------
    /** Change a staff member's password by username. */
    public void changeStaffPassword(Manager manager, String username, String newPassword)
            throws UnauthorizedActionException {
        if (manager == null) throw new UnauthorizedActionException("Only manager can change passwords!");
        Staff s = staffList.get(username);
        if (s == null) throw new IllegalArgumentException("Staff not found: " + username);

        s.setPasswordHash(newPassword);
        log(manager.getName() + " (" + manager.getId() + ") changed password for " + s.getUsername());
    }

    /** Assign a shift to a nurse (identified by username). */
    public void addShiftForNurse(Manager manager, String nurseUsername, Shift shift)
            throws UnauthorizedActionException {
        if (manager == null) throw new UnauthorizedActionException("Only manager can edit shifts!");
        Staff s = staffList.get(nurseUsername);
        if (!(s instanceof Nurse n)) throw new IllegalArgumentException("Not a nurse: " + nurseUsername);

        n.addShift(shift);
        log(manager.getName() + " (" + manager.getId() + ") assigned shift to "
                + n.getUsername() + ": " + shift);
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

        // OPTIONAL: doctor presence rule (uncomment if required by your tutor)
        // boolean anyDoctor = staffList.values().stream().anyMatch(s -> s instanceof Doctor);
        // if (!anyDoctor) {
        //     throw new ShiftViolationException("Compliance: No doctor available.");
        // }
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
