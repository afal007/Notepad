package com.afal.notepad;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class NotepadController implements Initializable {
    private boolean wasChanged = false;
    private Stage mainStage;
    private static final int CANCEL = -1;
    private static final int CONTINUE = 0;

    @FXML
    TextArea textArea;

    @FXML
    MenuBar menuBar;

    @FXML
    public void setWasChanged() {
        if(!wasChanged) {
            wasChanged = true;
            String[] titleParts = mainStage.getTitle().split("-");
            mainStage.setTitle(titleParts[0] + "*" + " - " + titleParts[1]);
        }
    }

    @FXML
    public void handleNewActionEvent(ActionEvent e) {
        if(wasChanged)
            if(showConfirmationDialog() == CANCEL)
                return;

        mainStage.setTitle(Main.MAIN_STAGE_TITLE);
        textArea.setText("");
        wasChanged = false;
    }

    private int showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Java Notepad");
        alert.setHeaderText("Do you want to save changes?");

        ButtonType save = new ButtonType("Save");
        ButtonType dontSave = new ButtonType("Don't save");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(save, dontSave, cancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent() || result.get() == cancel) {
            return CANCEL;
        } else if(result.get() == save) {
            //save
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
