package controllers;

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

        // Validasi input kosong (UX dasar)
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Username dan Password tidak boleh kosong!");
            return;
        }

        try {
            // 1. Buka Koneksi
            Connection kon = DatabaseConnection.getConnection();
            if (kon == null) {
                showAlert(Alert.AlertType.ERROR, "Koneksi Error", "Gagal menyambung ke database sistem.");
                return;
            }

            // 2. Siapkan Peluru Query
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement ps = kon.prepareStatement(query);
            
            // 3. Masukkan Data ke Titik Tanya (?)
            ps.setString(1, username);
            ps.setString(2, password);

            // 4. Tarik Pelatuknya
            ResultSet rs = ps.executeQuery();

            // 5. Cek Hasilnya
            if (rs.next()) {
                // Tarik data email dari database sekadar buat contoh
                String emailUser = rs.getString("email");
                
                // Pop-up ini akan menahan thread sebentar sampai user klik "OK"
                showAlert(Alert.AlertType.INFORMATION, "Login Sukses", 
                        "Selamat datang kembali, " + username + "!\nEmail: " + emailUser);
                
                // 6. EKSEKUSI PINDAH KE DASHBOARD
                // Arahkan ke file FXML Dashboard abang (sesuaikan ukurannya ke 950x600 biar pas)
                SceneNavigator.switchTo("/views/Dashboard.fxml", "Steam Clone - Dashboard", 950, 600);
                
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau Password yang Anda masukkan salah!");
            }

            // Bagian menutup resource SQL (Good Practice)
            rs.close();
            ps.close();
            kon.close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "Terjadi kesalahan: " + e.getMessage());
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