import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsWindowController implements Initializable {
    @FXML
    TextField textPathFFMPEG;
    @FXML
    Button btnPathFFMPEG;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConsolePrinter.printDebug("Initializing settings window ...");

        // Disable window settings if under Unix like OS
        if(CGlobals.CURRENT_OS == OS.Linux || CGlobals.CURRENT_OS == OS.OSX){
            textPathFFMPEG.setText("Not needed under Unix like OS");
            textPathFFMPEG.setEditable(false);
            btnPathFFMPEG.setDisable(true);
        }
    }
}
