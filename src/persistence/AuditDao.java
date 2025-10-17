package persistence;

import java.sql.*;
import java.time.LocalDateTime;

public class AuditDao {
    public static void log(String message) throws SQLException {
        try (PreparedStatement ps = Db.get().prepareStatement("INSERT INTO Audit(ts,message) VALUES(?,?)")) {
            ps.setString(1, LocalDateTime.now().toString());
            ps.setString(2, message);
            ps.executeUpdate();
        }
    }
}
