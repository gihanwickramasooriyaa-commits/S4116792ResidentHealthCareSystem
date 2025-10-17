# Resident HealthCare System

**Student:**Gihan Tharaka— S4116792
**Course:** COSC1295 — Assignment 2 (S2 2025)

## 1. What this app does
A small Resident HealthCare system with a JavaFX GUI and an OO back end:
- Ward grid of beds (occupied colour-coded by resident gender)
- **Roles:** Manager, Nurse, Doctor (role switcher in toolbar)
- **Manager:** add resident to a vacant bed, discharge resident (archives CSV), add staff (nurse/doctor), change staff password, assign nurse shifts, save/load state, export audit log
- **Nurse:** move resident between beds, record administered dose
- **Doctor:** create prescriptions for a resident
- **Compliance:** checks nurse shifts (≤ 8 hours per day); optional doctor presence rule commented in code
- **Persistence:** save/load entire system via serialization; discharge exports resident archive CSV; audit log export

## 2. How to run (Eclipse)
- JDK: **Java 21** (Eclipse JRE System Library [JavaSE-21])
- JavaFX: running via Eclipse launcher (no extra VM args needed)
1. Import the project → ensure `src/` is on the build path.
2. Run **`view.App`** as a Java Application.
3. Optional: Run JUnit tests (package `test`) with JUnit 5.

## 3. Quick user guide
- **Role selector:** top-left. Choose Manager / Doctor / Nurse.
- **Manager actions:**
  - Right-click a **vacant** bed → *Add resident here…*
  - Menu **File → Export audit log…**
  - Toolbar **Save / Load** serializes/deserializes to `carehome.dat`
  - Right-click an **occupied** bed → *Discharge resident…* (creates `archive_<id>.csv`)
  - Menu **Staff** → *Add Nurse*, *Add Doctor*, *Change Staff Password*, *Edit Nurse Shifts…*
- **Nurse actions:**
  - Right-click an **occupied** bed → *Select resident to move*; then right-click a **vacant** bed → *Move selected resident here*
  - Right-click an **occupied** bed → *Mark dose administered…*
- **Doctor actions:**
  - Right-click an **occupied** bed → *Add prescription…*
- **Compliance:** click **Check Compliance** in toolbar. Over-8h/day nurse shifts raise an alert.

## 4. Project layout
