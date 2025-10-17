package test;

import model.*;
import exceptions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class CareHomeTest {

    private CareHome freshHome() { return new CareHome(); }

    private Manager mgr() { return new Manager("M1","Manager",'M',"mgr","p"); }

    private Nurse nurse() { return new Nurse("N1","Nina",'F',"nina","p"); }

    private Doctor doc() { return new Doctor("D1","Dev",'M',"dev","p","General"); }

    // 1) Happy path: add resident to a vacant bed
    @Test
    void addResidentToVacantBed_succeeds() throws Exception {
        CareHome home = freshHome();
        home.addStaff(mgr(), nurse()); // not used here but realistic
        home.addStaff(mgr(), doc());

        Resident r = new Resident("R1","Ray",'M',80);
        home.addResident(mgr(), r, "W1-R2-B1");

        assertNotNull(r.getBed());
        assertEquals("W1-R2-B1", r.getBed().getBedId());
        assertTrue(home.getResidents().containsKey("R1"));
    }

    // 2) Negative: adding to occupied bed should throw
    @Test
    void addResidentToOccupiedBed_throws() throws Exception {
        CareHome home = freshHome();
        Manager m = mgr();
        Resident r1 = new Resident("R1","A",'M',70);
        Resident r2 = new Resident("R2","B",'F',68);
        home.addResident(m, r1, "W1-R2-B1");

        assertThrows(BedOccupiedException.class,
                () -> home.addResident(m, r2, "W1-R2-B1"));
    }

    // 3) Role guard: only doctor can add prescriptions
    @Test
    void addPrescription_onlyDoctorAllowed() throws Exception {
        CareHome home = freshHome();
        Manager m = mgr();
        Doctor d = doc();
        Nurse n = nurse();

        home.addStaff(m, d);
        home.addStaff(m, n);

        Resident r = new Resident("R1","Ray",'M',80);
        home.addResident(m, r, "W1-R2-B1");

        // doctor OK
        home.addPrescription(d, "R1",
                new Prescription("Amoxicillin","500mg", LocalTime.of(9,0), d.getId()));
        assertEquals(1, r.getPrescriptions().size());

        // nurse should fail
        assertThrows(UnauthorizedActionException.class, () ->
                home.addPrescription(null, "R1",
                        new Prescription("Paracetamol","500mg", LocalTime.of(13,0), d.getId())));
    }

    // 4) Compliance: nurse > 8h on same day should throw
    @Test
    void nurseOver8hPerDay_violatesCompliance() throws Exception {
        CareHome home = freshHome();
        Manager m = mgr();
        Nurse n = nurse();
        home.addStaff(m, n);

        n.addShift(new Shift(java.time.DayOfWeek.MONDAY,
                java.time.LocalTime.of(8,0), java.time.LocalTime.of(16,0)));
        n.addShift(new Shift(java.time.DayOfWeek.MONDAY,
                java.time.LocalTime.of(14,0), java.time.LocalTime.of(22,0))); // overlap → 16h

        assertThrows(ShiftViolationException.class, home::checkCompliance);
    }

    // 5) Serialization: save then load and verify resident remains
    @Test
    void saveAndLoad_restoresState() throws Exception {
        CareHome home = freshHome();
        Manager m = mgr();
        home.addResident(m, new Resident("R1","Ray",'M',80), "W1-R2-B1");
        String file = "test-carehome.dat";
        home.saveData(file);

        CareHome loaded = CareHome.loadData(file);
        assertTrue(loaded.getResidents().containsKey("R1"));
    }
}
