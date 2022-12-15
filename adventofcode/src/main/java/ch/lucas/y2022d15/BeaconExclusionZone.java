package ch.lucas.y2022d15;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class BeaconExclusionZone {

    private final List<Sensor> sensors;
    private final List<Coordinates> beacons;

    public BeaconExclusionZone(List<String> input) {
        sensors = input.stream().map(Sensor::from).toList();
        beacons = sensors.stream().map(Sensor::closestBeacon).toList();
    }

    public static void main(String[] args) {
        System.out.println("Beacon Exclusion Zone!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d15.txt"));
            BeaconExclusionZone bez = new BeaconExclusionZone(input);
            System.out.println("Covered positions in row: " + bez.positionsCoveredOnRow(2000000)); // 4424278
            int max = 4000000;
            Coordinates uncovered = bez.uncoveredWithin(0, max);
            System.out.println("Unconvered position: " + uncovered); // Coordinates[x=2595657, y=2753392]
            BigInteger tuningFrequency = BigInteger.valueOf(uncovered.x()).multiply(BigInteger.valueOf(max)).add(BigInteger.valueOf(uncovered.y()));
            System.out.println("Tuning frequency: " + tuningFrequency); // tuningFrequency
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int positionsCoveredOnRow(int y) {
        Set<Coordinates> covered = sensors.stream()
                .filter(it -> Math.abs(it.position().y() - y) <= it.dist())
                .flatMap(it -> intersection(it, y))
                .collect(toSet());
        covered.removeAll(beacons);
        return covered.size();
    }

    private Stream<Coordinates> intersection(Sensor s, int y) {
        int remainder = s.dist() - Math.abs(s.position().y() - y);
        List<Coordinates> result = new ArrayList<>();
        for (int x = s.position().x() - remainder; x <= s.position().x() + remainder; x++) {
            result.add(new Coordinates(x, y));
        }
        return result.stream();
    }

    public Coordinates uncoveredWithin(int min, int max) {
        for (int y = 0; y <= max; y++) {
            final int yp = y;
            List<int[]> coveredRanges = sensors.stream()
                    .filter(it -> Math.abs(it.position().y() - yp) <= it.dist())
                    .map(s -> {
                        int remainder = s.dist() - Math.abs(s.position().y() - yp);
                        int[] range = new int[]{Math.max(min, s.position().x() - remainder), Math.min(max, s.position().x() + remainder)};
                        return range;
                    })
                    .sorted(Comparator.comparingInt(it -> it[0])).toList();
            if (coveredRanges.isEmpty()) return new Coordinates(0, y); // rather unlikely
            int[] curRange = coveredRanges.get(0);
            if (curRange[0] > 0) return new Coordinates(0, y); // rather unlikely
            for (int i = 1; i < coveredRanges.size(); i++) {
                int[] nextRange = coveredRanges.get(i);
                if (curRange[1] < nextRange[0] - 1) return new Coordinates(nextRange[0] - 1, y);
                curRange[1] = Math.max(curRange[1], nextRange[1]);
            }
        }
        throw new IllegalArgumentException();
    }

}
