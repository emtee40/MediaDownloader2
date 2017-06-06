import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by dominik on 06.06.17.
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
            String ffmpeg = "ffmpeg -i {INPUT} -vn -acodec libmp3lame -ac 2 -ab 320k -ar 48000 {OUTPUT}.mp3";

            ffmpeg = ffmpeg.replace("{INPUT}",
                    currentObj.getString("savePath") + currentObj.getString("itemName"));
            ffmpeg = ffmpeg.replace("{OUTPUT}",
                    currentObj.getString("savePath") +
                            currentObj.getString("itemName").replace(".mp4", ""));

            try {
                Runtime.getRuntime().exec(ffmpeg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

enum ConvertTypes{
    MP3, MP4
}
