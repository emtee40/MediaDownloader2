import javafx.scene.control.Alert;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class which handles all links and acts as communication layer for each downloader
 */
public class LinkHandler {
    // TODO: rework mp4 stuff
    // TODO: rework table model
    // TODO: rework downloaders and add them here
    // TODO: LinkHandler just abstracts the layer of sending information to the server and responding to it with a user ui
                // => regarding Metadata

    //private static List<String> currentMp4Files = new ArrayList<>();

    /**
     * @deprecated
     * @param URL
     * @param hoster
     * @param window
     */
    public static void AddURLToTable(String URL, String hoster, FreshUI window) {
        /*
            TODO: review this code piece
        if (DownloadPage.valueOf(hoster.toString()) == DownloadPage.RE_Explorer) {
            new REExplorer(window.settingsManager);
        } */
        /* TODO: revalidate this code piece
        if (hoster.toLowerCase().equals("facebook")) {
            URL = URL.replace("photos_stream?tab=photos_stream", "");
            URL = URL.replace("photos_stream?tab=photos_albums", "");
            URL = URL.replace("photos_stream?tab=photos", "");


            // determine if real fb link
            if (URL.contains("/?type") && URL.contains(("&theater"))) {
                FacebookDownloader fb = new FacebookDownloader(URL);
                String[] urls = fb.GetDownloadLinks();
                for (int i = 0; i < urls.length; i++) {
                    AddToTableModel(window, urls[i], window.tlDownloadDomain.getSelectedItem().toString(),
                            0, window.settingsManager.GetRemoveGEMA(), window.settingsManager.GetRemoveMP4(),
                            window.settingsManager.GetStandardSavePath(), window.settingsManager.GetConvertToMP3());
                }
            } else if (URL.contains("facebook")) {
                FacebookDownloader fb = new FacebookDownloader(URL);
                String[] pic = fb.GetDownloadLinks();

                for (int i = 0; i < pic.length; i++) {
                    AddToTableModel(window, pic[i], window.tlDownloadDomain.getSelectedItem().toString(),
                            0, window.settingsManager.GetRemoveGEMA(), window.settingsManager.GetRemoveMP4(),
                            window.settingsManager.GetStandardSavePath(), window.settingsManager.GetConvertToMP3());
                }
            } else {
                JOptionPane.showMessageDialog(null, "No facebook link", "FacebookDownloader - Not a valid link", JOptionPane.ERROR_MESSAGE);
            }
        } else if (hoster.toLowerCase().equals("instagram")) {
            // Single Picture
            if (URL.contains("/p/") || URL.contains("?taken-by=")) {
                InstagramDownloader ig = new InstagramDownloader(URL, window.settingsManager.GetStandardSavePath(),
                        false);
                String url = ig.GetURLsAndPreview();
                AddToTableModel(window, url, window.tlDownloadDomain.getSelectedItem().toString(),
                        0, window.settingsManager.GetRemoveGEMA(), window.settingsManager.GetRemoveMP4(),
                        window.settingsManager.GetStandardSavePath(), window.settingsManager.GetConvertToMP3());
            } else {
                // Crawl Profile
                String[] username = URL.split("/");
                InstagramDownloader insta = new InstagramDownloader(username[3], window.settingsManager.GetStandardSavePath());
                String userID = insta.fetchUserID("https://api.instagram.com/v1/users/" +
                        "search?q={user}&client_id=21ae9c8b9ebd4183adf0d0602ead7f05");
                System.out.println("Found userID: " + userID);
                insta.setSavePath(window.settingsManager.GetStandardSavePath() + "/" + userID);
                String[] urls = insta.fetchAllImageURLs(userID, "");
                for (int i = 0; i < urls.length; i++) {
                    AddToTableModel(window, urls[i], window.tlDownloadDomain.getSelectedItem().toString(),
                            0, window.settingsManager.GetRemoveGEMA(), window.settingsManager.GetRemoveMP4(),
                            window.settingsManager.GetStandardSavePath() + CGlobals.PATH_SEPARATOR + userID,
                            window.settingsManager.GetConvertToMP3());
                }
            }
        } else if (hoster.toLowerCase().equals("mixcloud") || hoster.toLowerCase().equals("nowvideo")
                || hoster.toLowerCase().equals("sharedsx") || hoster.toLowerCase().equals("soundcloud")
                || hoster.toLowerCase().equals("streamcloud") || hoster.toLowerCase().equals("vimeo")) {

            AddToTableModel(window, URL, window.tlDownloadDomain.getSelectedItem().toString(),
                    0, window.settingsManager.GetRemoveGEMA(), window.settingsManager.GetRemoveMP4(),
                    window.settingsManager.GetStandardSavePath() + CGlobals.PATH_SEPARATOR,
                    window.settingsManager.GetConvertToMP3());

        } else if (hoster.toLowerCase().equals("vine")) {
            VineDownloader vine = new VineDownloader(URL);
            String[] list = vine.GetVines();
            for (int i = 0; i < list.length; i++) {
                AddToTableModel(window, list[i], window.tlDownloadDomain.getSelectedItem().toString(),
                        0, window.settingsManager.GetRemoveGEMA(), window.settingsManager.GetRemoveMP4(),
                        window.settingsManager.GetStandardSavePath() + CGlobals.PATH_SEPARATOR +
                                vine.GetUID() + CGlobals.PATH_SEPARATOR, window.settingsManager.GetConvertToMP3());
            }
        } else if (hoster.toLowerCase().equals("youtube")) {
            // if not a correct youtube name assume that text is a youtube username
            if (URL.contains("youtube.com/") && !URL.contains("youtube.com/watch?v=")
                    || URL.contains("youtube.com/user/")) {
                // now add all videos from a channel if it is a channel
                String username = URL.replace("youtube.com/", "")
                        .replace("https://", "").replace("http://", "").replace("www.", "")
                        .replace("user/", "").replace("/", "");

                YouTubeGetChannelVideos channelVideos = new YouTubeGetChannelVideos(username);
                String[] list = channelVideos.GetVideoList();

                if (list.length <= 0) {
                    JOptionPane.showMessageDialog(null, "No YouTube User matching your criteria",
                            "YouTubeDownloaderEngine - No user found!", JOptionPane.ERROR_MESSAGE);
                } else {
                    for (int i = 0; i < list.length; i++) {
                        AddToTableModel(window, "https://youtube.com/watch?v=" + list[i],
                                window.tlDownloadDomain.getSelectedItem().toString(),
                                0, window.settingsManager.GetRemoveGEMA(), window.settingsManager.GetRemoveMP4(),
                                window.settingsManager.GetStandardSavePath(), window.settingsManager.GetConvertToMP3());
                    }
                    JOptionPane.showMessageDialog(window,
                            "Added " + list.length + " videos recording to your download request: " +
                                    username, "MediaDownloader - Added all channel videos", JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (URL.contains("list=")) {
                YouTubeRetrievePlaylist retrievePlaylist = new YouTubeRetrievePlaylist(URL);
                String[] elements = retrievePlaylist.getAllVideosFromPlaylist("");
                for (int i = 0; i < elements.length; i++) {

                    AddToTableModel(window, elements[i], window.tlDownloadDomain.getSelectedItem().toString(),
                            0, window.settingsManager.GetRemoveGEMA(), window.settingsManager.GetRemoveMP4(),
                            window.settingsManager.GetStandardSavePath() + CGlobals.PATH_SEPARATOR +
                                    retrievePlaylist.getPlayListTitle(), window.settingsManager.GetConvertToMP3());

                }

                JOptionPane.showMessageDialog(null, "Added " + elements.length + " links to the downloader"
                        , "MediaDownloader - Added all playlist videos", JOptionPane.INFORMATION_MESSAGE);
            } else if (URL.contains("youtube.com/"))
                AddToTableModel(window, URL, window.tlDownloadDomain.getSelectedItem().toString(),
                        0, window.settingsManager.GetRemoveGEMA(), window.settingsManager.GetRemoveMP4(),
                        window.settingsManager.GetStandardSavePath(), window.settingsManager.GetConvertToMP3());
            else {
                JOptionPane.showMessageDialog(null, "No youtube link", "YouTubeDownloaderEngine - Not a valid link",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else
            return; // nothing i can do here cuz unknown host! should never happen (handled earlier in code) */
    }

