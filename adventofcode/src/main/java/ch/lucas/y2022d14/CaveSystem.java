package ch.lucas.y2022d14;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Comparator.comparingInt;

public class CaveSystem {

    public static Coordinates SAND = new Coordinates(500, 0);

    private char[][] cave;
    private int xMin;
    private int xMax;

    private int sandcorns = 0;

    public CaveSystem(char[][] cave, int xMin, int xMax) {
        this.cave = cave;
        this.xMin = xMin;
        this.xMax = xMax;
    }

    public static CaveSystem parse(List<String> input, boolean withFloor) {
        // determine dimensions
        List<Coordinates> allCoords = input.stream().flatMap(it -> Arrays.stream(it.split(" -> "))).map(Coordinates::from).toList();
        int xMin = allCoords.stream().map(Coordinates::x).min(Integer::compareTo).orElseThrow();
        int xMax = allCoords.stream().map(Coordinates::x).max(Integer::compareTo).orElseThrow();
        int yMax = allCoords.stream().map(Coordinates::y).max(Integer::compareTo).orElseThrow()+2;
        System.out.println(("xMax=" + xMax + ", yMax=" + yMax));
        char[][] res = new char[yMax+1][xMax+500]; // allow dropping off the right

        // fill with air
        for (int y = 0; y<res.length; y++) {
            for (int x = 0; x<res[0].length; x++) {
                res[y][x] = '.';
            }
        }
        // set sand starting point
        res[SAND.y][SAND.x] = '+';

        // parse rock structures
        for (String rockPath: input) {
            List<Coordinates> corners = Arrays.stream(rockPath.split(" -> ")).map(Coordinates::from).toList();
            for (int i = 1; i<corners.size(); i++) {
                Coordinates prev = corners.get(i-1);
                Coordinates cur = corners.get(i);
                if (prev.x == cur.x) {
                    for (int y = min(prev.y, cur.y); y<= Math.max(prev.y, cur.y); y++) {
                        res[y][prev.x] = '#';
                    }
                } else {
                    for (int x = min(prev.x, cur.x); x<= Math.max(prev.x, cur.x); x++) {
                        res[prev.y][x] = '#';
                    }
                }
            }
        }

        if (withFloor) {
            for (int x = 0; x<res[0].length; x++) {
                res[res.length-1][x] = '#';
            }
        }

        return new CaveSystem(res, xMin, xMax);
    }

    public int sandcorns() {
        return sandcorns;
    }

    public boolean addSandCorn() {
        Coordinates cur = SAND;
        while (true) {
            if (valid(cur.down())) {
                cur = cur.down();
            } else if (valid(cur.downLeft())) {
                cur = cur.downLeft();
            } else if (valid(cur.downRight())) {
                cur = cur.downRight();
            } else {
                break;
            }
        }
        if (cur.y == cave.length-1) { // falls through
            return false;
        }
        sandcorns++;
        xMin = min(xMin, cur.x);
        xMax = max(xMax, cur.x);
        cave[cur.y][cur.x] = 'o';
        return cur != SAND;
    }

    private boolean valid(Coordinates c) {
        return 0<= c.x && c.x < cave[0].length
                && 0<= c.y && c.y < cave.length
                && cave[c.y][c.x] == '.';
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int r = 0; r < cave.length; r++) {
            for (int c = xMin-1; c <= xMax+1; c++) {
                res.append(cave[r][c]);
            }
            res.append("\n");
        }
        return res.toString();
    }

    private record Coordinates(int x, int y) {
        public static Coordinates from(String s) {
            String[] raw = s.split(",");
            return new Coordinates(Integer.valueOf(raw[0]), Integer.valueOf(raw[1]));
        }

        public Coordinates down() {
            return new Coordinates(x, y+1);
        }
        public Coordinates downLeft() {
            return new Coordinates(x-1, y+1);
        }
        public Coordinates downRight() {
            return new Coordinates(x+1, y+1);
        }
    }

}
