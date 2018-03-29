package com.hoxo.main;

import com.hoxo.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initUI(primaryStage);
        primaryStage.show();
    }

    private void initUI(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainApp.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        controller = loader.getController();
        primaryStage.setTitle("Simulation");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
    }

    @Override
    public void stop() throws Exception {
    }
}