    /**
     * @deprecated
     * @param window
     */
    public static void StartDownloading(FreshUI window) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    window.btnDownload.setEnabled(false);
                    window.btnDownload.setText("Download all!");

                    for (int i = 0; i < window.dTableModel.getRowCount(); i++) {
                        // Retrieve which hoster we have
                        DownloadPage hoster = DownloadPage.valueOf(window.dTableModel.getValueAt(i, 1).toString());

                        /*if (hoster == DownloadPage.Facebook) {
                            String url = window.dTableModel.getValueAt(i, 0).toString();
                            FacebookDownloader fbDownloader = new FacebookDownloader();
                            int size = fbDownloader.getDownloadSize(url);
                            fbDownloader.DownloadFile(url, size, i, window.dTableModel,
                                    window.dTableModel.getValueAt(i, window.dTableModel.getColumnCount() - 1).toString());

                        } else if (hoster == DownloadPage.Instagram) {
                            String url = window.dTableModel.getValueAt(i, 0).toString();
                            InstagramDownloader instagramDownloader = new
                                    InstagramDownloader(window.dTableModel.getValueAt(i, window.dTableModel.getColumnCount()
                                    - 1).toString());

                            int size = instagramDownloader.getDownloadSize(url);
                            instagramDownloader.DownloadFile(url, i, size, window.dTableModel);

                        } else {
                                //
                                JOptionPane.showMessageDialog(null, "Error determine OS in order to delete mp4 files." +
                                                "Please contact: admin@r3d-soft.de in order to fix this error!" +
                                                " Error message: Unkown OS!",
                                        "YouTubeDownloaderEngine - Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }*/
                    }
                    // JOB Finished Display
                    JOptionPane.showMessageDialog(window, "Downloaded every URL, you entered, successfully. When you dispose this message the download list will be cleared.",
                            "MediaDownloader - Job finished", JOptionPane.INFORMATION_MESSAGE);
                    window.btnDownload.setEnabled(true);
                    window.btnDownload.setText("Download all!");


