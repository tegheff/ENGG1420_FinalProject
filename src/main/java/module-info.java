module com.example.eventmanagementapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.eventmanagementapp to javafx.fxml;
    exports com.example.eventmanagementapp;
}