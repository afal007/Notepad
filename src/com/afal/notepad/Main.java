package com.afal.notepad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    static final String MAIN_STAGE_TITLE = "Untitled - Java Notepad";
    private static Stage mainStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        mainStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("notepad.fxml"));
        primaryStage.setTitle(MAIN_STAGE_TITLE);
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    static Stage getMainStage() {
        return mainStage;
    }
}
