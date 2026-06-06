package models;

public class Game {
    private int id;
    private String title;
    private String description;
    private double price;
    private String category;
    private String developer;
    private int steamAppId;
    private String coverUrl;
    private String bannerUrl;

    public Game(int id, String title, String description, double price, String category, String developer, int steamAppId, String coverUrl, String bannerUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.developer = developer;
        this.steamAppId = steamAppId;
        this.coverUrl = coverUrl;
        this.bannerUrl = bannerUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getDeveloper() { return developer; }
    public int getSteamAppId() { return steamAppId; }
    public String getCoverUrl() { return coverUrl; }
    public String getBannerUrl() { return bannerUrl; }
}
