package com.afal.notepad;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;


public class NotepadController implements Initializable {
    private boolean wasChanged = false;
    private Stage mainStage;

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
            showWasChangedDialog();

        mainStage.setTitle(Main.MAIN_STAGE_TITLE);
        textArea.setText("");
        wasChanged = false;
    }

    private void showWasChangedDialog() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainStage = Main.getMainStage();
    }
}
