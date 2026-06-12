package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneNavigator {
    private static Stage primaryStage;

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
            String standardizedPath = fxmlPath.startsWith("/") ? fxmlPath : "/" + fxmlPath;
            URL fxmlUrl = SceneNavigator.class.getResource(standardizedPath);
            
            if (fxmlUrl == null) {
                fxmlUrl = SceneNavigator.class.getClassLoader().getResource(standardizedPath.substring(1));
            }

            if (fxmlUrl == null) {
                throw new IOException("FXML file not found at: " + standardizedPath);
            }
            
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene currentScene = primaryStage.getScene();
            
            if (currentScene != null) {
                currentScene.setRoot(root);
                
                if (!primaryStage.isMaximized() && !primaryStage.isFullScreen()) {
                    primaryStage.setWidth(width);
                    primaryStage.setHeight(height);
                    primaryStage.centerOnScreen();
                }
            } else {
                Scene scene = new Scene(root, width, height);
                primaryStage.setScene(scene);
            }
            
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("CRITICAL: Gagal memuat halaman: " + fxmlPath);
            e.printStackTrace();
            
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