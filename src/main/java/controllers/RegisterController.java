package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import utils.DatabaseConnection;
import utils.SceneNavigator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView bgImageView;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtReenterPassword;

    @FXML
    private void initialize() {
        if (rootPane != null && bgImageView != null) {
            bgImageView.fitWidthProperty().bind(rootPane.widthProperty());
            bgImageView.fitHeightProperty().bind(rootPane.heightProperty());
        }
    }

    @FXML
    private void handleRegisterBtn(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String reenterPassword = txtReenterPassword.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || reenterPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "All fields must be filled!");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid email address!");
            return;
        }

        if (!password.equals(reenterPassword)) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Passwords do not match!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Cannot connect to the database!");
                return;
            }

            String checkUserSql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement psUser = conn.prepareStatement(checkUserSql)) {
                psUser.setString(1, username);
                try (ResultSet rsUser = psUser.executeQuery()) {
                    if (rsUser.next()) {
                        showAlert(Alert.AlertType.WARNING, "Registration Failed", "Username is already taken.");
                        return;
                    }
                }
            }

            String checkEmailSql = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement psEmail = conn.prepareStatement(checkEmailSql)) {
                psEmail.setString(1, email);
                try (ResultSet rsEmail = psEmail.executeQuery()) {
                    if (rsEmail.next()) {
                        showAlert(Alert.AlertType.WARNING, "Registration Failed", "Email is already registered.");
                        return;
                    }
                }
            }

            // Insert new user
            String insertSql = "INSERT INTO users (username, email, password, wallet_balance, avatar_url, profile_bg_url) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, username);
                psInsert.setString(2, email);
                psInsert.setString(3, password);
                psInsert.setDouble(4, 0.0);
                psInsert.setString(5, "animated_avatar.gif");
                psInsert.setString(6, "");

                int rowsAffected = psInsert.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Account successfully registered!");
                    SceneNavigator.switchTo("/views/Login.fxml", "Steam Clone - Login", 800, 600);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Registration failed. Please try again.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "System Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowLogin(ActionEvent event) {
        SceneNavigator.switchTo("/views/Login.fxml", "Steam Clone - Login", 800, 600);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}