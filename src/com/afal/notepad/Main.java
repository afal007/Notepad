package com.afal.notepad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    static final String APP_NAME = "Java Notepad";
    static final String NEW_FILE_NAME = "Untitled";
    static final String MAIN_STAGE_TITLE = NEW_FILE_NAME + " - " + APP_NAME;
    private static final Image APP_ICON = new Image("file:res/icon.png");

    private static Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    static Stage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        mainStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("notepad.fxml"));
        primaryStage.setTitle(MAIN_STAGE_TITLE);
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.getIcons().setAll(APP_ICON);
        primaryStage.show();
    }
}
