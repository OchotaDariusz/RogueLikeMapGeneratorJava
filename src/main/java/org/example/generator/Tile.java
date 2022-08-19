package org.example.generator;

public enum Tile {
    STONE(" "),
    FLOOR("."),
    WALL("#");

    private final String tile;

    Tile(String tile) {
        this.tile = tile;
    }

    public String getTile() {
        return tile;
    }

    @Override
    public String toString() {
        return getTile();
    }
}
