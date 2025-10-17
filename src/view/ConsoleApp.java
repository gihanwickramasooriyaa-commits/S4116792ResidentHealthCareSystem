package view;

import model.*;
import model.Prescription;
import exceptions.*;
import java.time.LocalTime;

/**
 * Simple console-based driver to test model functionality.
 */
public class ConsoleApp {

    public static void main(String[] args) {
        try {
            // Create main CareHome system
            CareHome home = new CareHome();

            // Create manager and staff
            Manager mgr = new Manager("M001", "Alex", 'M', "alex", "pass123");
            Nurse n1 = new Nurse("N001", "Nina", 'F', "nina", "pw");
            Doctor d1 = new Doctor("D001", "Dev", 'M', "dev", "pw", "General");

            // Add staff via manager
            home.addStaff(mgr, n1);
            home.addStaff(mgr, d1);

            // Create and add resident
            Resident r1 = new Resident("R001", "Sam", 'M', 75);
            home.addResident(mgr, r1, "W1-R2-B1");

            // Doctor adds prescription
            Prescription p = new Prescription("Amoxicillin", "500mg", LocalTime.of(9, 0), d1.getId());
            home.addPrescription(d1, r1.getId(), p);

            // Nurse moves resident to new bed
            home.moveResident(n1, "R001", "W1-R4-B1");

            // Display current data
            System.out.println("=== All Beds ===");
            for (Bed b : home.getBeds()) {
                System.out.println(b);
            }

            System.out.println("\n=== Residents ===");
            home.getResidents().values().forEach(System.out::println);

            System.out.println("\n=== Audit Log ===");
            home.getAuditLog().forEach(System.out::println);

         // ----------------------------------------------------------------
         // Add shifts and check compliance (may throw ShiftViolationException)
         n1.addShift(new Shift(java.time.DayOfWeek.MONDAY,
                 java.time.LocalTime.of(8, 0), java.time.LocalTime.of(16, 0))); // OK (8h)

         // Uncomment next shift to test violation (>8h same day)
         // n1.addShift(new Shift(java.time.DayOfWeek.MONDAY,
//                  java.time.LocalTime.of(14, 0), java.time.LocalTime.of(22, 0)));

         home.checkCompliance(); // <-- this may throw ShiftViolationException
         // ----------------------------------------------------------------
            
            // Save system state
            home.saveData("carehome.dat");
            System.out.println("\nData saved to carehome.dat");

        } catch (UnauthorizedActionException | BedOccupiedException | ShiftViolationException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
