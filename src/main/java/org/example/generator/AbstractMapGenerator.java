package org.example.generator;

import java.util.*;

public abstract class AbstractMapGenerator implements MapGenerator {

    protected final int WIDTH;
    protected final int HEIGHT;
    protected final int MAX_ROOMS;
    protected final int MIN_ROOM_XY;
    protected final int MAX_ROOM_XY;
    protected final boolean ROOMS_OVERLAP;
    protected final int RANDOM_CONNECTIONS;
    protected final int RANDOM_SPURS;
    protected final List<Tile> TILES;
    protected final Tile[][] LEVEL;
    protected final List<int[]> ROOM_LIST;
    protected final List<int[][]> CORRIDOR_LIST;
    protected final List<String> TILES_LEVEL;
    private final Random RANDOM = new Random((System.currentTimeMillis() / 1000L));

    protected AbstractMapGenerator(int WIDTH, int HEIGHT, int MAX_ROOMS, int MIN_ROOM_XY, int MAX_ROOM_XY, boolean ROOMS_OVERLAP, int RANDOM_CONNECTIONS, int RANDOM_SPURS) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.MAX_ROOMS = MAX_ROOMS;
        this.MIN_ROOM_XY = MIN_ROOM_XY;
        this.MAX_ROOM_XY = MAX_ROOM_XY;
        this.ROOMS_OVERLAP = ROOMS_OVERLAP;
        this.RANDOM_CONNECTIONS = RANDOM_CONNECTIONS;
        this.RANDOM_SPURS = RANDOM_SPURS;
        this.TILES = new ArrayList<>(Arrays.asList(Tile.STONE, Tile.FLOOR, Tile.WALL));
        this.LEVEL = new Tile[this.WIDTH][this.HEIGHT];
        this.ROOM_LIST = new ArrayList<>();
        this.CORRIDOR_LIST = new ArrayList<>();
        this.TILES_LEVEL = new ArrayList<>();
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getMAX_ROOMS() {
        return MAX_ROOMS;
    }

    public int getMIN_ROOM_XY() {
        return MIN_ROOM_XY;
    }

    public int getMAX_ROOM_XY() {
        return MAX_ROOM_XY;
    }

    public boolean isROOMS_OVERLAP() {
        return ROOMS_OVERLAP;
    }

    public int getRANDOM_CONNECTIONS() {
        return RANDOM_CONNECTIONS;
    }

    public int getRANDOM_SPURS() {
        return RANDOM_SPURS;
    }

    public List<Tile> getTILES() {
        return TILES;
    }

    public Tile[][] getLEVEL() {
        return LEVEL;
    }

    public List<int[]> getROOM_LIST() {
        return ROOM_LIST;
    }

    public List<int[][]> getCORRIDOR_LIST() {
        return CORRIDOR_LIST;
    }

    public List<String> getTILES_LEVEL() {
        return TILES_LEVEL;
    }

    public Random getRANDOM() {
        return RANDOM;
    }

    abstract protected int[] genRoom();

    abstract protected boolean roomOverlapping(int[] room, List<int[]> roomList);

    abstract protected int[][] corridorBetweenPoints(int x1, int y1, int x2, int y2, JoinType joinType);

    abstract protected void joinRooms(int[] roomOne, int[] roomTwo, JoinType joinType);

}
