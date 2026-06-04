package controllers;

import javafx.fxml.FXML;
import utils.SceneNavigator; 

public class DashboardController {

    @FXML
    private void handleLogout() {
        System.out.println("User Logout. Kembali ke halaman login...");
        
        // Kembalikan ukuran scene ke ukuran login semula (800x600)
        SceneNavigator.switchTo("/views/Login.fxml", "Steam Clone - Login", 800, 600);
    }
}