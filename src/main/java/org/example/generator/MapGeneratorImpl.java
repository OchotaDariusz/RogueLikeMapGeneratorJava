package org.example.generator;

import java.util.*;

public class MapGeneratorImpl implements MapGenerator {
    private final int WIDTH;
    private final int HEIGHT;
    private final int MAX_ROOMS;
    private final int MIN_ROOM_XY;
    private final int MAX_ROOM_XY;
    private final boolean ROOMS_OVERLAP;
    private final int RANDOM_CONNECTIONS;
    private final int RANDOM_SPURS;
    private final List<Tile> TILES;
    private final Tile[][] LEVEL;
    private final List<int[]> ROOM_LIST;
    private final List<int[][]> CORRIDOR_LIST;
    private final List<String> TILES_LEVEL;
    private final Random RANDOM = new Random();

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

    public MapGeneratorImpl(int WIDTH, int HEIGHT, int MAX_ROOMS, int MIN_ROOM_XY, int MAX_ROOM_XY, boolean ROOMS_OVERLAP, int RANDOM_CONNECTIONS, int RANDOM_SPURS) {
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

    private int[] genRoom() {
        int w = getRANDOM().nextInt(getMIN_ROOM_XY(), getMAX_ROOM_XY() + 1);
        int h = getRANDOM().nextInt(getMIN_ROOM_XY(), getMAX_ROOM_XY() + 1);
        int x = getRANDOM().nextInt(1, (getWIDTH() - w));
        int y = getRANDOM().nextInt(1, (getHEIGHT() - h));
        return new int[]{x, y, w, h};
    }

    private boolean roomOverlapping(int[] room, List<int[]> roomList) {
        int x = room[0];
        int y = room[1];
        int w = room[2];
        int h = room[3];

        for (int[] currentRoom : roomList) {
            if ((x < (currentRoom[0] + currentRoom[2])) &&
                    currentRoom[0] < (x + w) &&
                    y < (currentRoom[1] + currentRoom[3]) &&
                    currentRoom[1] < (y + h)) {
                return true;
            }
        }
        return false;
    }

    private int[][] corridorBetweenPoints(int x1, int y1, int x2, int y2, JoinType joinType) {
        //noinspection ConditionCoveredByFurtherCondition
        if ((x1 == x2 && y1 == y2) || (x1 == x2) || (y1 == y2)) {
            return new int[][]{{x1, y1}, {x2, y2}};
        }
        JoinType join = joinType;

        Set<int[]> setA = new HashSet<>(Collections.singleton(new int[]{0, 1}));
        Set<int[]> setB = new HashSet<>(Collections.singleton(new int[]{x1, x2, y1, y2}));
        Set<int[]> setC = new HashSet<>(Collections.singleton(new int[]{getWIDTH() - 1, getWIDTH() - 2}));
        Set<int[]> setD = new HashSet<>(Collections.singleton(new int[]{x1, x2}));
        Set<int[]> setE = new HashSet<>(Collections.singleton(new int[]{getHEIGHT() - 1, getHEIGHT() - 2}));
        Set<int[]> setF = new HashSet<>(Collections.singleton(new int[]{y1, y2}));

        if (joinType.equals(JoinType.EITHER) && setA.retainAll(setB)) {
            join = JoinType.BOTTOM;
        } else if ((joinType.equals(JoinType.EITHER) && setC.retainAll(setD)) || setE.retainAll(setF)) {
            join = JoinType.TOP;
        } else if (joinType.equals(JoinType.EITHER)) {
            if (getRANDOM().nextInt(1, 3) == 1) {
                join = JoinType.TOP;
            } else {
                join = JoinType.BOTTOM;
            }
        }

        if (join.equals(JoinType.TOP)) {
            return new int[][]{{x1, y1}, {x1, y2}, {x2, y2}};
        } else if (join.equals(JoinType.BOTTOM)) {
            return new int[][]{{x1, y1}, {x2, y1}, {x2, y2}};
        }
        return new int[][]{{x1, y1}, {x2, y1}, {x2, y2}};
    }

    private void joinRooms(int[] roomOne, int[] roomTwo, JoinType joinType) {
        int x1, y1, w1, h1, x1_2, y1_2;
        int x2, y2, w2, h2, x2_2, y2_2;
        int jx1, jx2, jy1, jy2;
        int[][] corridors;
        int[] tmpX, tmpY;
        int[][] sortedRoom = new int[][]{roomOne, roomTwo};
        // python: sorted_room.sort(key=lambda x_y: x_y[0])
        Arrays.sort(sortedRoom, Comparator.comparingInt(x_y -> x_y[0]));
        x1 = sortedRoom[0][0];
        y1 = sortedRoom[0][1];
        w1 = sortedRoom[0][2];
        h1 = sortedRoom[0][3];
        x1_2 = x1 + w1 - 1;
        y1_2 = y1 + h1 - 1;

        x2 = sortedRoom[1][0];
        y2 = sortedRoom[1][1];
        w2 = sortedRoom[1][2];
        h2 = sortedRoom[1][3];
        x2_2 = x2 + w2 - 1;
        y2_2 = y2 + h2 - 1;

        if (x1 < (x2 + w2) && x2 < (x1 + w1)) {
            jx1 = getRANDOM().nextInt(x2, x1_2 + 1);
            jx2 = jx1;
            tmpY = new int[]{y1, y2, y1_2, y2_2};
            Arrays.sort(tmpY);
            jy1 = tmpY[1] + 1;
            jy2 = tmpY[2] - 1;
            corridors = corridorBetweenPoints(jx1, jy1, jx2, jy2, joinType);
            getCORRIDOR_LIST().add(corridors);
        } else if (y1 < (y2 + h2) && y2 < (y1 + h1)) {
            if (y2 > y1) {
                jy1 = getRANDOM().nextInt(y2, y1_2 + 1);
                jy2 = jy1;
            } else {
                jy1 = getRANDOM().nextInt(y1, y2_2 + 1);
                jy2 = jy1;
            }

            tmpX = new int[]{x1, x2, x1_2, x2_2};
            Arrays.sort(tmpX);
            jx1 = tmpX[1] + 1;
            jx2 = tmpX[2] - 1;
            corridors = corridorBetweenPoints(jx1, jy1, jx2, jy2, joinType);
            getCORRIDOR_LIST().add(corridors);
        } else {
            JoinType join = joinType;
            if (joinType.equals(JoinType.EITHER)) {
                if (getRANDOM().nextInt(1, 3) == 1) {
                    join = JoinType.TOP;
                } else {
                    join = JoinType.BOTTOM;
                }
            }

            if (join.equals(JoinType.TOP)) {
                if (y2 > y1) {
                    jx1 = x1_2 + 1;
                    jy1 = getRANDOM().nextInt(y1, y1_2 + 1);
                    jx2 = getRANDOM().nextInt(x2, x2_2 + 1);
                    jy2 = y2 - 1;
                    corridors = corridorBetweenPoints(jx1, jy1, jx2, jy2, JoinType.BOTTOM);
                    getCORRIDOR_LIST().add(corridors);
                } else {
                    jx1 = getRANDOM().nextInt(x1, x1_2 + 1);
                    jy1 = y1 - 1;
                    jx2 = x2 - 1;
                    jy2 = getRANDOM().nextInt(y2, y2_2 + 1);
                    corridors = corridorBetweenPoints(jx1, jy1, jx2, jy2, JoinType.TOP);
                    getCORRIDOR_LIST().add(corridors);
                }
            } else if (join.equals(JoinType.BOTTOM)) {
                if (y2 > y1) {
                    jx1 = getRANDOM().nextInt(x1, x1_2 + 1);
                    jy1 = y1_2 + 1;
                    jx2 = x2 - 1;
                    jy2 = getRANDOM().nextInt(y2, y2_2 + 1);
                    corridors = corridorBetweenPoints(jx1, jy1, jx2, jy2, JoinType.TOP);
                    getCORRIDOR_LIST().add(corridors);
                } else {
                    jx1 = x1_2 + 1;
                    jy1 = getRANDOM().nextInt(y1, y1_2 + 1);
                    jx2 = getRANDOM().nextInt(x2, x2_2 + 1);
                    jy2 = y2_2 + 1;
                    corridors = corridorBetweenPoints(jx1, jy1, jx2, jy2, JoinType.BOTTOM);
                    getCORRIDOR_LIST().add(corridors);
                }
            }
        }
    }

    public void genLevel() {
        for (int i = 0; i < getHEIGHT(); i++) {
            for (int j = 0; j < getWIDTH(); j++) {
                getLEVEL()[i][j] = Tile.STONE;
            }
        }

        int maxIters = getMAX_ROOMS() * 5;
        int[] tmpRoom;
        List<int[]> tmpRoomList = new ArrayList<>();
        for (int i = 0; i < maxIters; i++) {
            tmpRoom = genRoom();

            if (isROOMS_OVERLAP() || getROOM_LIST().size() == 0) {
                getROOM_LIST().add(tmpRoom);
            } else {
                tmpRoom = genRoom();
                tmpRoomList.addAll(getROOM_LIST());
                if (!roomOverlapping(tmpRoom, tmpRoomList)) {
                    getROOM_LIST().add(tmpRoom);
                }
            }

            if (getROOM_LIST().size() >= getMAX_ROOMS()) {
                break;
            }
        }

        for (int i = 0; i < getROOM_LIST().size() - 1; i++) {
            joinRooms(getROOM_LIST().get(i), getROOM_LIST().get(i + 1), JoinType.EITHER);
        }

        for (int i = 0; i < getRANDOM_CONNECTIONS(); i++) {
            int[] room1 = getROOM_LIST().get(getRANDOM().nextInt(0, getROOM_LIST().size()));
            int[] room2 = getROOM_LIST().get(getRANDOM().nextInt(0, getROOM_LIST().size()));
            joinRooms(room1, room2, JoinType.EITHER);
        }

        for (int i = 0; i < getRANDOM_SPURS(); i++) {
            int[] room1 = new int[]{
                    getRANDOM().nextInt(2, getWIDTH() - 1),
                    getRANDOM().nextInt(2, getHEIGHT() - 1),
                    1,
                    1
            };
            int[] room2 = getROOM_LIST().get(getRANDOM().nextInt(0, getROOM_LIST().size()));
            joinRooms(room1, room2, JoinType.EITHER);
        }

        for (int[] room : getROOM_LIST()) {
            for (int i = 0; i < room[2]; i++) {
                for (int j = 0; j < room[3]; j++) {
                    getLEVEL()[room[1] + j][room[0] + i] = Tile.FLOOR;
                }
            }
        }

        int x1, y1, x2, y2, x3, y3;
        for (int[][] corridor : getCORRIDOR_LIST()) {
            x1 = corridor[0][0];
            y1 = corridor[0][1];
            x2 = corridor[1][0];
            y2 = corridor[1][1];
            for (int i = 0; i < Math.abs(x1 - x2) + 1; i++) {
                for (int j = 0; j < Math.abs(y1 - y2) + 1; j++) {
                    getLEVEL()[Math.min(y1, y2) + j][Math.min(x1, x2) + i] = Tile.FLOOR;
                }
            }

            if (corridor.length == 3) {
                x3 = corridor[2][0];
                y3 = corridor[2][1];

                for (int i = 0; i < Math.abs(x2 - x3) + 1; i++) {
                    for (int j = 0; j < Math.abs(y2 - y3) + 1; j++) {
                        getLEVEL()[Math.min(y2, y3) + j][Math.min(x2, x3) + i] = Tile.FLOOR;
                    }
                }
            }
        }

        for (int row = 1; row < getHEIGHT() - 1; row++) {
            for (int col = 1; col < getWIDTH() - 1; col++) {
                if (getLEVEL()[row][col].equals(Tile.FLOOR)) {
                    if (getLEVEL()[row - 1][col - 1].equals(Tile.STONE)) {
                        getLEVEL()[row - 1][col - 1] = Tile.WALL;
                    }

                    if (getLEVEL()[row - 1][col].equals(Tile.STONE)) {
                        getLEVEL()[row - 1][col] = Tile.WALL;
                    }

                    if (getLEVEL()[row - 1][col + 1].equals(Tile.STONE)) {
                        getLEVEL()[row - 1][col + 1] = Tile.WALL;
                    }

                    if (getLEVEL()[row][col - 1].equals(Tile.STONE)) {
                        getLEVEL()[row][col - 1] = Tile.WALL;
                    }

                    if (getLEVEL()[row][col + 1].equals(Tile.STONE)) {
                        getLEVEL()[row][col + 1] = Tile.WALL;
                    }

                    if (getLEVEL()[row + 1][col - 1].equals(Tile.STONE)) {
                        getLEVEL()[row + 1][col - 1] = Tile.WALL;
                    }

                    if (getLEVEL()[row + 1][col].equals(Tile.STONE)) {
                        getLEVEL()[row + 1][col] = Tile.WALL;
                    }

                    if (getLEVEL()[row + 1][col + 1].equals(Tile.STONE)) {
                        getLEVEL()[row + 1][col + 1] = Tile.WALL;
                    }
                }
            }
        }
    }

    public void genTilesLevel() {
        for (Tile[] row : getLEVEL()) {
            List<String> tmpTiles = new ArrayList<>();
            for (Tile col : row) {
                if (col.equals(Tile.STONE)) {
                    tmpTiles.add(Tile.STONE.getTile());
                }
                if (col.equals(Tile.FLOOR)) {
                    tmpTiles.add(Tile.FLOOR.getTile());
                }
                if (col.equals(Tile.WALL)) {
                    tmpTiles.add(Tile.WALL.getTile());
                }
            }
            getTILES_LEVEL().add(String.join("", tmpTiles));
        }

        for (String row : getTILES_LEVEL()) {
            System.out.println(row);
        }
    }

}
