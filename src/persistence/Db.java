package persistence;

import java.sql.*;

public final class Db {
    private static final String URL = "jdbc:sqlite:carehome.db";
    private static Connection CONN;

    private Db() {}

    public static Connection get() throws SQLException {
        if (CONN == null || CONN.isClosed()) {
            CONN = DriverManager.getConnection(URL);
            CONN.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return CONN;
    }

    // Create tables if not exist
    public static void init() throws SQLException {
        try (Statement st = get().createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS Staff(
                  id TEXT PRIMARY KEY,
                  username TEXT UNIQUE,
                  role TEXT NOT NULL,
                  name TEXT NOT NULL,
                  gender CHAR(1) NOT NULL
                );
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS Resident(
                  id TEXT PRIMARY KEY,
                  name TEXT NOT NULL,
                  gender CHAR(1) NOT NULL,
                  age INTEGER NOT NULL
                );
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS Bed(
                  id TEXT PRIMARY KEY,
                  residentId TEXT REFERENCES Resident(id) ON DELETE SET NULL
                );
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS Prescription(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  residentId TEXT NOT NULL REFERENCES Resident(id) ON DELETE CASCADE,
                  medicine TEXT NOT NULL,
                  dosage TEXT NOT NULL,
                  time TEXT NOT NULL,
                  doctorId TEXT NOT NULL
                );
            """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS Audit(
                  ts TEXT NOT NULL,
                  message TEXT NOT NULL
                );
            """);
        }
    }
}
