package ch.lucas.y2022d9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RopeBridge {

    private final List<String> moves;

    public RopeBridge(List<String> moves) {
        this.moves = moves;
    }

    public static void main(String[] args) {
        System.out.println("Treetop Tree House!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d9.txt"));
            RopeBridge rp = new RopeBridge(input);
            System.out.println("Tail positions (n=2): " + rp.simulate(2)); // 6209
            System.out.println("Tail positions (n=10): " + rp.simulate(10)); // 2460
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int simulate(int n) {
        Position[] knots = new Position[n];
        for (int i = 0; i < n; i++) knots[i] = new Position(0, 0);

        Set<Position> tailPositions = new HashSet<>();
        tailPositions.add(knots[n - 1]);

        for (String moveAndCount : moves) {
            Move move = Move.from(moveAndCount.charAt(0));
            int count = Integer.valueOf(moveAndCount.substring(2));
            for (int i = 0; i < count; i++) {
                knots[0] = move.apply(knots[0]);
                for (int j = 1; j < n; j++) {
                    knots[j] = knots[j].moveTowards(knots[j - 1]);
                }
                tailPositions.add(knots[n - 1]);
            }
        }
        return tailPositions.size();
    }

    private enum Move {
        R() {
            @Override
            public Position apply(Position p) {
                return new Position(p.x + 1, p.y);
            }
        },
        L() {
            @Override
            public Position apply(Position p) {
                return new Position(p.x - 1, p.y);
            }
        },
        U() {
            @Override
            public Position apply(Position p) {
                return new Position(p.x, p.y + 1);
            }
        },
        D() {
            @Override
            public Position apply(Position p) {
                return new Position(p.x, p.y - 1);
            }
        };

        public static Move from(Character c) {
            return switch (c) {
                case 'R' -> R;
                case 'L' -> L;
                case 'U' -> U;
                case 'D' -> D;
                default -> throw new IllegalStateException("Unexpected value: " + c);
            };
        }

        public abstract Position apply(Position p);
    }

    private record Position(int x, int y) {
        public Position moveTowards(Position to) {
            int dx = to.x - this.x;
            int dy = to.y - this.y;
            if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) return this;
            return new Position(this.x + (int) Math.signum(dx), this.y + (int) Math.signum(dy));
        }
    }
}
