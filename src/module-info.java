module ResidentHealthCareSystem {
    requires javafx.controls;
    requires javafx.fxml;

    // your packages:
    exports view;       // so the launcher is visible
    opens view to javafx.fxml; // if/when you use FXML
    exports model;      // handy for controllers that reference model classes
}
