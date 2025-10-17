package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ScrollPane;     // scroll
import javafx.scene.layout.VBox;          // menu + toolbar container
import javafx.stage.Stage;

import model.*;
import util.Validators;                   // validation

import java.io.PrintWriter;               // for CSV export
import java.time.LocalTime;
import java.util.List;

public class App extends Application {

    // ---------------- state ----------------
    public static CareHome HOME = new CareHome();

    private enum Role { MANAGER, DOCTOR, NURSE }
    private Role currentRole = Role.MANAGER;

    // one instance of each staff role for actions
    private final Manager mgr   = new Manager("M1","Manager",'M',"mgr","p");
    private final Nurse nurse   = new Nurse("N1","Nina",'F',"nina","p");
    private final Doctor doctor = new Doctor("D1","Dev",'M',"dev","p","General");

    // remember selection for nurse "move" action
    private Bed selectedBed = null;

    private BorderPane root;              // for refreshing
    private final Label status = new Label();  // status bar

    // ---------------- app start ----------------
    @Override
    public void start(Stage stage) {
        seed(); // add sample data

        root = new BorderPane();
        root.setPadding(new Insets(10));

        // ==== MenuBar (File -> Export audit) ====
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem exportAudit = new MenuItem("Export audit log…");
        exportAudit.setOnAction(e -> {
            try (PrintWriter out = new PrintWriter("audit.csv")) {
                out.println("timestamp_and_message");
                for (String line : HOME.getAuditLog()) {
                    out.println("\"" + line.replace("\"","\"\"") + "\"");
                }
                info("Export", "Audit log written to audit.csv");
            } catch (Exception ex) { error(ex); }
        });

        fileMenu.getItems().addAll(exportAudit);
        menuBar.getMenus().addAll(fileMenu);

        // ==== ToolBar (Role + Save/Load + Compliance) ====
        ToolBar bar = new ToolBar();

        ChoiceBox<Role> roleBox = new ChoiceBox<>();
        roleBox.getItems().addAll(Role.MANAGER, Role.DOCTOR, Role.NURSE);
        roleBox.setValue(currentRole);
        roleBox.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {
            currentRole = newV;
            selectedBed = null;
            refreshUI();
        });

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                HOME.saveData("carehome.dat");
                info("Saved", "State saved to carehome.dat");
                refreshUI(); // update counts after save (optional)
            } catch (Exception ex) { error(ex); }
        });

        Button loadBtn = new Button("Load");
        loadBtn.setOnAction(e -> {
            try {
                HOME = CareHome.loadData("carehome.dat");
                selectedBed = null;
                refreshUI();
                info("Loaded", "State loaded from carehome.dat");
            } catch (Exception ex) { error(ex); }
        });

        Button complianceBtn = new Button("Check Compliance");
        complianceBtn.setOnAction(e -> {
            try {
                HOME.checkCompliance();
                info("Compliance", "All good: no shift violations.");
            } catch (exceptions.ShiftViolationException ex) {
                error(ex);
            } catch (Exception ex) {
                error(ex);
            }
        });

        bar.getItems().addAll(new Label("Role: "), roleBox,
                new Separator(), saveBtn, loadBtn,
                new Separator(), complianceBtn);

        // Put MenuBar + ToolBar together at the top
        VBox top = new VBox(menuBar, bar);
        root.setTop(top);

        // center: ScrollPane containing the ward grid
        ScrollPane sp = new ScrollPane(buildWardGrid());
        sp.setFitToWidth(true);
        sp.setPannable(true);
        root.setCenter(sp);

        // bottom: status bar
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(6, 0, 0, 0));
        updateStatus(); // show initial counts

        Scene scene = new Scene(root, 980, 620);
        stage.setTitle("Resident HealthCare System");
        stage.setScene(scene);
        stage.show();
    }

    // ---------------- UI builders ----------------
    private GridPane buildWardGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        int col = 0, row = 0, count = 0;
        for (Bed bed : HOME.getBeds()) {
            Button b = new Button(bed.getBedId());
            b.setMinSize(120, 60);

            // color occupied beds by gender
            if (bed.isOccupied()) {
                char g = bed.getOccupant().getGender();
                b.setStyle("-fx-background-color:" + (g == 'M' ? "#cfe8ff" : "#ffd0d0") + "; -fx-font-weight: bold;");
            }

            // left click -> info + select
            b.setOnAction(e -> {
                selectedBed = bed;
                String msg = bed.isOccupied()
                        ? bed.getBedId() + "\nResident: " + bed.getOccupant().getName()
                          + " (" + bed.getOccupant().getGender() + ")\nPrescriptions: "
                          + bed.getOccupant().getPrescriptions().size()
                        : bed.getBedId() + " is VACANT";
                info("Bed Details", msg);
            });

            // right-click menu depends on role
            ContextMenu menu = new ContextMenu();

            if (currentRole == Role.MANAGER) {
                if (!bed.isOccupied()) {
                    MenuItem addRes = new MenuItem("Add resident here…");
                    addRes.setOnAction(e -> addResidentHere(bed));
                    menu.getItems().add(addRes);
                }
            } else if (currentRole == Role.NURSE) {
                if (bed.isOccupied()) {
                    MenuItem select = new MenuItem("Select resident to move");
                    select.setOnAction(e -> {
                        selectedBed = bed;
                        info("Selected", "Selected " + bed.getOccupant().getName()
                                + ". Now right-click a VACANT bed → Move here.");
                    });
                    menu.getItems().add(select);
                } else {
                    MenuItem moveHere = new MenuItem("Move selected resident here");
                    moveHere.setDisable(selectedBed == null || !selectedBed.isOccupied() || selectedBed == bed);
                    moveHere.setOnAction(e -> moveSelectedResidentTo(bed));
                    menu.getItems().add(moveHere);
                }
            } else if (currentRole == Role.DOCTOR) {
                if (bed.isOccupied()) {
                    MenuItem addRx = new MenuItem("Add prescription…");
                    addRx.setOnAction(e -> addPrescriptionFor(bed.getOccupant()));
                    menu.getItems().add(addRx);
                }
            }

            b.setOnContextMenuRequested(e -> menu.show(b, e.getScreenX(), e.getScreenY()));

            grid.add(b, col, row);
            col++; count++;
            if (count % 6 == 0) { col = 0; row++; }
        }
        return grid;
    }

    private void refreshUI() {
        // if center is ScrollPane, swap its content; otherwise, set new ScrollPane
        if (root.getCenter() instanceof ScrollPane sp) {
            sp.setContent(buildWardGrid());
        } else {
            ScrollPane spNew = new ScrollPane(buildWardGrid());
            spNew.setFitToWidth(true);
            spNew.setPannable(true);
            root.setCenter(spNew);
        }
        updateStatus(); // refresh counts
    }

    // status text updater
    private void updateStatus() {
        int total = HOME.getBeds().size();
        long occupied = HOME.getBeds().stream().filter(Bed::isOccupied).count();
        long vacant = total - occupied;
        status.setText("Beds: " + total + " | Occupied: " + occupied + " | Vacant: " + vacant);
    }

    // ---------------- dialog handlers ----------------
    private void addResidentHere(Bed bed) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Add Resident");
        dlg.setHeaderText("Add new resident to " + bed.getBedId());
        dlg.setContentText("Format: id,name,gender(M/F),age");
        dlg.getEditor().setPromptText("R100,Sam,M,75");

        dlg.showAndWait().ifPresent(txt -> {
            try {
                String[] p = txt.split("\\s*,\\s*");
                Validators.require(p.length == 4, "Enter: id,name,gender(M/F),age");

                String id = p[0];
                String name = p[1];
                char g = Validators.parseGender(p[2]);          // validate gender
                int age = Validators.parseInt(p[3], "Age");     // validate age

                Resident r = new Resident(id, name, g, age);
                HOME.addResident(mgr, r, bed.getBedId());
                refreshUI();
            } catch (Exception ex) { error(ex); }
        });
    }

    private void moveSelectedResidentTo(Bed target) {
        if (selectedBed == null || !selectedBed.isOccupied()) {
            info("Move", "Select an occupied bed first.");
            return;
        }
        try {
            HOME.moveResident(nurse, selectedBed.getOccupant().getId(), target.getBedId());
            selectedBed = null;
            refreshUI();
        } catch (Exception ex) { error(ex); }
    }

    private void addPrescriptionFor(Resident r) {
        Dialog<List<String>> dlg = new Dialog<>();
        dlg.setTitle("Add Prescription for " + r.getName());
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gp = new GridPane();
        gp.setHgap(8); gp.setVgap(8);
        TextField med  = new TextField();
        TextField dose = new TextField("500mg");
        TextField time = new TextField("09:00");
        gp.addRow(0, new Label("Medicine:"), med);
        gp.addRow(1, new Label("Dosage:"), dose);
        gp.addRow(2, new Label("Time (HH:MM):"), time);
        dlg.getDialogPane().setContent(gp);

        dlg.setResultConverter(bt -> bt == ButtonType.OK ? List.of(med.getText(), dose.getText(), time.getText()) : null);

        dlg.showAndWait().ifPresent(vals -> {
            try {
                // validate inputs
                Validators.require(!vals.get(0).isBlank(), "Medicine required.");
                Validators.require(!vals.get(1).isBlank(), "Dosage required.");
                LocalTime t = Validators.parseTime(vals.get(2)); // validate time

                HOME.addPrescription(doctor, r.getId(), new Prescription(vals.get(0), vals.get(1), t, doctor.getId()));
                info("Prescription", "Added.");
            } catch (Exception ex) { error(ex); }
        });
    }

    // ---------------- helpers ----------------
    private void info(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null); a.setTitle(title); a.showAndWait();
    }

    private void error(Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage());
        a.setHeaderText("Error"); a.setTitle("Error"); a.showAndWait();
    }

    private void seed() {
        try {
            HOME.addStaff(mgr, nurse);
            HOME.addStaff(mgr, doctor);
            Resident r = new Resident("R1", "Sam", 'M', 75);
            HOME.addResident(mgr, r, "W1-R2-B1");
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) { launch(); }
}
