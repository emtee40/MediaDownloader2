import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;

/**
 * Created by dominik on 23.05.17.
 */
public class ConsolePrinter {
    private static String WARNING_MESSAGE_PREFIX = "[WARNING - MediaDownloader] ";
    private static String ERROR_MESSAGE_PREFIX = "[ERROR - MediaDownloader] ";
    private static String DEBUG_MESSAGE_PREFIX = "[DEBUG - MediaDownloader] ";

    public static void printWarning(String strMessage){
        System.out.println(WARNING_MESSAGE_PREFIX + strMessage);
    }

    public static void printError(String strMessage){
        System.err.println(ERROR_MESSAGE_PREFIX + strMessage);
    }

    public static void printDebug(String strMessage) {
        System.out.println(DEBUG_MESSAGE_PREFIX + strMessage);
    }

    /**
     * Shows a messagebox to the user
     * @param strTitle
     * @param strHeader
     * @param strMessage
     * @param alertType
     */
    public static void showAlert(String strTitle, String strHeader, String strMessage, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(strTitle);
        alert.setHeaderText(strHeader);
        alert.setContentText(strMessage);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        setStageWindowIcon((Stage) alert.getDialogPane().getScene().getWindow());
        alert.showAndWait();
    }

    public static void showAbout(){
        String msg = "This tool allows you to download various files " +
                "from many social/video platforms (eg. YouTube, SoundCloud, Vimeo).\n\n" +
                "We also support Uploaded.to as a Premium link generator.\n" +
                "\nFor more information visit: http://r3d-soft.de";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About - MediaDownloader v" + CGlobals.VERSION_STRING);
        alert.setHeaderText("About MediaDownloader v" + CGlobals.VERSION_STRING);
        alert.setContentText(msg);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.initModality(Modality.APPLICATION_MODAL);
        setStageWindowIcon((Stage) alert.getDialogPane().getScene().getWindow());
        alert.showAndWait();
    }

    private static void setStageWindowIcon(Stage stageWindow){
        stageWindow.getIcons().add(new Image("resources/images/main_icon.png"));
    }
}
