import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.json.JSONObject;

import java.io.Console;
import java.net.URL;
import java.rmi.server.ExportException;
import java.util.Queue;
import java.util.ResourceBundle;

/**
 * Created by dominik on 24.05.17.
 */
public class UIController implements Initializable {
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private SettingsManager settingsManager;

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
        tableQueue.getItems().add(new QueueItem((strToDownload.contains("youtu")) ? currentMetadata.getString("title")
                + ".mp4" : currentMetadata.getString("title") + ".mp3",
                strToDownload, "0%", settingsManager.GetStandardSavePath()));

        // get latest item and add to conversion queue
        QueueItem latestQueueItem = (QueueItem) tableQueue.getItems().get(tableQueue.getItems().size() - 1);
        if(latestQueueItem.getTitle().contains(".mp4"))
            ConvertManager.AddItemToQueue(Downloader.validateFileName(latestQueueItem.getTitle()), ConvertTypes.MP4,
                    latestQueueItem.getPath());

        // re-enable
        txtUrl.setText("");
        txtUrl.setEditable(true);
    }

    @FXML
    protected void btnStartAction(){
        Task task = new Task<Void>() {
            @Override
            protected void failed() {
                super.failed();

                ConsolePrinter.showAlert("Download error", "Error occurred while downloading",
                        "Couldn't finish download task!", Alert.AlertType.ERROR);
            }

            @Override
            protected Void call() throws Exception {
                // loop all items
                for (int i = 0; i < tableQueue.getItems().size(); i++) {
                    String downloadUrl = tableQueue.getItems().get(i).getUrl();
                    String fileName = Downloader.validateFileName(tableQueue.getItems().get(i).getTitle());

                    JSONObject currentLink = new LinkHandler().getDownloadUrl(downloadUrl);

                    if (currentLink.getBoolean("success")) {
                        String strdownloadLink = currentLink.getString("download_url");

                        Downloader.DownloadFile(new URL(strdownloadLink).openConnection(), settingsManager.GetStandardSavePath() +
                                        CGlobals.PATH_SEPARATOR + fileName
                                , Downloader.getDownloadSize(strdownloadLink), tableQueue.getItems().get(i), tableQueue);

                    } else {
                        Platform.runLater(() -> {
                            ConsolePrinter.showAlert("Error fetching download url", "API error",
                                    currentLink.getString("errorMessage"), Alert.AlertType.ERROR);
                        });
                        return null;
                    }
                }

                Platform.runLater(() -> {
                    ConsolePrinter.showAlert("Job finished", "Downloads successfully finished",
                            "All downloads have been successfully downloaded to: " + settingsManager.GetStandardSavePath(), Alert.AlertType.INFORMATION);
                });
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            ConsolePrinter.printDebug("Task finished!");
            ConvertManager.StartConversion();
        });
        new Thread(task).start();
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

        // init user settings
        settingsManager = new SettingsManager();
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

    @FXML
    protected void showAbout(){
        ConsolePrinter.showAbout();
    }

    @FXML
    protected void menuItemCloseClicked(){
        // check if everything is safe to exit

        //
        System.exit(0);
    }
}
