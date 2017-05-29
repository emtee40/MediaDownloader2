import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.json.JSONObject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dominik on 24.05.17.
 */
public class UIController implements Initializable {
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    @FXML
    TextField txtUrl;
    @FXML
    Button btnAddToList;
    @FXML
    Button btnStart;
    @FXML
    javafx.scene.control.TableView<QueueItem> tableQueue;

    // Queue Items columns
    @FXML
    TableColumn<QueueItem, String> titleCol;
    @FXML
    TableColumn<QueueItem, String> urlCol;
    @FXML
    TableColumn<QueueItem, String> progressCol;
    @FXML
    TableColumn<QueueItem, String> pathCol;



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
        JSONObject currentMetadata = new LinkHandler().getMetadata(strToDownload);
        tableQueue.getItems().add(new QueueItem(currentMetadata.getString("title"), strToDownload, "0%", "C:\\Users\\Dominik\\Desktop"));

        // re-enable
        txtUrl.setText("");
        txtUrl.setEditable(true);
    }

    @FXML
    protected void btnStartAction(){
        // Start the download for each item in list
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("Url"));
        progressCol.setCellValueFactory(new PropertyValueFactory<>("Progress"));
        pathCol.setCellValueFactory(new PropertyValueFactory<>("Path"));

        // init enter add to list
        txtUrl.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)  {
                btnAddToListAction();
            }
        });
    }

    public void deleteQueueRow(){
        ObservableList<QueueItem> itemSelected, allItems;
        allItems = tableQueue.getItems();
        itemSelected = tableQueue.getSelectionModel().getSelectedItems();
        allItems.removeAll(itemSelected);
    }

    @FXML
    private void tableQueueKeyPressed(javafx.scene.input.KeyEvent e){
        if(e.getCode() == KeyCode.DELETE){
            deleteQueueRow();
        }
    }
}
