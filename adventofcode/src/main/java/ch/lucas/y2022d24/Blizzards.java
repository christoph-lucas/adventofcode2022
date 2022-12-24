package ch.lucas.y2022d24;

import java.util.ArrayList;
import java.util.List;

public class Blizzards {

    private final int width;
    private final int height;

    private final List<Blizzard> blizzards = new ArrayList<>();

    public Blizzards(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addBlizzard(Position p, char c) {
        blizzards.add(new Blizzard(p, Direction.from(c), width, height));
    }

    public boolean anyBlizzardAt(State s) {
        return blizzards.stream().anyMatch(it -> it.isAt(s));
    }

    public void plot(State s, Position end, Position start) {
        char[][] basin = new char[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                basin[r][c] = '.';
            }
        }

        for (int r = 0; r < height; r++) {
            basin[r][0] = '#';
            basin[r][width - 1] = '#';
        }
        for (int c = 0; c < width; c++) {
            basin[0][c] = '#';
            basin[height - 1][c] = '#';
        }
        basin[start.row()][start.col()] = '.';
        basin[end.row()][end.col()] = '.';

        for (Blizzard b : blizzards) {
            int r = b.getPositionAt(s.min()).row();
            int c = b.getPositionAt(s.min()).col();
            if (basin[r][c] == '.') {
                basin[r][c] = b.dir.toString().charAt(0);
            } else if (basin[r][c] == '#') {
                throw new IllegalStateException();
            } else {
                basin[r][c] = 'x';
            }
        }
        
        if (basin[s.p().row()][s.p().col()] != '.') {
            throw new IllegalStateException();
        }

        basin[s.p().row()][s.p().col()] = 'E';


        System.out.println(s);
        StringBuilder res = new StringBuilder();
        for (int x = 0; x < basin.length; x++) {
            for (int y = 0; y < basin[0].length; y++) {
                res.append(basin[x][y]);
            }
            res.append("\n");
        }
        System.out.println(res);

    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public static Direction from(char c) {
            return switch (c) {
                case '>' -> RIGHT;
                case '<' -> LEFT;
                case 'v' -> DOWN;
                case '^' -> UP;
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case UP -> "^";
                case DOWN -> "v";
                case LEFT -> "<";
                case RIGHT -> ">";
            };
        }

    }

    private static class Blizzard {
        private final Position initialPos;
        private final Direction dir;
        private final int width;
        private final int height;

        public Blizzard(Position initialPos, Direction dir, int width, int height) {
            this.initialPos = initialPos;
            this.dir = dir;
            this.width = width;
            this.height = height;
        }

        public boolean isAt(State s) {
            return getPositionAt(s.min()).equals(s.p());
        }

        private Position getPositionAt(int min) {
            // take coordinate (DOWN / UP -> col, RIGHT / LEFT -> row)
            // change range from (1, height-1) to (0, height-2)
            // apply diff in minutes (+/- min), NB: negativ does not get wrapped automatically -> add height-2
            // modulo (height-2)
            // change range back to (1, height-1)
            return switch (dir) {
                case UP ->
                        new Position(((initialPos.row() - 1 - (min % (height - 2)) + (height - 2)) % (height - 2)) + 1, initialPos.col());
                case DOWN -> new Position(((initialPos.row() - 1 + min) % (height - 2)) + 1, initialPos.col());
                case LEFT ->
                        new Position(initialPos.row(), ((initialPos.col() - 1 - (min % (width - 2)) + (width - 2)) % (width - 2)) + 1);
                case RIGHT -> new Position(initialPos.row(), ((initialPos.col() - 1 + min) % (width - 2)) + 1);
            };
        }

    }
}
