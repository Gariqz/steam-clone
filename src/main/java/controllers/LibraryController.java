package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;
import models.Game;
import models.User;
import utils.DatabaseConnection;
import utils.SceneNavigator;
import utils.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class LibraryController implements Initializable {

    @FXML private Label lblUsername;
    @FXML private Label lblWalletBalance;
    @FXML private ImageView imgNavAvatar;
    @FXML private GridPane libraryGrid;
    @FXML private HBox hboxInstalling;
    @FXML private ProgressIndicator progressInstall;
    @FXML private Label lblInstallStatus;
    @FXML private Label lblInstallTitle;

    private Map<Integer, Boolean> installationStatus = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateNavbar();
        loadOwnedGames();
    }

    private void updateNavbar() {
        if (SessionManager.isLoggedIn()) {
            User user = SessionManager.getCurrentUser();
            lblUsername.setText(user.getUsername());
            lblWalletBalance.setText("Balance: Rp " + String.format("%,.0f", user.getWalletBalance()));
            try {
                URL res = getClass().getResource("/images/" + user.getAvatarUrl());
                if (res != null) {
                    imgNavAvatar.setImage(new Image(res.toExternalForm()));
                    imgNavAvatar.setStyle("-fx-effect: dropshadow(three-pass-box, " + SessionManager.getStatusColor() + ", 10, 0.5, 0, 0);");
                }
            } catch (Exception e) {}
        }
    }

    private void loadOwnedGames() {
        List<Game> ownedGames = new ArrayList<>();
        installationStatus.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT g.*, ul.is_installed FROM games g JOIN user_library ul ON g.id = ul.game_id WHERE ul.user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, SessionManager.getCurrentUser().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Game g = new Game(
                    rs.getInt("id"), rs.getString("title"), rs.getString("description"),
                    rs.getDouble("price"), rs.getString("category"), rs.getString("developer"),
                    rs.getInt("steam_appid"), rs.getString("cover_url"), rs.getString("banner_url")
                );
                ownedGames.add(g);
                installationStatus.put(g.getId(), rs.getBoolean("is_installed"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        displayGames(ownedGames);
    }

    private void displayGames(List<Game> games) {
        libraryGrid.getChildren().clear();
        int column = 0, row = 0;
        for (Game game : games) {
            VBox card = createGameCard(game);
            libraryGrid.add(card, column, row);
            column++;
            if (column == 4) { column = 0; row++; }
        }
    }

    private VBox createGameCard(Game game) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #3b3a63; -fx-background-radius: 5;");
        card.setPrefWidth(220);

        ImageView iv = new ImageView();
        try {
            URL res = getClass().getResource("/images/" + game.getCoverUrl());
            if (res != null) iv.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) {}
        iv.setFitWidth(200); iv.setFitHeight(120); iv.setPreserveRatio(false);

        Label title = new Label(game.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
        
        boolean isInstalled = installationStatus.getOrDefault(game.getId(), false);
        
        HBox actions = new HBox(5);
        if (isInstalled) {
            Button btnPlay = new Button("PLAY");
            btnPlay.setStyle("-fx-background-color: #75b022; -fx-text-fill: white; -fx-font-weight: bold;");
            btnPlay.setOnAction(e -> { SessionManager.setSelectedGame(game); SceneNavigator.switchTo("/views/GameDetail.fxml", "Steam Clone - " + game.getTitle(), 1280, 720); });
            
            Button btnUninstall = new Button("UNINSTALL");
            btnUninstall.setStyle("-fx-background-color: #a32a2a; -fx-text-fill: white; -fx-font-size: 10;");
            btnUninstall.setOnAction(e -> handleUninstall(game));
            
            actions.getChildren().addAll(btnPlay, btnUninstall);
        } else {
            Button btnInstall = new Button("INSTALL");
            btnInstall.setStyle("-fx-background-color: #3b3a63; -fx-text-fill: white; -fx-font-weight: bold;");
            btnInstall.setOnAction(e -> handleInstall(game));
            actions.getChildren().add(btnInstall);
        }

        card.getChildren().addAll(iv, title, actions);
        return card;
    }

    private void handleInstall(Game game) {
        hboxInstalling.setVisible(true);
        lblInstallTitle.setText(game.getTitle());
        lblInstallStatus.setText("Downloading & Installing...");
        
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(300); // ~30 seconds
                }
                return null;
            }
        };

        progressInstall.progressProperty().bind(task.progressProperty());
        
        task.setOnSucceeded(e -> {
            updateInstallationDB(game.getId(), true);
            hboxInstalling.setVisible(false);
            loadOwnedGames();
        });

        new Thread(task).start();
    }

    private void handleUninstall(Game game) {
        updateInstallationDB(game.getId(), false);
        loadOwnedGames();
    }

    private void updateInstallationDB(int gameId, boolean status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE user_library SET is_installed = ? WHERE user_id = ? AND game_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setBoolean(1, status);
            ps.setInt(2, SessionManager.getCurrentUser().getId());
            ps.setInt(3, gameId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void goToStore() { SceneNavigator.switchTo("/views/Dashboard.fxml", "Steam Clone - Store", 1280, 720); }
    @FXML private void goToProfile() { SceneNavigator.switchTo("/views/Profile.fxml", "Steam Clone - Profile", 1280, 720); }
    @FXML private void goToWallet() { SceneNavigator.switchTo("/views/Wallet.fxml", "Steam Clone - Wallet", 1280, 720); }
    @FXML private void handleLogout() { SessionManager.logout(); SceneNavigator.setFullScreen(false); SceneNavigator.switchTo("/views/Login.fxml", "Steam Clone - Login", 800, 600); }
}
