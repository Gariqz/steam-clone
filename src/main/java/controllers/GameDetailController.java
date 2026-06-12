package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Game;
import models.User;
import utils.DatabaseConnection;
import utils.SceneNavigator;
import utils.SessionManager;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class GameDetailController implements Initializable {

    @FXML private Label lblGameTitle;
    @FXML private Label lblCategory;
    @FXML private Label lblActionTitle;
    @FXML private Label lblPrice;
    @FXML private Label lblDescription;
    @FXML private Label lblUsername;
    @FXML private Label lblWalletBalance;
    @FXML private ImageView imgNavAvatar;
    @FXML private ImageView imgBanner;
    @FXML private Button btnAction;

    private Game currentGame;
    private boolean isOwned = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentGame = SessionManager.getSelectedGame();
        if (currentGame != null) {
            updateNavbar();
            populateGameData();
            checkOwnership();
        }
    }

    private void updateNavbar() {
        if (SessionManager.isLoggedIn()) {
            User user = SessionManager.getCurrentUser();
            lblUsername.setText(user.getUsername());
            lblWalletBalance.setText("Balance: Rp " + String.format("%,.0f", user.getWalletBalance()));
            try {
                URL res = getClass().getResource("/images/avatars/" + user.getAvatarUrl());
                if (res != null) {
                    imgNavAvatar.setImage(new Image(res.toExternalForm()));
                    imgNavAvatar.setStyle("-fx-effect: dropshadow(three-pass-box, " + SessionManager.getStatusColor() + ", 10, 0.5, 0, 0);");
                }
            } catch (Exception e) {}
        }
    }

    private void populateGameData() {
        lblGameTitle.setText(currentGame.getTitle());
        lblCategory.setText(currentGame.getCategory());
        lblDescription.setText(currentGame.getDescription());
        String priceText = currentGame.getPrice() == 0 ? "Free to Play" : "Rp " + String.format("%,.0f", currentGame.getPrice());
        lblPrice.setText(priceText);
        lblActionTitle.setText("Get " + currentGame.getTitle());
        try {
            URL res = getClass().getResource("/images/games/" + currentGame.getBannerUrl());
            if (res != null) imgBanner.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) {}
    }

    private void checkOwnership() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM user_library WHERE user_id = ? AND game_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, SessionManager.getCurrentUser().getId());
            ps.setInt(2, currentGame.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isOwned = true;
                btnAction.setText("PLAY NOW");
                lblPrice.setText("In Library");
            } else {
                isOwned = false;
                btnAction.setText(currentGame.getPrice() == 0 ? "ADD TO LIBRARY" : "BUY NOW");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleAction() {
        if (isOwned) { launchSteamGame(); } else { buyOrAddGame(); }
    }

    private void launchSteamGame() {
        try {
            String steamUri = "steam://rungameid/" + currentGame.getSteamAppId();
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(steamUri));
            }
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed to launch Steam."); }
    }

    private void buyOrAddGame() {
        User user = SessionManager.getCurrentUser();
        if (user.getWalletBalance() < currentGame.getPrice()) {
            showAlert(Alert.AlertType.WARNING, "Insufficient Balance", "Your wallet balance is too low.");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String insertLib = "INSERT INTO user_library (user_id, game_id) VALUES (?, ?)";
                PreparedStatement psLib = conn.prepareStatement(insertLib);
                psLib.setInt(1, user.getId()); psLib.setInt(2, currentGame.getId());
                psLib.executeUpdate();
                if (currentGame.getPrice() > 0) {
                    String updateWallet = "UPDATE users SET wallet_balance = wallet_balance - ? WHERE id = ?";
                    PreparedStatement psWallet = conn.prepareStatement(updateWallet);
                    psWallet.setDouble(1, currentGame.getPrice()); psWallet.setInt(2, user.getId());
                    psWallet.executeUpdate();
                    user.setWalletBalance(user.getWalletBalance() - currentGame.getPrice());
                }
                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", currentGame.getTitle() + " added to library!");
                updateNavbar(); checkOwnership();
            } catch (Exception e) { conn.rollback(); throw e; }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void goToLibrary() { SceneNavigator.switchTo("/views/Library.fxml", "Steam Clone - Library", 1280, 720); }
    @FXML private void goToProfile() { SceneNavigator.switchTo("/views/Profile.fxml", "Steam Clone - Profile", 1280, 720); }
    @FXML private void goToWallet() { SceneNavigator.switchTo("/views/Wallet.fxml", "Steam Clone - Wallet", 1280, 720); }
    @FXML private void handleLogout() { SessionManager.logout(); SceneNavigator.setFullScreen(false); SceneNavigator.switchTo("/views/Login.fxml", "Steam Clone - Login", 800, 600); }
    @FXML private void handleBack() { SceneNavigator.switchTo("/views/Dashboard.fxml", "Steam Clone - Store", 1280, 720); }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType); alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }
}
