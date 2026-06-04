import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.SceneNavigator;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Daftarkan stage utama ke navigator
        SceneNavigator.setPrimaryStage(primaryStage);
        
        // 2. Load halaman Login pertama kali (bukan GameDetail)
        Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        Scene scene = new Scene(root, 800, 600);
        
        primaryStage.setTitle("Steam Clone - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}