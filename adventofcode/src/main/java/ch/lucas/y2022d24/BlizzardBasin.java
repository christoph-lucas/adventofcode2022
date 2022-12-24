package ch.lucas.y2022d24;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BlizzardBasin {

    private final Position start = new Position(0, 1);
    private final Position end;
    private final int height;
    private final int width;
    private final Blizzards blizzards;

    public BlizzardBasin(List<String> input) {
        height = input.size();
        width = input.get(0).length();
        end = new Position(height - 1, width - 2);

        blizzards = new Blizzards(width, height);
        for (int r = 1; r <= height - 2; r++) {
            String row = input.get(r);
            for (int c = 1; c <= width - 2; c++) {
                if (row.charAt(c) != '.') {
                    blizzards.addBlizzard(new Position(r, c), row.charAt(c));
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Blizzard Basin\n");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d24.txt"));
            System.out.println("Duration One-Way: " + new BlizzardBasin(input).part1());
            new BlizzardBasin(input).part2();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int part1() {
        return bfs(new State(start, 0), end, false);
    }

    public void part2() {
        int timeToEnd = bfs(new State(start, 0), end, false);
        System.out.println("Time to reach goal the first time: " + timeToEnd);

        int timeBackToStart = bfs(new State(end, timeToEnd), start, false);
        System.out.println("Time to go back to start: " + timeBackToStart);

        int timeGoAgain = bfs(new State(start, timeBackToStart), end, false);
        System.out.println("Time to go again: " + timeGoAgain);

    }

    private int bfs(State initState, Position target, boolean debug) {
        Set<State> visited = new HashSet<>();
        Queue<State> q = new LinkedList<>();
        q.add(initState);
        while (!q.isEmpty()) {
            State cur = q.poll();

            if (cur.p().equals(target)) {
                blizzards.plot(cur, end, start);
                return cur.min();
            }
            if (isWall(cur.p())) continue;
            if (visited.contains(cur)) continue;
            visited.add(cur);
            if (blizzards.anyBlizzardAt(cur)) continue;

            if (debug) blizzards.plot(cur, end, start);

            q.add(new State(cur.p().up(), cur.min() + 1));
            q.add(new State(cur.p().down(), cur.min() + 1));
            q.add(new State(cur.p().left(), cur.min() + 1));
            q.add(new State(cur.p().right(), cur.min() + 1));
            q.add(new State(cur.p(), cur.min() + 1));
        }
        throw new IllegalStateException("Exit not found!");
    }

    public boolean isWall(Position p) {
        return p != start && p != end
                && (p.row() <= 0 || p.col() <= 0 || p.row() >= height - 1 || p.col() >= width - 1);
    }

}
