package controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.User;
import utils.DatabaseConnection;
import utils.SceneNavigator;
import utils.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.UUID;

public class WalletController implements Initializable {

    @FXML private Label lblBalance;
    @FXML private Label lblUsername;
    @FXML private Label lblWalletBalance;
    @FXML private VBox vboxVoucherResult;
    @FXML private ProgressIndicator progressLoading;
    @FXML private Label lblVoucherStatus;
    @FXML private HBox hboxVoucherCode;
    @FXML private ImageView imgNavAvatar;
    @FXML private TextField txtVoucherCodeDisplay;
    @FXML private TextField txtRedeemCode;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateNavbar();
        updateBalanceDisplay();
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
            } catch (Exception e) {
                System.out.println("Failed to load nav avatar.");
            }
        }
    }

    private void updateBalanceDisplay() {
        double balance = SessionManager.getCurrentUser().getWalletBalance();
        lblBalance.setText("Rp " + String.format("%,.0f", balance));
        lblWalletBalance.setText("Balance: Rp " + String.format("%,.0f", balance));
    }

    @FXML
    private void handleGenerateVoucher(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String rawData = btn.getUserData().toString();
        double nominal = Double.parseDouble(rawData);

        vboxVoucherResult.setVisible(true);
        progressLoading.setVisible(true);
        hboxVoucherCode.setVisible(false);
        lblVoucherStatus.setText("Generating Voucher Code...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                Thread.sleep(2000);
                return generateAndSaveVoucher(nominal);
            }
        };

        task.setOnSucceeded(e -> {
            String code = task.getValue();
            progressLoading.setVisible(false);
            lblVoucherStatus.setText("Voucher Created Successfully!");
            hboxVoucherCode.setVisible(true);
            txtVoucherCodeDisplay.setText(code);
        });

        new Thread(task).start();
    }

    private String generateAndSaveVoucher(double nominal) {
        String code = UUID.randomUUID().toString().substring(0, 18).toUpperCase();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO vouchers (voucher_code, nominal) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, code);
            ps.setDouble(2, nominal);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    @FXML
    private void handleCopyVoucher() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(txtVoucherCodeDisplay.getText());
        clipboard.setContent(content);
        
        showAlert(Alert.AlertType.INFORMATION, "Copied", "Voucher code copied to clipboard!");
    }

    @FXML
    private void handleRedeem() {
        String code = txtRedeemCode.getText().trim();
        if (code.isEmpty()) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String checkQuery = "SELECT * FROM vouchers WHERE voucher_code = ? AND is_redeemed = FALSE";
                PreparedStatement psCheck = conn.prepareStatement(checkQuery);
                psCheck.setString(1, code);
                ResultSet rs = psCheck.executeQuery();

                if (rs.next()) {
                    double nominal = rs.getDouble("nominal");
                    int userId = SessionManager.getCurrentUser().getId();

                    String updateVoucher = "UPDATE vouchers SET is_redeemed = TRUE, redeemed_by = ? WHERE voucher_code = ?";
                    PreparedStatement psUpVoucher = conn.prepareStatement(updateVoucher);
                    psUpVoucher.setInt(1, userId);
                    psUpVoucher.setString(2, code);
                    psUpVoucher.executeUpdate();

                    String updateWallet = "UPDATE users SET wallet_balance = wallet_balance + ? WHERE id = ?";
                    PreparedStatement psUpWallet = conn.prepareStatement(updateWallet);
                    psUpWallet.setDouble(1, nominal);
                    psUpWallet.setInt(2, userId);
                    psUpWallet.executeUpdate();

                    conn.commit();

                    SessionManager.getCurrentUser().setWalletBalance(SessionManager.getCurrentUser().getWalletBalance() + nominal);
                    updateBalanceDisplay();
                    txtRedeemCode.clear();

                    showAlert(Alert.AlertType.INFORMATION, "Redeem Success", "Success! Your wallet has been topped up by Rp " + String.format("%,.0f", nominal));
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Code", "Voucher code is invalid or already used.");
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "An error occurred during redemption.");
        }
    }

    @FXML private void goToStore() { SceneNavigator.switchTo("/views/Dashboard.fxml", "Steam Clone - Store", 1280, 720); }
    @FXML private void goToLibrary() { SceneNavigator.switchTo("/views/Library.fxml", "Steam Clone - Library", 1280, 720); }
    @FXML private void goToProfile() { SceneNavigator.switchTo("/views/Profile.fxml", "Steam Clone - Profile", 1280, 720); }
    @FXML private void handleLogout() { SessionManager.logout(); SceneNavigator.switchTo("/views/Login.fxml", "Steam Clone - Login", 800, 600); }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
