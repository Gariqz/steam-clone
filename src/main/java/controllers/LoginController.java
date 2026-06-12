package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView; // Pastikan di-import kalau SceneNavigator beda package
import javafx.scene.layout.StackPane;
import utils.DatabaseConnection;
import utils.SceneNavigator;

public class LoginController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView bgImageView;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private void initialize() {
        if (rootPane != null && bgImageView != null) {
            bgImageView.fitWidthProperty().bind(rootPane.widthProperty());
            bgImageView.fitHeightProperty().bind(rootPane.heightProperty());
        }
    }

    @FXML
    private void handleLoginBtn(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Username dan Password tidak boleh kosong!");
            return;
        }

        try {
            Connection kon = DatabaseConnection.getConnection();
            if (kon == null) {
                showAlert(Alert.AlertType.ERROR, "Koneksi Error", "Gagal menyambung ke database sistem.");
                return;
            }

            String hashedPassword = utils.PasswordHasher.hashSHA256(password);

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = kon.prepareStatement(query);
            
            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                models.User loggedInUser = new models.User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getDouble("wallet_balance"),
                    rs.getString("avatar_url"),
                    rs.getString("profile_bg_url")
                );
                utils.SessionManager.loginUser(loggedInUser);

                Platform.runLater(() -> {
                    SceneNavigator.switchTo("/views/Dashboard.fxml", "Steam Clone - Dashboard", 1280, 720);
                    SceneNavigator.setFullScreen(true);
                });
                
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau Password yang Anda masukkan salah!");
            }

            rs.close();
            ps.close();
            kon.close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowRegister(ActionEvent event) {
        SceneNavigator.switchTo("/views/Register.fxml", "Steam Clone - Register", 800, 600);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}