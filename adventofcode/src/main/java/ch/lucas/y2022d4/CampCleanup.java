package ch.lucas.y2022d4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CampCleanup {

    private final List<Pair<Assignment>> pairs;

    public CampCleanup(List<String> values) {
        pairs = values.stream().filter(it -> it.length() > 0).map(this::parse).toList();
        System.out.println(pairs);
    }

    public static void main(String[] args) {
        System.out.println("Camp Cleanup!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d4.txt"));
            CampCleanup cc = new CampCleanup(input);
            System.out.println("Number of total overlaps: " + cc.totalOverlaps()); //448
            System.out.println("Number of partial overlaps: " + cc.someOverlaps()); //794
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long totalOverlaps() {
        return pairs.stream()
                .filter(it -> it.first.contains(it.second) || it.second.contains(it.first))
                .count();
    }

    public long someOverlaps() {
        return pairs.stream()
                .filter(it -> it.first.overlap(it.second))
                .count();
    }

    private Pair<Assignment> parse(String s) {
        return new Pair(new Assignment(s.split(",")[0]), new Assignment(s.split(",")[1]));
    }

    private record Pair<T>(T first, T second) {
        @Override
        public String toString() {
            return "(" + first.toString() + ", " + second.toString() + ")";
        }
    }

    private record Assignment(int start, int end) {

        public Assignment(String s) {
            this(Integer.valueOf(s.split("-")[0]), Integer.valueOf(s.split("-")[1]));
        }

        public boolean contains(Assignment other) {
            return this.start <= other.start && this.end >= other.end;
        }

        public boolean overlap(Assignment other) {
            if (this.start <= other.start) {
                return this.end >= other.start;
            }
            // else: other.start < this.start
            return other.end >= this.start;
        }

        @Override
        public String toString() {
            return start + "-" + end;
        }
    }
}
