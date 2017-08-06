package com.afal.notepad;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class NotepadController implements Initializable {
    private boolean wasChanged = false;
    private Stage mainStage;

    private File curFile;

    private enum Action {
        CANCEL,
        CONTINUE
    }

    private enum FileAction {
        SAVE,
        OPEN
    }

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
            if(getConfirmation() == Action.CANCEL)
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

        saveToCurFile();
    }

    @FXML
    public void handleSaveAsActionEvent() {
        curFile = getFile(FileAction.SAVE);
        if(curFile == null)
            return;

        saveToCurFile();
    }

    private void saveToCurFile() {
        try {
            FileIO.writeTo(curFile, textArea.getText());
            wasChanged = false;
            setCanonicalStageName();
        } catch (IOException e) {
            showErrorAlert(e.getMessage());
        }
    }

    private void setCanonicalStageName() {
        mainStage.setTitle((curFile != null
                ? curFile.getName()
                : Main.NEW_FILE_NAME) + " - " + Main.APP_NAME);
    }

    @FXML
    public void handleOpenActionEvent() {
        if(wasChanged)
            if(getConfirmation() == Action.CANCEL)
                return;

        try {
            curFile = getFile(FileAction.OPEN);
            if(curFile == null)
                return;

            String text = FileIO.readFrom(curFile);
            textArea.setText(text);
            wasChanged = false;
            setCanonicalStageName();
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

    private File getFile(FileAction action) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Text Documents", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        if(action == FileAction.OPEN) {
            chooser.setTitle("Open");
            return chooser.showOpenDialog(mainStage);
        } else {
            chooser.setTitle("Save As");
            chooser.setInitialFileName("*.txt");
            return chooser.showSaveDialog(mainStage);
        }
    }

    private Action getConfirmation() {
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
            return Action.CANCEL;
        } else if(result.get() == save) {
            handleSaveAsActionEvent();
        }

        return Action.CONTINUE;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainStage = Main.getMainStage();
        mainStage.setOnCloseRequest((e) -> {
            if(wasChanged)
                if(getConfirmation() == Action.CANCEL)
                    e.consume(); //Cancel close request
        });
    }
}
