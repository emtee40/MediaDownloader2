import javafx.scene.control.TableView;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Dominik on 30.05.2017.
 */
public class Downloader {

    // Methods any Downloader need
    public static boolean isFileExisting(File fileToCheck) {
        try {
            if (fileToCheck.exists())
                return true;
            else
                return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static int getDownloadSize(String urls) {
        URLConnection hUrl;
        try {
            hUrl = new URL(urls).openConnection();
            int size = hUrl.getContentLength();
            return size;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String CheckSavePath(String pathToCheck) {
        if (CGlobals.CURRENT_OS == OS.Windows) {
            if (!pathToCheck.endsWith("\\")) {
                pathToCheck = pathToCheck + "\\";
            }

            if (!Files.isDirectory(Paths.get(pathToCheck))) {
                try {
                    Files.createDirectory(Paths.get(pathToCheck));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return pathToCheck;
        } else if (CGlobals.CURRENT_OS == OS.Linux) {
            if (!pathToCheck.endsWith("/"))
                pathToCheck = pathToCheck + "/";

            if (!Files.isDirectory(Paths.get(pathToCheck))) {
                try {
                    Files.createDirectory(Paths.get(pathToCheck));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return pathToCheck;
        } else
            return pathToCheck;
    }

    public static String validateFileName(String name) {
        if (name.contains("|"))
            name = name.replace("|", "_");

        if (name.contains(">"))
            name = name.replace(">", "_");

        if (name.contains("<"))
            name = name.replace("<", "_");

        if (name.contains("\""))
            name = name.replace("\"", "_");

        if (name.contains("?"))
            name = name.replace("?", "_");

        if (name.contains("*"))
            name = name.replace("*", "_");

        if (name.contains(":"))
            name = name.replace(":", "_");

        if (name.contains("\\\\"))
            name = name.replace("\\\\", "_");

        if (name.contains("/"))
            name = name.replace("/", "_");

        return name;
    }

    public static void DownloadFile(URLConnection con, String filename, int downloadSize, QueueItem download/*int i*/, TableView table) {
        try {
            HttpURLConnection connection = (HttpURLConnection) con;
            File outputFileCache = new File(filename);
            long downloadedSize = 0;
            long fileLength = 0;
            BufferedInputStream input = null;
            RandomAccessFile output = null;

            if (outputFileCache.exists()) {
                connection.setAllowUserInteraction(true);
                connection.setRequestProperty("Range", "bytes=" + outputFileCache.length() + "-");
            }

            connection.setConnectTimeout(14000);
            connection.setReadTimeout(20000);
            connection.connect();

            if (connection.getResponseCode() / 100 != 2)
                System.err.println("Unknown response code!");
            else {
                String connectionField = connection.getHeaderField("content-range");

                if (connectionField != null) {
                    String[] connectionRanges = connectionField.substring("bytes=".length()).split("-");
                    downloadedSize = Long.valueOf(connectionRanges[0]);
                }

                if (connectionField == null && outputFileCache.exists())
                    outputFileCache.delete();

                fileLength = connection.getContentLength() + downloadedSize;
                input = new BufferedInputStream(connection.getInputStream());
                output = new RandomAccessFile(outputFileCache, "rw");
                output.seek(downloadedSize);

                byte data[] = new byte[1024];
                int count = 0;
                int __progress = 0;

                while ((count = input.read(data, 0, 1024)) != -1
                        && __progress != 100) {
                    downloadedSize += count;
                    output.write(data, 0, count);
                    __progress = (int) ((downloadedSize * 100) / fileLength);

                    //dTableModel.setValueAt(__progress + "%", i, 2);
                    download.setProgress(__progress + "%");
                    table.refresh();
                }

                output.close();
                input.close();
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage() + " Error occured while downloading!");
        }
    }
}
