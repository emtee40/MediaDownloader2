import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

/**
 * ConvertManager which handles all conversion
 */
public class ConvertManager {
    private static JSONArray convertList = new JSONArray();

    /**
     * Add a item to the conversion queue
     * @param itemName String name of the file
     * @param convertTo ConvertTypes defines into what format a file should be converted
     */
    public static void AddItemToQueue(String itemName, ConvertTypes convertTo, String savePath){
        JSONObject itemObj = new JSONObject();
        itemObj.put("itemName", itemName);
        itemObj.put("convertTo", convertTo.toString());
        itemObj.put("savePath", savePath);
        convertList.put(itemObj);
    }

    /**
     * Start the conversion
     */
    public static void StartConversion(){
        for (int i = 0; i < convertList.length(); i++) {
            JSONObject currentObj = convertList.getJSONObject(i);

            String ffmpeg = "";
            if(CGlobals.CURRENT_OS == OS.Linux || CGlobals.CURRENT_OS == OS.OSX){
                ffmpeg = "ffmpeg -i \"{INPUT}\" -vn -acodec libmp3lame -ac 2 -ab 320k -ar 48000 \"{OUTPUT}.mp3\"";
            } else if (CGlobals.CURRENT_OS == OS.Windows){
                SettingsManager settingsMan = new SettingsManager();
                ffmpeg = settingsMan.GetFFMPEGDir() +
                        "ffmpeg.exe -i \"{INPUT}\" -vn -acodec libmp3lame -ac 2 -ab 320k -ar 48000 \"{OUTPUT}.mp3\"";
            }
            try {
                if(ffmpeg.isEmpty())
                    throw new Exception("FFmpeg could not be found!");

                ffmpeg = ffmpeg.replace("{INPUT}",
                        currentObj.getString("savePath") + currentObj.getString("itemName"));
                ffmpeg = ffmpeg.replace("{OUTPUT}",
                        currentObj.getString("savePath") +
                                currentObj.getString("itemName").replace(".mp4", ""));


                Runtime.getRuntime().exec(ffmpeg);
            } catch (IOException e){
              ConsolePrinter.showAlert("I/O Error", "An I/O error occurred",
                      "Please check if FFmpeg is installed correctly. More information: \n" + e.getMessage(),
                      Alert.AlertType.ERROR);
            } catch (Exception e) {
                ConsolePrinter.printError(e.getMessage());
                ConsolePrinter.showAlert("FFmpeg missing", "FFmpeg could not be found",
                        "Please check your local settings and install ffmpeg if your using an Unix-like system",
                        Alert.AlertType.ERROR);
            }
        }
    }
}

enum ConvertTypes{
    MP3, MP4
}
