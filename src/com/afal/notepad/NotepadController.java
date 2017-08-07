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

/**
 * Controller for main stage. Contains handlers for menu buttons.
 * @author Alexander Fal (falalexandr007@gmail.com)
 */
public class NotepadController implements Initializable {
    @FXML
    TextArea textArea;
    @FXML
    MenuBar menuBar;
    private boolean wasChanged = false;
    private Stage mainStage;
    private File curFile;

/*
* Add asterisk to title and set variable.
* */
    private void setWasChanged() {
        if(!wasChanged) {
            wasChanged = true;
            String[] titleParts = mainStage.getTitle().split(" - ");
            mainStage.setTitle(titleParts[0] + "*" + " - " + titleParts[1]);
        }
    }

    /**
     * Handles New menu item action event.
     * If document was changed shows confirmation dialog.
     * Resets workspace.
     */
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

    /**
     * Handles Save menu item action event.
     * If document wasn't changed - do nothing.
     * Write to current document or let user choose location if it wasn't specified yet.
     * @return internal enum element. If cancel button was pressed Cancel, else Continue. It is needed to correctly
     * stop window close request if cancel button is pressed.
     */
    @FXML
    public Action handleSaveMenuItem() {
        if(!wasChanged)
            return Action.CANCEL;

        if(curFile == null) {
            return handleSaveAsMenuItem();
        }

        saveToCurFile();
        return Action.CONTINUE;
    }

    /**
     * Use file chooser to specify location to save document.
     * @return internal enum element. If cancel button was pressed Cancel, else Continue. It is needed to correctly
     * stop window close request if cancel button is pressed.
     */
    @FXML
    public Action handleSaveAsMenuItem() {
        curFile = getFile(FileAction.SAVE);
        if(curFile == null)
            return Action.CANCEL;

        saveToCurFile();
        return Action.CONTINUE;
    }

/*
* Use helper method from util class.
* Reset was changed state.
* */
    private void saveToCurFile() {
        try {
            FileIO.writeTo(curFile, textArea.getText());
            wasChanged = false;
            setCanonicalStageName();
        } catch (IllegalArgumentException e) {
            showErrorAlert(e.getMessage());
        }
    }

/*
* Untitled - Java Notepad
* or
* curFile - Java Notepad
* */
    private void setCanonicalStageName() {
        mainStage.setTitle((curFile != null
                ? curFile.getName()
                : Main.NEW_FILE_NAME) + " - " + Main.APP_NAME);
    }

    /**
     * Use file chooser to specify document to open.
     * Show confirmation dialog if current document was changed but wasn't saved.
     * Read text from specified file into {@link TextArea}.
     * Reset was changed state.
     */
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

    /**
     * Fire {@link WindowEvent#WINDOW_CLOSE_REQUEST}
     */
    @FXML
    public void handleExitMenuItem() {
        mainStage.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

/*
* Set up and show alert dialog with specified text string.
* */
    private void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(mainStage);
        alert.setTitle(Main.APP_NAME);
        alert.setHeaderText(text);
        alert.show();
    }


/*
* Set up and show File Chooser save or open dialog depending on specified argument.
* */
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

/*
* Set up and show confirmation dialog if current document was changed but wasn't saved
* */
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
            return handleSaveMenuItem();
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

    /**
     * Set up and show simple dialog with text about application.
     */
    @FXML
    public void handleAboutMenuItem() {
        Dialog dialog = new Alert(Alert.AlertType.NONE);
        dialog.getDialogPane().getButtonTypes().add(
                new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));

        dialog.initOwner(mainStage);
        dialog.setTitle(Main.APP_NAME);
        dialog.setContentText("Java Notepad v1.0\nAuthor: Alexander Fal (falalexandr007@gmail.com)");
        dialog.show();
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

/*
* Internal enum used for handling situations in which Cancel button should prevent application from closing.
* */
    private enum Action {
        CANCEL,
        CONTINUE
    }

/*
* Used to generify getFile method
* */
    private enum FileAction {
        SAVE,
        OPEN
    }
}
