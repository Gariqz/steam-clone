package controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GameDetailController {

    @FXML private Button btnBack;
    @FXML private Button btnPlay;

    @FXML
    private void handlePlayGame() {
        try {
            // Menggunakan logic URI Steam App ID Apex Legends
            String steamUri = "steam://rungameid/291550";
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(steamUri));
                System.out.println("Apex Legends berhasil dipicu via Steam Clone!");
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        // Logic abang buat balik ke scene dashboard utama
        System.out.println("Kembali ke Dashboard...");
    }
}