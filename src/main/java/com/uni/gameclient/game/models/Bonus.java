package com.uni.gameclient.game.models;

public class Bonus {
    private String id;
    private String type;

    public Bonus(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
