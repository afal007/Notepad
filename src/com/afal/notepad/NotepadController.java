package com.afal.notepad;

import com.afal.notepad.file.FileIO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class NotepadController implements Initializable {
    @FXML
    TextArea textArea;
    @FXML
    MenuBar menuBar;
    private boolean wasChanged = false;
    private Stage mainStage;
    private File curFile;

    private void setWasChanged() {
        if(!wasChanged) {
            wasChanged = true;
            String[] titleParts = mainStage.getTitle().split(" - ");
            mainStage.setTitle(titleParts[0] + "*" + " - " + titleParts[1]);
        }
    }

    @FXML
    public void handleNewMenuItem() {
        if(wasChanged)
            if(getConfirmation() == Action.CANCEL)
                return;

        textArea.setText("");
        mainStage.setTitle(Main.MAIN_STAGE_TITLE);
        wasChanged = false;
        curFile = null;
    }

    @FXML
    public void handleSaveMenuItem() {
        if(!wasChanged)
            return;

        if(curFile == null) {
            handleSaveAsMenuItem();
            return;
        }

        saveToCurFile();
    }

    @FXML
    public void handleSaveAsMenuItem() {
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
        } catch (IllegalArgumentException e) {
            showErrorAlert(e.getMessage());
        }
    }

    private void setCanonicalStageName() {
        mainStage.setTitle((curFile != null
                ? curFile.getName()
                : Main.NEW_FILE_NAME) + " - " + Main.APP_NAME);
    }

    @FXML
    public void handleOpenMenuItem() {
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

    @FXML
    public void handleExitMenuItem() {
        mainStage.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(mainStage);
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
        alert.initOwner(mainStage);
        alert.setHeaderText("Do you want to save changes" +
                (curFile != null
                        ? " to " + curFile.getAbsolutePath() + "?"
                        : "?"));

        ButtonType save = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType dontSave = new ButtonType("Don't save", ButtonBar.ButtonData.NO);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(save, dontSave, cancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() == cancel) {
            return Action.CANCEL;
        } else if(result.get() == save) {
            handleSaveMenuItem();
        }

        return Action.CONTINUE;
    }

    @FXML
    public void handleCopyMenuItem() {
        textArea.copy();
    }

    @FXML
    public void handleCutMenuItem() {
        textArea.cut();
    }

    @FXML
    public void handlePasteMenuItem() {
        textArea.paste();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainStage = Main.getMainStage();
        mainStage.setOnCloseRequest((e) -> {
            if(wasChanged)
                if(getConfirmation() == Action.CANCEL)
                    e.consume(); //Cancel close request
        });
        textArea.textProperty().addListener((observable -> setWasChanged()));
    }

    private enum Action {
        CANCEL,
        CONTINUE
    }

    private enum FileAction {
        SAVE,
        OPEN
    }
}
