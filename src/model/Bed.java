package model;

import java.io.Serializable;

/** Bed within a room/ward. Example id: W1-R3-B2 */
public class Bed implements Serializable {
    private final String bedId;
    private Resident occupant; // null if vacant

    public Bed(String bedId) { this.bedId = bedId; }

    public String getBedId() { return bedId; }
    public boolean isOccupied() { return occupant != null; }
    public Resident getOccupant() { return occupant; }

    /* package-private */ void assign(Resident r) { this.occupant = r; }
    /* package-private */ void vacate() { this.occupant = null; }

    @Override public String toString() { return bedId + (isOccupied() ? " [OCCUPIED]" : " [VACANT]"); }
}
