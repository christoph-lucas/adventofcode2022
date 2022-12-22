package ch.lucas.y2022d22;

import ch.lucas.y2022d22.CubeCoordinates.Axis;
import ch.lucas.y2022d22.MonkeyMap.Direction;
import ch.lucas.y2022d22.MonkeyMap.Position;

import java.util.HashSet;
import java.util.Set;

import static ch.lucas.y2022d22.MonkeyMap.Direction.*;

/**
 * The cube does the actual folding of the map into a cube so that afterwards the agent can move actually in 3D.
 * So when moving right and crossing an edge, the agent can still move right, just on another side of the cube.
 * <p>
 * In order to be able to move back to the "original" coordinates, I do not only store the Char but also the Map
 * coordinates in the cube (each cubicle is called a Tile).
 * <p>
 * Only the outer sides of the cube are used. Also, the edges where two of the axis are 0 or n+1 are left free.
 * Otherwise, there would be a clash.
 * <p>
 * I found this rather smooth. Unfortunately, I ignored that the direction is part of the key and that it needs to
 * be changed when crossing an edge. So, I get the right coordinate, but to determine the original direction, I have
 * to move the agent a bit and see how he would move on the original map. A little hack, but well...
 */
public class MonkeyCube {

    private final Set<Position> upperLeftCorners = new HashSet<>();
    private final Tile[][][] cube;
    private final int n;
    private final char[][] map;

    public MonkeyCube(MyMap map, int n) {
        this.map = map.mapData;
        cube = new Tile[n + 2][n + 2][n + 2];
        this.n = n;

        Position initialPosition = map.getInitialPosition();
        CubeCoordinates initialCubeCoords = getInitialCoordinates();

        parseSide(initialPosition, initialCubeCoords);
    }

    public CubeCoordinates getInitialCoordinates() {
        return new CubeCoordinates(n, 1, 1, 0, Axis.X, Axis.Y,
                true, true);
    }

    public boolean isWall(CubeCoordinates c) {
        return cube[c.z()][c.y()][c.x()].c == '#';
    }

    public Position getMapPosition(CubeCoordinates c) {
        Tile t = cube[c.z()][c.y()][c.x()];
        return new Position(t.col, t.row);
    }

    /**
     * Honestly, I forgot to track the "original" direction. Yet, it can be determined from the mapping back to the
     * map positions. It's a bit of a hack, but well... I like the rest of the code :).
     * <p>
     * I need to look for both next and prev position, in case one of the two moves would go over an edge.
     */
    public void printPassword(CubeAgent a) {
        Position curMapPos = getMapPosition(a.coordinates);
        int password = 1000 * curMapPos.y() + 4 * curMapPos.x();

        Position nextMapPos = getMapPosition(a.wouldMoveTo());
        a.turnLeft();
        a.turnLeft();
        Position prevMapPos = getMapPosition(a.wouldMoveTo());
        a.turnLeft();
        a.turnLeft();
        if (curMapPos.moveRight().equals(nextMapPos) || curMapPos.moveLeft().equals(prevMapPos)) {
            System.out.println("The password is: " + (password + RIGHT.passwordPart()) + ". Correct direction is RIGHT.");
        } else if (curMapPos.moveLeft().equals(nextMapPos) || curMapPos.moveRight().equals(prevMapPos)) {
            System.out.println("The password is: " + (password + LEFT.passwordPart()) + ". Correct direction is LEFT.");
        } else if (curMapPos.moveDown().equals(nextMapPos) || curMapPos.moveUp().equals(prevMapPos)) {
            System.out.println("The password is: " + (password + DOWN.passwordPart()) + ". Correct direction is DOWN.");
        } else if (curMapPos.moveUp().equals(nextMapPos) || curMapPos.moveDown().equals(prevMapPos)) {
            System.out.println("The password is: " + (password + UP.passwordPart()) + ". Correct direction is UP.");
        } else {
            System.out.println("Cannot determine the original direction. Going for trial and error...");
            for (Direction d : Direction.values()) {
                System.out.println(d + ": " + (password + d.passwordPart()));
            }
        }
    }

    private void parseSide(Position upperLeft, CubeCoordinates cubeCorner) {
        if (upperLeftCorners.contains(upperLeft)) {
            System.out.println("Side already done, returning.");
            return;
        }
        upperLeftCorners.add(upperLeft);
        System.out.println("Map coords: " + upperLeft);

        CubeCoordinates cubeRowStart = cubeCorner;
        Position mapRowStart = upperLeft;
        for (int i = 0; i < n; i++) {
            CubeCoordinates cubeRowRunner = cubeRowStart;
            Position mapRowRunner = mapRowStart;
            for (int j = 0; j < n; j++) {
                cube[cubeRowRunner.z()][cubeRowRunner.y()][cubeRowRunner.x()] =
                        new Tile(mapRowRunner.y(), mapRowRunner.x(), map[mapRowRunner.y()][mapRowRunner.x()]);
                cubeRowRunner = cubeRowRunner.moveRight();
                mapRowRunner = mapRowRunner.moveRight();
            }
            cubeRowStart = cubeRowStart.moveDown();
            mapRowStart = mapRowStart.moveDown();
        }

        plotSide(cubeCorner);

        if (upperLeft.x() - n > 0 && map[upperLeft.y()][upperLeft.x() - n] != ' ') {
            System.out.println("Moving to Left Neighbour!");
            CubeCoordinates cubeCoordLeftNeighbour = cubeCorner;
            for (int i = 0; i < n; i++) cubeCoordLeftNeighbour = cubeCoordLeftNeighbour.moveLeft();
            parseSide(new Position(upperLeft.x() - n, upperLeft.y()), cubeCoordLeftNeighbour);
        }
        if (upperLeft.x() + n < map[upperLeft.y()].length && map[upperLeft.y()][upperLeft.x() + n] != ' ') {
            System.out.println("Moving to Right Neighbour!");
            CubeCoordinates cubeCoordRightNeighbour = cubeCorner;
            for (int i = 0; i < n; i++) cubeCoordRightNeighbour = cubeCoordRightNeighbour.moveRight();
            parseSide(new Position(upperLeft.x() + n, upperLeft.y()), cubeCoordRightNeighbour);
        }
        if (upperLeft.y() + n < map.length && map[upperLeft.y() + n][upperLeft.x()] != ' ') {
            System.out.println("Moving to Down Neighbour!");
            CubeCoordinates cubeCoordDownNeighbour = cubeCorner;
            for (int i = 0; i < n; i++) cubeCoordDownNeighbour = cubeCoordDownNeighbour.moveDown();
            parseSide(new Position(upperLeft.x(), upperLeft.y() + n), cubeCoordDownNeighbour);
        }
    }

    private void plotSide(CubeCoordinates c) {
        CubeCoordinates cubeRowStart = c;
        for (int i = 0; i < n; i++) {
            CubeCoordinates cubeRowRunner = cubeRowStart;
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < n; j++) {
                row.append(cube[cubeRowRunner.z()][cubeRowRunner.y()][cubeRowRunner.x()].c);
                cubeRowRunner = cubeRowRunner.moveRight();
            }
            cubeRowStart = cubeRowStart.moveDown();
            System.out.println(row);
        }
        System.out.println("------------------");
    }

    private record Tile(int row, int col, char c) {
    }

}
