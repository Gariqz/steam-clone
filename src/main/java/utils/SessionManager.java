package utils;

import models.User;

public class SessionManager {
    private static User currentUser;
    private static models.Game selectedGame;
    private static String statusColor = "#75b022"; // Default Online (Green)

    public static void loginUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setStatusColor(String color) {
        statusColor = color;
    }

    public static String getStatusColor() {
        return statusColor;
    }

    public static void setSelectedGame(models.Game game) {
        selectedGame = game;
    }

    public static models.Game getSelectedGame() {
        return selectedGame;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
