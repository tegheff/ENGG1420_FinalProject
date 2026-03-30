package com.example.loginui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CampusEventApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AppState.init();
        FXMLLoader fxmlLoader = new FXMLLoader(CampusEventApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Login.");
        stage.setScene(scene);
        stage.show();
    }
}
