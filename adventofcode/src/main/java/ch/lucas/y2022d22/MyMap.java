package ch.lucas.y2022d22;

import ch.lucas.y2022d22.MonkeyMap.Direction;
import ch.lucas.y2022d22.MonkeyMap.Position;

import java.util.List;

/**
 * Mapping regions as follows:
 *   21
 *   3
 *  54
 *  6
 *
 * 1, DOWN -> 3, RIGHT (entering facing LEFT)
 * 1, UP ->
 * 1, RIGHT ->
 * 2, DOWN ->
 * 2, UP ->
 * 2, RIGHT ->
 * 3, DOWN ->
 * 3, UP ->
 * 3, RIGHT -> 1, DOWN (entering facing UP)
 * 4, DOWN ->
 * 4, UP ->
 * 4, RIGHT ->
 * 5, DOWN ->
 * 5, UP ->
 * 5, RIGHT ->
 * 6, DOWN ->
 * 6, UP ->
 * 6, RIGHT ->
 *
 *
 */
public class MyMap {

    private char[][] mapData;

    public MyMap(char[][] mapData) {
        this.mapData = mapData;
    }

    public Position getInitialPosition() {
        int col = 0;
        while(mapData[1][col] != '.') col++;
        return new Position(col, 1);
    }

    public Position getNextPosition(Position p, Direction dir) {
        int y = p.y();
        int x = p.x();
        switch (dir) {
            case UP:
                if (mapData[y-1][x] != ' ') {
                    return new Position(x, y-1);
                }
                while (mapData[y+1][x] != ' ') y++;
                return new Position(x, y);
            case DOWN:
                if (mapData[y+1][x] != ' ') {
                    return new Position(x, y+1);
                }
                while (mapData[y-1][x] != ' ') y--;
                return new Position(x, y);
            case RIGHT:
                if (mapData[y][x+1] != ' ') {
                    return new Position(x+1, y);
                }
                while (mapData[y][x-1] != ' ') x--;
                return new Position(x, y);
            case LEFT:
                if (mapData[y][x-1] != ' ') {
                    return new Position(x-1, y);
                }
                while (mapData[y][x+1] != ' ') x++;
                return new Position(x, y);
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isWall(Position p) {
        return mapData[p.y()][p.x()] == '#';
    }

    public static MyMap from(List<String> input) {
        int height = input.size();
        int width = input.stream().map(String::length).max(Integer::compareTo).orElseThrow();
        char[][] mapData = new char[height+2][width+2];

        for (int col = 0; col<mapData[0].length; col++) {
            mapData[0][col] = ' ';
            mapData[mapData.length-1][col] = ' ';
        }

        int row = 1;
        for (String rowData: input) {
            mapData[row][0] = ' ';
            int col = 1;
            for (char c: rowData.toCharArray()) mapData[row][col++] = c;
            for (; col<mapData[row].length; col++) mapData[row][col] = ' ';
            row++;
        }

        return new MyMap(mapData);
    }


    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int x = 0; x < mapData.length; x++) {
            for (int y = 0; y < mapData[0].length; y++) {
                res.append(mapData[x][y]);
            }
            res.append("\n");
        }
        return res.toString();
    }

}
