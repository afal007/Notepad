package com.afal.notepad;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class NotepadController implements Initializable {
    private boolean wasChanged = false;
    private Stage mainStage;
    private static final int CANCEL = -1;
    private static final int CONTINUE = 0;

    private File curFile;

    @FXML
    TextArea textArea;

    @FXML
    MenuBar menuBar;

    @FXML
    public void setWasChanged() {
        if(!wasChanged) {
            wasChanged = true;
            String[] titleParts = mainStage.getTitle().split(" - ");
            mainStage.setTitle(titleParts[0] + "*" + " - " + titleParts[1]);
        }
    }

    @FXML
    public void handleNewActionEvent() {
        if(wasChanged)
            if(showConfirmationDialog() == CANCEL)
                return;

        mainStage.setTitle(Main.MAIN_STAGE_TITLE);
        textArea.setText("");
        wasChanged = false;
    }

    @FXML
    public void handleSaveActionEvent() {
        if(!wasChanged)
            return;

        if(curFile == null) {
            handleSaveAsActionEvent();
            return;
        }

        try {
            FileIO.writeTo(curFile, textArea.getText());
            wasChanged = false;
            mainStage.setTitle(curFile.getName() + " - " + Main.APP_NAME);
        } catch (IOException e) {
            showErrorAlert(e.getMessage());
        }
    }

    @FXML
    public void handleSaveAsActionEvent() {
        if(!wasChanged)
            return;


    }

    @FXML
    public void handleOpenActionEvent() {
        if(wasChanged)
            if(showConfirmationDialog() == CANCEL)
                return;

        try {
            curFile = getFileToOpen();
            if(curFile == null)
                return;

            String text = FileIO.readFrom(curFile);
            textArea.setText(text);
            wasChanged = false;
            mainStage.setTitle(curFile.getName() + " - " + Main.APP_NAME);
        } catch (IOException e) {
            showErrorAlert("Couldn't read from specified file.");
        }
    }

    private void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Main.APP_NAME);
        alert.setHeaderText(text);
        alert.show();
    }

    private File getFileToOpen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open");
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Text Documents", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        return chooser.showOpenDialog(mainStage);
    }

    private int showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Java Notepad");
        alert.setHeaderText("Do you want to save changes" +
                (curFile != null
                        ? " to " + curFile.getAbsolutePath() + "?"
                        : "?"));

        ButtonType save = new ButtonType("Save");
        ButtonType dontSave = new ButtonType("Don't save");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(save, dontSave, cancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() == cancel) {
            return CANCEL;
        } else if(result.get() == save) {
            handleSaveAsActionEvent();
        }

        return CONTINUE;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainStage = Main.getMainStage();
        mainStage.setOnCloseRequest((e) -> {
            if(wasChanged)
                if(showConfirmationDialog() == CANCEL)
                    e.consume(); //Cancel close request
        });
    }
}
