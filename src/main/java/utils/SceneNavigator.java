package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

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
            // Ensure path starts with / for absolute resource loading
            String standardizedPath = fxmlPath.startsWith("/") ? fxmlPath : "/" + fxmlPath;
            URL fxmlUrl = SceneNavigator.class.getResource(standardizedPath);
            
            if (fxmlUrl == null) {
                // Fallback to simpler loading if first attempt fails
                fxmlUrl = SceneNavigator.class.getClassLoader().getResource(standardizedPath.substring(1));
            }

            if (fxmlUrl == null) {
                throw new IOException("FXML file not found at: " + standardizedPath);
            }
            
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root, width, height);
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            
            primaryStage.centerOnScreen(); 
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("CRITICAL: Gagal memuat halaman: " + fxmlPath);
            e.printStackTrace();
            
            // Extract the deepest cause for better error reporting
            Throwable cause = e;
            while (cause.getCause() != null && cause.getCause() != cause) {
                cause = cause.getCause();
            }
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load page: " + fxmlPath);
            alert.setContentText("Error detail: " + cause.toString());
            alert.showAndWait();
        }
    }
}