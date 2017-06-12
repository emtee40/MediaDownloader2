import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsWindowController implements Initializable {
    @FXML
    TextField textPathFFMPEG;
    @FXML
    Button btnPathFFMPEG;
    @FXML
    Button cancelButton;

    SettingsManager man;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConsolePrinter.printDebug("Initializing settings window ...");

        // Disable window settings if under Unix like OS
        if(CGlobals.CURRENT_OS == OS.Linux || CGlobals.CURRENT_OS == OS.OSX){
            textPathFFMPEG.setText("Not needed under Unix like OS");
            textPathFFMPEG.setEditable(false);
            btnPathFFMPEG.setDisable(true);
        }

        // read in settings.ini
        man = new SettingsManager();
        textPathFFMPEG.setText(man.GetFFMPEGDir());
    }

    @FXML
    protected void btnChooseFFMPEG_Clicked(){
        ConsolePrinter.printDebug("Showing choose folder dialog");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(null);

        if(selectedDirectory == null){
            ConsolePrinter.showAlert("No directory selected", "No directory selected",
                    "There has been no FFMPEG directory selected. Converting will may not work properly",
                    Alert.AlertType.INFORMATION);
            ConsolePrinter.printDebug("No directory selected");
        }else{
            ConsolePrinter.printDebug("Directory selected: " + selectedDirectory.getAbsolutePath());
            textPathFFMPEG.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    protected void btnClose_Clicked(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void btnSave_Clicked(){
        // set each value
        if(CGlobals.CURRENT_OS == OS.Windows || CGlobals.CURRENT_OS == OS.Undefined)
            man.setFFMPEGPath(textPathFFMPEG.getText());

        // save whole settings into ini file
        man.saveSettings();

        // notice the user
        ConsolePrinter.showAlert("Saved successfully", "Settings have been saved!",
                "Your new settings have been successfully save", Alert.AlertType.INFORMATION);
    }
}
