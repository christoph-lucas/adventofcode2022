package ch.lucas.y2022d12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.util.Comparator.comparingInt;

public class HillClimbingAlgorithm {

    private final char[][] topo;
    private final List<Coordinates> valleys = new ArrayList<>();
    private Coordinates start;
    private Coordinates end;

    public HillClimbingAlgorithm(List<String> lines) {
        this.topo = new char[lines.size()][lines.get(0).length()];
        int row = 0;
        for (String line : lines) {
            int col = 0;
            for (char c : line.toCharArray()) {
                if (c == 'S') {
                    topo[row][col] = 'a';
                    start = new Coordinates(topo, row, col, 0, CheckingDirection.UP);
                } else {
                    topo[row][col] = c;
                }
                if (c == 'E') {
                    end = new Coordinates(topo, row, col, 0, CheckingDirection.DOWN);
                }
                col++;
            }
            row++;
        }
//        prettyPrintTopo();
    }

    public static void main(String[] args) {
        System.out.println("Hill Climbing Algorithm!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d12.txt"));
            System.out.println("BFS Shortest Path from Start: " + new HillClimbingAlgorithm(input).bfs()); // 391
            System.out.println("BFS Shortest Hike to End: " + new HillClimbingAlgorithm(input).bfsReverse()); // 386
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int bfs() {
        Queue<Coordinates> q = new LinkedList<>();
        q.add(start);

        while (!q.isEmpty()) {
            Coordinates curCoord = q.poll();
            if (!curCoord.valid()) continue;
            if (curCoord.end()) {
                return curCoord.dist;
            }
            q.addAll(curCoord.validNeighbours());
            // NB: marking after visit instead of when being enqueued has the advantage that no separate
            // array is needed to track visited nodes, yet we might enqueue the same node twice
            // -> need to check node is still valid at beginning of visit
            curCoord.mark();
        }
        throw new IllegalStateException();
    }

    public int bfsReverse() {
        Queue<Coordinates> q = new LinkedList<>();
        q.add(end);

        while (!q.isEmpty()) {
            Coordinates curCoord = q.poll();
            if (!curCoord.valid()) continue; // might have been visited in the meantime
            if (curCoord.eval() == 'a') {
                valleys.add(curCoord);
                curCoord.mark();
                continue; // no need to search in the valley, can only get longer
            }
            q.addAll(curCoord.validNeighbours());
            // NB: marking after visit instead of when being enqueued has the advantage that no separate
            // array is needed to track visited nodes, yet we might enqueue the same node twice
            // -> need to check node is still valid at beginning of visit
            curCoord.mark();
        }
//        prettyPrintTopo();
        return valleys.stream().min(comparingInt(Coordinates::dist)).orElseThrow().dist;
    }

    private void prettyPrintTopo() {
        for (int r = 0; r < topo.length; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < topo[r].length; c++) {
                line.append(topo[r][c]);
            }
            System.out.println(line);
        }
        System.out.println("-----------------------------------------------------------------------");
    }

    private enum CheckingDirection {
        UP, DOWN
    }

    private record Coordinates(char[][] topo, int row, int col, int dist, CheckingDirection dir) {
        private static final char MARKER = '#';


        public void mark() {
            topo[row][col] = MARKER;
        }

        public boolean valid() {
            return row >= 0 && row < topo.length
                    && col >= 0 && col < topo[0].length
                    && topo[row][col] != MARKER;
        }

        public boolean end() {
            return eval() == 'E';
        }

        public boolean valley() {
            return eval() == 'a';
        }

        public List<Coordinates> validNeighbours() {
            List<Coordinates> res = new ArrayList<>();
            if (canGo(left())) res.add(left());
            if (canGo(right())) res.add(right());
            if (canGo(up())) res.add(up());
            if (canGo(down())) res.add(down());
            return res;
        }

        private char eval() {
            return topo[row][col];
        }

        private Coordinates left() {
            return new Coordinates(topo, row, col - 1, dist + 1, dir);
        }

        private Coordinates right() {
            return new Coordinates(topo, row, col + 1, dist + 1, dir);
        }

        private Coordinates up() {
            return new Coordinates(topo, row - 1, col, dist + 1, dir);
        }

        private Coordinates down() {
            return new Coordinates(topo, row + 1, col, dist + 1, dir);
        }

        private boolean canGo(Coordinates n) {
            return n.valid() && validStep(n);
        }

        private boolean validStep(Coordinates n) {
            if (CheckingDirection.UP.equals(dir)) {
                char next = n.end() ? 'z' : n.eval();
                return next - eval() <= 1;
            }
            char cur = end() ? 'z' : eval();
            return cur - n.eval() <= 1;
        }
    }

}
