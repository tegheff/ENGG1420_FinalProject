package com.example.loginui.eventManagement;

import com.example.loginui.AppState;
import com.example.loginui.CampusEventApplication;
import com.example.loginui.waitlistManagement.Booking;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EventManagementView {

    private final EventManager eventManager = AppState.getEventManager();
    private final TextArea outputArea = new TextArea();

    public Parent build() {

        Button mainManagementBtn = new Button("Main Management");
        mainManagementBtn.setOnAction(e -> goToMainManagement(mainManagementBtn));

        Button addEventBtn = new Button("Add Event");
        Button listEventsBtn = new Button("List All Events");
        Button searchBtn = new Button("Search by Title");
        Button cancelBtn = new Button("Cancel Event");
        Button updateBtn = new Button("Update Event");
        Button filterBtn = new Button("Filter by Type");

        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);

        addEventBtn.setOnAction(e -> addEvent());
        listEventsBtn.setOnAction(e -> listAllEvents());
        searchBtn.setOnAction(e -> searchByTitle());
        cancelBtn.setOnAction(e -> cancelEvent());
        updateBtn.setOnAction(e -> updateEvent());
        filterBtn.setOnAction(e -> filterByType());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(
                new Label("Event Management System"),
                mainManagementBtn,
                addEventBtn,
                listEventsBtn,
                searchBtn,
                cancelBtn,
                updateBtn,
                filterBtn,
                new Label("Output:"),
                outputArea
        );

        return layout;
    }

    private void goToMainManagement(Node source) {
        try {
            Parent root = FXMLLoader.load(CampusEventApplication.class.getResource("dashboard-view.fxml"));
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (Exception e) {
            e.printStackTrace();
            outputArea.appendText("Could not load main management view.\n");
        }
    }

    // ---- Methods copied from his HelloApplication, unchanged except no Stage ----

    private void listAllEvents() {
        outputArea.clear();

        if (eventManager.getEvents().isEmpty()) {
            outputArea.setText("No events found. Add some events first!");
        } else {
            String allEvents = eventManager.getAllEventsAsString();
            outputArea.setText(allEvents);
        }
    }

    private void  addEvent() {
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Add Event");
        dialog.setHeaderText("Enter event details");

        TextField eventIdField = new TextField();
        eventIdField.setPromptText("Event ID (e.g., E001)");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField dateField = new TextField();
        dateField.setPromptText("Date (YYYY-MM-DD)");

        TextField timeField = new TextField();
        timeField.setPromptText("Time (HH:MM, 24-hour)");

        TextField locationField = new TextField();
        locationField.setPromptText("Location");

        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity (number)");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Workshop", "Seminar", "Concert");
        typeBox.setPromptText("Event Type");

        Label extraLabel = new Label("Extra Info:");
        TextField extraField = new TextField();
        extraField.setPromptText("Topic/Speaker/Age Restriction");

        typeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                extraLabel.setText("Extra Info:");
                extraField.setPromptText("Topic/Speaker/Age Restriction");
                return;
            }
            switch (newVal) {
                case "Workshop" -> {
                    extraLabel.setText("Topic:");
                    extraField.setPromptText("Workshop topic");
                }
                case "Seminar" -> {
                    extraLabel.setText("Speaker:");
                    extraField.setPromptText("Speaker name");
                }
                case "Concert" -> {
                    extraLabel.setText("Age Restriction:");
                    extraField.setPromptText("e.g., 18+");
                }
                default -> {
                    extraLabel.setText("Extra Info:");
                    extraField.setPromptText("Topic/Speaker/Age Restriction");
                }
            }
        });

        VBox dialogLayout = new VBox(10);
        dialogLayout.getChildren().addAll(
                new Label("Event ID:"), eventIdField,
                new Label("Title:"), titleField,
                new Label("Date:"), dateField,
                new Label("Time:"), timeField,
                new Label("Location:"), locationField,
                new Label("Capacity:"), capacityField,
                new Label("Type:"), typeBox,
                extraLabel, extraField
        );
        dialogLayout.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(dialogLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    String eventId = eventIdField.getText();
                    String title = titleField.getText();
                    String dateText = dateField.getText().trim();
                    String timeText = timeField.getText().trim();
                    LocalDate date = LocalDate.parse(dateText);
                    LocalTime time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("H:mm"));
                    LocalDateTime dateTime = LocalDateTime.of(date, time);
                    String location = locationField.getText();
                    int capacity = Integer.parseInt(capacityField.getText());
                    String type = typeBox.getValue();
                    String extra = extraField.getText();

                    if (type == null) return null;

                    if (type.equals("Workshop")) {
                        return new Workshop(eventId, title, dateTime, location, capacity, Booking.STATUS_CONFIRMED, extra);
                    } else if (type.equals("Seminar")) {
                        return new Seminar(eventId, title, dateTime, location, capacity, Booking.STATUS_CONFIRMED, extra);
                    } else if (type.equals("Concert")) {
                        return new Concert(eventId, title, dateTime, location, capacity, Booking.STATUS_CONFIRMED, extra);
                    }
                } catch (Exception e) {
                    outputArea.appendText("Error creating event: " + e.getMessage() + "\n");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(event -> {
            eventManager.addEvent(event);
            outputArea.appendText("Event added: " + event.getTitle() + "\n");
        });
    }

    private void searchByTitle() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Events");
        dialog.setHeaderText("Search by title");
        dialog.setContentText("Keyword:");

        dialog.showAndWait().ifPresent(keyword -> {
            var results = eventManager.searchByTitle(keyword.trim());
            outputArea.setText("Search results for \"" + keyword + "\":\n\n"
                    + eventManager.getEventsAsString(results));
        });
    }

    private void filterByType() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Workshop", "Workshop", "Seminar", "Concert");
        dialog.setTitle("Filter Events");
        dialog.setHeaderText("Filter by event type");
        dialog.setContentText("Type:");

        dialog.showAndWait().ifPresent(type -> {
            var results = eventManager.filterByType(type);
            outputArea.setText(type + " events:\n\n" + eventManager.getEventsAsString(results));
        });
    }

    private void cancelEvent() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancel Event");
        dialog.setHeaderText("Cancel an event");
        dialog.setContentText("Event ID:");

        dialog.showAndWait().ifPresent(eventId -> {
            boolean cancelled = eventManager.cancelEvent(eventId.trim());
            if (cancelled) {
                outputArea.setText("Event " + eventId + " has been cancelled.");
            } else {
                outputArea.setText("Event not found for ID: " + eventId);
            }
        });
    }

    private void updateEvent() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Event");
        dialog.setHeaderText("Update event details");

        TextField idField = new TextField();
        idField.setPromptText("Event ID");
        TextField titleField = new TextField();
        titleField.setPromptText("New title");
        TextField locationField = new TextField();
        locationField.setPromptText("New location");
        TextField capacityField = new TextField();
        capacityField.setPromptText("New capacity");

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(10));
        dialogLayout.getChildren().addAll(
                new Label("Event ID:"), idField,
                new Label("New Title:"), titleField,
                new Label("New Location:"), locationField,
                new Label("New Capacity:"), capacityField
        );

        dialog.getDialogPane().setContent(dialogLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    String eventId = idField.getText().trim();
                    String newTitle = titleField.getText().trim();
                    String newLocation = locationField.getText().trim();
                    int newCapacity = Integer.parseInt(capacityField.getText().trim());

                    boolean updated = eventManager.updateEvent(eventId, newTitle, newLocation, newCapacity);
                    outputArea.setText(updated
                            ? "Event updated successfully for ID: " + eventId
                            : "Event not found for ID: " + eventId);
                } catch (NumberFormatException e) {
                    outputArea.setText("Capacity must be a valid number.");
                }
            }
        });
    }
}
