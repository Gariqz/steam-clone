package models;

public class User {
    private int id;
    private String username;
    private String email;
    private double walletBalance;
    private String avatarUrl;
    private String profileBgUrl;

    public User(int id, String username, String email, double walletBalance, String avatarUrl, String profileBgUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.walletBalance = walletBalance;
        this.avatarUrl = avatarUrl;
        this.profileBgUrl = profileBgUrl;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getProfileBgUrl() { return profileBgUrl; }
}
