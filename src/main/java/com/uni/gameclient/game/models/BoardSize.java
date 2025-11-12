package com.uni.gameclient.game.models;

public class BoardSize {
    private int rows;
    private int cols;

    public BoardSize() {}
    public BoardSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }
}
