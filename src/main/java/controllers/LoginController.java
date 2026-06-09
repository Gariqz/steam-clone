package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utils.DatabaseConnection;
import utils.SceneNavigator; // Pastikan di-import kalau SceneNavigator beda package

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

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

            // Gunakan tabel 'users' sesuai skema terbaru
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = kon.prepareStatement(query);
            
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Simpan user ke SessionManager
                models.User loggedInUser = new models.User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getDouble("wallet_balance"),
                    rs.getString("avatar_url"),
                    rs.getString("profile_bg_url")
                );
                utils.SessionManager.loginUser(loggedInUser);

                // showAlert is blocking, better to switch scene then show welcome message or vice versa
                // But let's try to ensure navigation happens.
                
                Platform.runLater(() -> {
                    // Pindah ke Dashboard
                    SceneNavigator.switchTo("/views/Dashboard.fxml", "Steam Clone - Dashboard", 1280, 720);
                    
                    // Set to Full Screen after scene switch
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

    // Method helper untuk mempersingkat penulisan pop-up Alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Menunggu sampai user klik tombol OK/Close
    }
}