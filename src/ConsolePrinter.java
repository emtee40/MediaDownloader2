/**
 * Created by dominik on 23.05.17.
 */
public class ConsolePrinter {
    private static String WARNING_MESSAGE_PREFIX = "[WARNING - MediaDownloader] ";
    private static String ERROR_MESSAGE_PREFIX = "[ERROR - MediaDownloader] ";

    public static void printWarning(String strMessage){
        System.out.println(WARNING_MESSAGE_PREFIX + strMessage);
    }

    public static void printError(String strMessage){
        System.err.println(ERROR_MESSAGE_PREFIX + strMessage);
    }
}
