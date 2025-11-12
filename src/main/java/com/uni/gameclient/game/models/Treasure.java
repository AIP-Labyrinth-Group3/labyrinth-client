package com.uni.gameclient.game.models;

public class Treasure {
    private int id;
    private String name;

    public Treasure(int id, String name) {
        this.id = id;
        this.name = name;
        if(this.name == null){
            this.name = "UNKNOWN";
        }
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