                    for (int j = window.dTableModel.getRowCount() - 1; j > -1; j--) {
                        window.dTableModel.removeRow(j);
                    }
                } catch (Exception ex) {
                    // Job failed due to a reconnect
                    try {
                        for (int i = 0; i < 4; i++) {
                            Thread.sleep(10 * 1000);
                            if (netIsAvailable() && i <= 3) {
                                StartDownloading(window);
                            }
                        }
                        window.btnDownload.setEnabled(true);
                        window.btnDownload.setText("Continue download!");

                        JOptionPane.showMessageDialog(window, "There was a problem with your internet connection.\n" +
                                        "I tried to continue the download for myself,\nbut your internet connection was " +
                                        "longer away than 40 seconds so please hit 'Continue download' and continue",
                                "MediaDownloader - Internet connection failure", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        });
        t.start();
    }

    /**
     * Checks if google is reachable which is used to test if a proper network connection is available
     * @return boolean true if google is reachable; false if not
     */
    private static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @deprecated
     * @param window
     * @param url
     * @param hoster
     * @param progress
     * @param removeGema
     * @param removeVideo
     * @param SavePath
     * @param convertAudio
     */
    private static void AddToTableModel(FreshUI window, Object url, Object hoster, Object progress,
                                        Object removeGema, Object removeVideo, Object SavePath, Object convertAudio) {
        window.dTableModel.addRow(new Object[]{
                url,
                hoster, progress,
                removeGema,
                removeVideo,
                convertAudio,
                SavePath
        });
    }

    //public static void AddMp4ToList(String s) {
    //    currentMp4Files.add(s);
    //}

    /* New implementation for JavaFX */
    public static void getMetadata(String strToDownload){
        // TODO: declare api end node to return following data:
        // TODO: { success: true|false, errorMessage: null|String, isPlaylist: true|false , data: [ {title: string, url: string}, ...]
        // If playlist => data is populated with all urls in playlist => otherwise just echo input url => add them to table

        // To start the download another request is send for each url
        // api end node should retrieve a downloadable url inside json
        // TODO: attention in this node remember youtube saves the IP => replace the server ip with users public ip
    }
}