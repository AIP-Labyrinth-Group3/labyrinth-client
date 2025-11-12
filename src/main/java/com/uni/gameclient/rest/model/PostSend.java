package com.uni.gameclient.rest.model;

public class PostSend {



    private String name;
    private String uri;
    private int maxPlayers;


    // Getter + Setter


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
}