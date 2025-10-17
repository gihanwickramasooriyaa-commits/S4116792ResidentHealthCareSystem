package persistence;

import model.Resident;

import java.sql.*;
import java.util.*;

public class ResidentDao {
    public static void upsert(Resident r) throws SQLException {
        try (PreparedStatement ps = Db.get().prepareStatement("""
            INSERT INTO Resident(id,name,gender,age) VALUES(?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET name=excluded.name, gender=excluded.gender, age=excluded.age
        """)) {
            ps.setString(1, r.getId());
            ps.setString(2, r.getName());
            ps.setString(3, String.valueOf(r.getGender()));
            ps.setInt(4, r.getAge());
            ps.executeUpdate();
        }
    }

    public static Map<String, Resident> findAll() throws SQLException {
        Map<String, Resident> map = new LinkedHashMap<>();
        try (PreparedStatement ps = Db.get().prepareStatement("SELECT id,name,gender,age FROM Resident");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1),
                        new Resident(rs.getString(1), rs.getString(2),
                                     rs.getString(3).charAt(0), rs.getInt(4)));
            }
        }
        return map;
    }
}
