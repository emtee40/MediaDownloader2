import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

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
        alert.showAndWait();
    }
}
