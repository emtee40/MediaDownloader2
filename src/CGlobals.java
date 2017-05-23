/**
 * Created by Robin on 09.06.2016.
 * <p>
 * Static class for global variables
 */
public class CGlobals {

    public static final String VERSION_STRING = "1.0b";
    public static OS CURRENT_OS = OS.Undefined;
    public static String PATH_SEPARATOR;

    public static void init() {
        // Get OS
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows"))
            CURRENT_OS = OS.Windows;
        else if (osName.toLowerCase().contains("nux"))
            CURRENT_OS = OS.Linux;
        else if (osName.toLowerCase().contains("mac"))
            CURRENT_OS = OS.OSX;
        else
            CURRENT_OS = OS.Undefined;

        // Separator
        switch (CURRENT_OS) {
            case Windows:
                PATH_SEPARATOR = "\\";
                break;
            case Linux:
                PATH_SEPARATOR = "/";
                break;
            case OSX:
                PATH_SEPARATOR = "/";
                break;
            default:
                PATH_SEPARATOR = "";
        }
    }
}

enum OS {
    Linux,
    Windows,
    OSX,
    Undefined
}