import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.net.URL;
import java.security.Key;
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
    @FXML
    javafx.scene.control.TableView<QueueItem> tableFinished;

    // Queue Items columns
    @FXML
    TableColumn<QueueItem, String> titleCol;
    @FXML
    TableColumn<QueueItem, String> urlCol;
    @FXML
    TableColumn<QueueItem, String> progressCol;
    @FXML
    TableColumn<QueueItem, String> pathCol;

    // Finished Items columns
    @FXML
    TableColumn<QueueItem, String> finishedTitle;
    @FXML
    TableColumn<QueueItem, String> finishedUrl;
    @FXML
    TableColumn<QueueItem, String> finishedPath;

    @FXML
    protected void btnAddToListAction(){
        // Add URL to list
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
        if(currentMetadata.getBoolean("success")) {
            tableQueue.getItems().add(new QueueItem((strToDownload.contains("youtu")) ? currentMetadata.getString("title")
                    + ".mp4" : currentMetadata.getString("title") + ".mp3",
                    strToDownload, "0%", settingsManager.GetStandardSavePath()));

            // get latest item and add to conversion queue
            QueueItem latestQueueItem = tableQueue.getItems().get(tableQueue.getItems().size() - 1);
            if (latestQueueItem.getTitle().contains(".mp4"))
                ConvertManager.AddItemToQueue(Downloader.validateFileName(latestQueueItem.getTitle()), ConvertTypes.MP4,
                        latestQueueItem.getPath());
        } else {
            ConsolePrinter.printError("Metadata could not be retrieved");
            ConsolePrinter.showAlert("Error retrieving metadata", "Metadata - Server error",
                    currentMetadata.getString("errorMessage"), Alert.AlertType.ERROR);
        }

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
                    QueueItem currentItem = tableQueue.getItems().get(i);
                    String downloadUrl = currentItem.getUrl();
                    String fileName = Downloader.validateFileName(currentItem.getTitle());

                    JSONObject currentLink = new LinkHandler().getDownloadUrl(downloadUrl);

                    if (currentLink.getBoolean("success")) {
                        String strdownloadLink = currentLink.getString("download_url");

                        Downloader.DownloadFile(new URL(strdownloadLink).openConnection(), settingsManager.GetStandardSavePath() +
                                        CGlobals.PATH_SEPARATOR + fileName
                                , Downloader.getDownloadSize(strdownloadLink), currentItem, tableQueue);

                        // download finished => move to finished downloads
                        tableFinished.getItems().add(currentItem);
                        tableQueue.getItems().remove(i);
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
                            "All downloads have been successfully downloaded to: " + settingsManager.GetStandardSavePath() +
                                    "\n\n" +
                            "We are now converting those files into a given format - if any.", Alert.AlertType.INFORMATION);
                });
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            ConsolePrinter.printDebug("Download-Task finished!");

            ConsolePrinter.printDebug("Conversion-Task started!");
            ConvertManager.StartConversion();
            ConsolePrinter.printDebug("Conversion-Task finished!");
        });
        new Thread(task).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set cell factory for each table
        titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        urlCol.setCellValueFactory(new PropertyValueFactory<>("Url"));
        progressCol.setCellValueFactory(new PropertyValueFactory<>("Progress"));
        pathCol.setCellValueFactory(new PropertyValueFactory<>("Path"));

        finishedTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        finishedUrl.setCellValueFactory(new PropertyValueFactory<>("Url"));
        finishedPath.setCellValueFactory(new PropertyValueFactory<>("Path"));

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

    public void deleteFinishRow(){
        ObservableList<QueueItem> itemSelected, allItems;
        allItems = tableFinished.getItems();
        itemSelected = tableFinished.getSelectionModel().getSelectedItems();
        allItems.removeAll(itemSelected);
    }

    @FXML
    private void tableQueueKeyPressed(javafx.scene.input.KeyEvent e){
        if(e.getCode() == KeyCode.DELETE){
            deleteQueueRow();
        }
    }

    @FXML
    private void tableFinishKeyPressed(javafx.scene.input.KeyEvent e){
        if(e.getCode() == KeyCode.DELETE){
            deleteFinishRow();
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

    @FXML
    protected void showSettings(){
        ConsolePrinter.printDebug("Displaying settings window");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("mediadownloader_settings.fxml"));
            /*
            * if "fx:controller" is not set in fxml
            * fxmlLoader.setController(NewWindowController);
            */
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("MediaDownloader v" + CGlobals.VERSION_STRING + " - Settings");
            stage.setScene(scene);
            stage.getIcons().add(new Image(this.getClass().getResource("resources/images/main_icon.png").toString()));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            ConsolePrinter.printError("Failed to create settings window");
        }
    }
}
