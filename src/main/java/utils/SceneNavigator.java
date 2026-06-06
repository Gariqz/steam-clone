package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneNavigator {
    private static Stage primaryStage;

    // Dipanggil sekali di Main.java saat start
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void setFullScreen(boolean value) {
        if (primaryStage != null) {
            primaryStage.setFullScreen(value);
        }
    }

    public static void switchTo(String fxmlPath, String title, double width, double height) {
        try {
            Parent root = FXMLLoader.load(SceneNavigator.class.getResource(fxmlPath));
            Scene scene = new Scene(root, width, height);
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            
            // Opsional: Bikin window-nya otomatis ke tengah layar pas ganti ukuran
            primaryStage.centerOnScreen(); 
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Gagal memuat halaman: " + fxmlPath);
            e.printStackTrace();
        }
    }
}