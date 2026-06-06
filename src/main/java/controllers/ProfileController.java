package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import models.User;
import utils.DatabaseConnection;
import utils.SceneNavigator;
import utils.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private Label lblProfileUsername;
    @FXML private Label lblProfileEmail;
    @FXML private Label lblUsernameNavbar;
    @FXML private Label lblWalletBalance;
    @FXML private Label lblStatus;
    @FXML private ImageView imgAvatar;
    @FXML private ImageView imgNavAvatar;
    @FXML private TextField txtEditUsername;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            updateNavbar();
            lblProfileUsername.setText(user.getUsername());
            lblProfileEmail.setText(user.getEmail());
            txtEditUsername.setText(user.getUsername());
            
            loadAvatar(user.getAvatarUrl());

            // Default effect
            imgAvatar.setStyle("-fx-effect: dropshadow(three-pass-box, #75b022, 20, 0, 0, 0);");
        }
    }

    private void updateNavbar() {
        User user = SessionManager.getCurrentUser();
        lblUsernameNavbar.setText(user.getUsername());
        lblWalletBalance.setText("Balance: Rp " + String.format("%,.0f", user.getWalletBalance()));
        
        try {
            URL res = getClass().getResource("/images/" + user.getAvatarUrl());
            if (res != null) {
                imgNavAvatar.setImage(new Image(res.toExternalForm()));
            }
        } catch (Exception e) {
            System.out.println("Failed to load nav avatar.");
        }
    }

    @FXML
    private void handleStatusChange(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String colorStr = btn.getUserData().toString();
        String statusText = btn.getText();

        lblStatus.setText(statusText);
        lblStatus.setTextFill(javafx.scene.paint.Color.web(colorStr));
        
        // Update Session for Global Navbar Aura
        SessionManager.setStatusColor(colorStr);
        
        // Dynamic "Aura" effect
        imgAvatar.setStyle("-fx-effect: dropshadow(three-pass-box, " + colorStr + ", 25, 0.5, 0, 0);");
        imgNavAvatar.setStyle("-fx-effect: dropshadow(three-pass-box, " + colorStr + ", 10, 0.5, 0, 0);");
        
        System.out.println("Status changed to: " + statusText);
    }

    private void loadAvatar(String avatarFileName) {
        try {
            URL res = getClass().getResource("/images/" + avatarFileName);
            if (res != null) {
                imgAvatar.setImage(new Image(res.toExternalForm()));
            }
        } catch (Exception e) {
            System.out.println("Failed to load avatar: " + avatarFileName);
        }
    }

    @FXML
    private void handleSaveUsername() {
        String newName = txtEditUsername.getText().trim();
        if (newName.isEmpty()) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE users SET username = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, newName);
            ps.setInt(2, SessionManager.getCurrentUser().getId());
            ps.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Username updated! Please re-login to see full changes.");
            lblProfileUsername.setText(newName);
            lblUsernameNavbar.setText(newName);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update username.");
        }
    }

    @FXML private void goToStore() { SceneNavigator.switchTo("/views/Dashboard.fxml", "Steam Clone - Store", 1280, 720); }
    @FXML private void goToLibrary() { SceneNavigator.switchTo("/views/Library.fxml", "Steam Clone - Library", 1280, 720); }
    @FXML private void goToWallet() { SceneNavigator.switchTo("/views/Wallet.fxml", "Steam Clone - Wallet", 1280, 720); }
    @FXML private void handleLogout() { SessionManager.logout(); SceneNavigator.switchTo("/views/Login.fxml", "Steam Clone - Login", 800, 600); }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
