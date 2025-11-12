package com.uni.gameclient.game.database;

import java.time.LocalDateTime;

public class Gameserver {
    private String id;
    private String name;
    private String uri;
    private int maxPlayers;
    private int currentPlayerCount;
    private String status;
    private LocalDateTime lastSeen;
    private Boolean serverActive;

    public Gameserver() {
        // Leerer Konstruktor für Frameworks / DB
    }

    public Gameserver(String id, String name, String uri,
                      int maxPlayers, int currentPlayerCount,
                      String status, LocalDateTime lastSeen, Boolean serverActive) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.maxPlayers = maxPlayers;
        this.currentPlayerCount = currentPlayerCount;
        this.status = status;
        this.lastSeen = lastSeen;
        this.serverActive = serverActive;
    }

    // --- Getter & Setter (für TableView, JSON, DB) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public int getCurrentPlayerCount() { return currentPlayerCount; }
    public void setCurrentPlayerCount(int currentPlayerCount) { this.currentPlayerCount = currentPlayerCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    public Boolean getServerActive() { return serverActive; }
    public void setServerActive(Boolean serverActive) { this.serverActive = serverActive; }

    @Override
    public String toString() {
        return String.format("%s (%s) [%s: %d/%d]",
                name, uri, status, currentPlayerCount, maxPlayers);
    }
}
