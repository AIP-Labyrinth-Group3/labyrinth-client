package com.uni.gameclient.game.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


public class Tile {
    private List<String> entrances;
    @JsonIgnore
    private String type;
    private Treasure treasure;
    private Bonus bonus;

    public Tile(List<String> entrances, String type) {
        this.entrances = entrances;
        this.treasure = new Treasure(0,"0");
        this.bonus = new Bonus("0","0");


    }
   public List<String> getEntrances() {
        return entrances;
    }

    public void setEntrances(List<String> entrances) {
        this.entrances = entrances;
    }

    public Treasure getTreasure() {
        return treasure;
    }

    public void setTreasure(Treasure treasure) {
        this.treasure = treasure;
    }

    public Bonus getBonus() {
        return bonus;
    }

    public void setBonus(Bonus bonus) {
        this.bonus = bonus;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
