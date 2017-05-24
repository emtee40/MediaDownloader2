import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Created by dominik on 24.05.17.
 */
public class UIController {
    @FXML
    TextField txtUrl;
    @FXML
    Button btnAddToList;
    @FXML
    Button btnStart;

    @FXML
    protected void btnAddToListAction(){
        // Add URL to list
        /*
            1. Disable URL manipulation
            2. Use LinkHandler to validate the URL
            3. Add link to TableView
            4. Reset URL textbox and re-enable
         */
        String strToDownload = txtUrl.getText();

        // validate
        if(strToDownload.isEmpty() || !strToDownload.startsWith("http"))
        {
            ConsolePrinter.showAlert("No valid url", "Please enter a valid url",
                    "To add a url to the download queue, please enter a valid url like: http://example.com/exampleFile",
                    Alert.AlertType.ERROR);
            return;
        }

        txtUrl.setEditable(false);
        // analyze url
        LinkHandler.AnalyzeUrl(strToDownload);

        // re-enable
        txtUrl.setText("");
        txtUrl.setEditable(true);
    }

    @FXML
    protected void btnStartAction(){
        // Start the download for each item in list
    }
}
