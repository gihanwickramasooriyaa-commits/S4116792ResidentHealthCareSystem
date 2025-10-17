package persistence;

import model.*;

import java.sql.*;
import java.util.*;

public class BedDao {
    public static void initBedsFromModel(CareHome home) throws SQLException {
        // Seed table with the same bed IDs your model constructs
        try (PreparedStatement ps = Db.get().prepareStatement("""
            INSERT INTO Bed(id,residentId) VALUES(?,NULL)
            ON CONFLICT(id) DO NOTHING
        """)) {
            for (Bed b : home.getBeds()) {
                ps.setString(1, b.getBedId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public static void setOccupant(String bedId, String residentId) throws SQLException {
        try (PreparedStatement ps = Db.get().prepareStatement("UPDATE Bed SET residentId=? WHERE id=?")) {
            if (residentId == null) ps.setNull(1, Types.VARCHAR); else ps.setString(1, residentId);
            ps.setString(2, bedId);
            ps.executeUpdate();
        }
    }

    public static void loadIntoModel(CareHome home) throws SQLException {
        // Clear any current occupancy, then set from DB
        for (Bed b : home.getBeds()) { if (b.isOccupied()) { b.getOccupant().setBed(null); b.vacate(); } }
        try (PreparedStatement ps = Db.get().prepareStatement("SELECT id,residentId FROM Bed");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String bedId = rs.getString(1);
                String rid = rs.getString(2);
                if (rid != null && home.getResidents().containsKey(rid)) {
                    Resident r = home.getResidents().get(rid);
                    for (Bed b : home.getBeds()) {
                        if (b.getBedId().equals(bedId)) { b.assign(r); r.setBed(b); break; }
                    }
                }
            }
        }
    }
}
