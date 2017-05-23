import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Dominik on 23.05.2017.
 */
public class FreshUI2 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mediadownloader_ui.fxml"));

        Scene scene = new Scene(root, 640, 400);
        primaryStage.setTitle("MediaDownloader v2.0.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
