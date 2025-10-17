package persistence;

import model.Prescription;

import java.sql.*;
import java.time.LocalTime;

public class PrescriptionDao {
    public static void insert(String residentId, Prescription p) throws SQLException {
        try (PreparedStatement ps = Db.get().prepareStatement("""
            INSERT INTO Prescription(residentId,medicine,dosage,time,doctorId)
            VALUES(?,?,?,?,?)
        """)) {
            ps.setString(1, residentId);
            ps.setString(2, p.getMedicine());
            ps.setString(3, p.getDosage());
            ps.setString(4, p.getTime().toString());
            ps.setString(5, p.getDoctorId());
            ps.executeUpdate();
        }
    }
}
