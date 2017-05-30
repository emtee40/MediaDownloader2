import javafx.collections.ObservableList;

/**
 * Created by Dominik on 29.05.2017.
 */
public class QueueItem {
    private String Title;
    private String Url;
    private String Progress;
    private String Path;

    public QueueItem(String strTitle, String strUrl, String strProgress, String strPathOnDisk){
        this.Title = strTitle;
        this.Url = strUrl;
        this.Progress = strProgress;
        this.Path = strPathOnDisk;
    }

    public String getTitle() {
        return Title;
    }

    public String getUrl() {
        return Url;
    }

    public String getProgress() {
        return Progress;
    }

    public void setProgress(String prog) {
        this.Progress = prog;
    }

    public String getPath() {
        return Path;
    }
}
