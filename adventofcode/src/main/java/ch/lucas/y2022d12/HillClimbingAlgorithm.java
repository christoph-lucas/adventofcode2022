package ch.lucas.y2022d12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static ch.lucas.y2022d12.Coordinates.CheckingDirection.DOWN;
import static ch.lucas.y2022d12.Coordinates.CheckingDirection.UP;
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
                    start = new Coordinates(topo, row, col, 0, UP);
                } else if (c == 'E') {
                    topo[row][col] = Coordinates.END;
                    end = new Coordinates(topo, row, col, 0, DOWN);
                } else {
                    topo[row][col] = c;
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
            if (curCoord.end()) {
                return curCoord.dist();
            }
            q.addAll(curCoord.getAndMarkValidNeighbours());
        }
        throw new IllegalStateException();
    }

    public int bfsReverse() {
        Queue<Coordinates> q = new LinkedList<>();
        q.add(end);

        while (!q.isEmpty()) {
            Coordinates curCoord = q.poll();
            if (curCoord.valley()) {
                valleys.add(curCoord); // no need to search in the valley, can only get longer
            } else {
                q.addAll(curCoord.getAndMarkValidNeighbours());
            }
        }
        return valleys.stream().min(comparingInt(Coordinates::dist)).orElseThrow().dist();
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

}
