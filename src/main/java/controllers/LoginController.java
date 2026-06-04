package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private void handleLoginBtn(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        System.out.println("Tombol Login Diklik!");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        
        // Nanti logika nyambungin ke MySQL taruh di sini
    }
}