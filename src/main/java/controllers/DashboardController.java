package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblUsername;
    @FXML private Label lblWalletBalance;
    @FXML private ImageView imgNavAvatar;
    @FXML private TextField txtSearch;
    @FXML private GridPane gameGrid;

    private List<Game> allGames = new ArrayList<>();
    private List<Integer> ownedGameIds = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadOwnedGameIds();
        updateNavbar();
        loadGamesFromDatabase();
        displayGames(allGames);
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
                    // Apply interactive aura border
                    imgNavAvatar.setStyle("-fx-effect: dropshadow(three-pass-box, " + SessionManager.getStatusColor() + ", 10, 0.5, 0, 0);");
                }
            } catch (Exception e) {}
        }
    }

    private void loadOwnedGameIds() {
        ownedGameIds.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT game_id FROM user_library WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, SessionManager.getCurrentUser().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ownedGameIds.add(rs.getInt("game_id"));
            }
        } catch (Exception e) {}
    }

    private void loadGamesFromDatabase() {
        allGames.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM games";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                allGames.add(new Game(
                    rs.getInt("id"), rs.getString("title"), rs.getString("description"),
                    rs.getDouble("price"), rs.getString("category"), rs.getString("developer"),
                    rs.getInt("steam_appid"), rs.getString("cover_url"), rs.getString("banner_url")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void displayGames(List<Game> games) {
        gameGrid.getChildren().clear();
        int column = 0, row = 0;
        for (Game game : games) {
            VBox card = createGameCard(game);
            gameGrid.add(card, column, row);
            column++;
            if (column == 4) { column = 0; row++; }
        }
    }

    private VBox createGameCard(Game game) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #3b3a63; -fx-background-radius: 5; -fx-cursor: hand;");
        card.setPrefWidth(200);

        ImageView iv = new ImageView();
        try {
            URL res = getClass().getResource("/images/" + game.getCoverUrl());
            if (res != null) iv.setImage(new Image(res.toExternalForm()));
        } catch (Exception e) {}
        iv.setFitWidth(180); iv.setFitHeight(110); iv.setPreserveRatio(false);

        Label title = new Label(game.getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");

        HBox priceBox = new HBox(10);
        if (ownedGameIds.contains(game.getId())) {
            Label owned = new Label("OWNED");
            owned.setStyle("-fx-background-color: #75b022; -fx-text-fill: white; -fx-font-size: 10; -fx-padding: 2 5; -fx-background-radius: 3;");
            priceBox.getChildren().add(owned);
        } else {
            String priceText = game.getPrice() == 0 ? "Free to Play" : "Rp " + String.format("%,.0f", game.getPrice());
            Label price = new Label(priceText);
            price.setStyle("-fx-text-fill: #FFFFFF;");
            priceBox.getChildren().add(price);
        }

        card.getChildren().addAll(iv, title, priceBox);
        card.setOnMouseClicked(event -> {
            SessionManager.setSelectedGame(game);
            SceneNavigator.switchTo("/views/GameDetail.fxml", "Steam Clone - " + game.getTitle(), 1280, 720);
        });
        return card;
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().toLowerCase();
        List<Game> filtered = allGames.stream()
                .filter(g -> g.getTitle().toLowerCase().contains(keyword) 
                        || g.getCategory().toLowerCase().contains(keyword)
                        || String.valueOf(g.getPrice()).contains(keyword))
                .toList();
        displayGames(filtered);
    }

    @FXML private void goToStore() { SceneNavigator.switchTo("/views/Dashboard.fxml", "Steam Clone - Store", 1280, 720); }
    @FXML private void goToLibrary() { SceneNavigator.switchTo("/views/Library.fxml", "Steam Clone - Library", 1280, 720); }
    @FXML private void goToProfile() { SceneNavigator.switchTo("/views/Profile.fxml", "Steam Clone - Profile", 1280, 720); }
    @FXML private void goToWallet() { SceneNavigator.switchTo("/views/Wallet.fxml", "Steam Clone - Wallet", 1280, 720); }
    @FXML private void handleLogout() { SessionManager.logout(); SceneNavigator.setFullScreen(false); SceneNavigator.switchTo("/views/Login.fxml", "Steam Clone - Login", 800, 600); }
}
