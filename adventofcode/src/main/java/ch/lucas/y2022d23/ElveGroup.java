package ch.lucas.y2022d23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.groupingBy;

public class ElveGroup {

    Map<Position, Elve> pos2Elve = new HashMap<>();
    List<Elve> elves = new ArrayList<>();

    public ElveGroup(List<String> input) {
        int rowCount = 0;
        for (String row : input) {
            int colCount = 0;
            for (char c : row.toCharArray()) {
                if (c == '#') {
                    Position pos = new Position(rowCount, colCount);
                    Elve elve = new Elve(pos, pos2Elve);
                    pos2Elve.put(pos, elve);
                    elves.add(elve);
                }
                colCount++;
            }
            rowCount++;
        }
        System.out.println("Number of Elves: " + elves.size());
        plot();
    }

    public void updateElvePositions(int rounds) {
        for (int i = 0; i < rounds; i++) {
            for (Elve e : elves) {
                e.proposePosition(i);
            }
            Map<Position, List<Elve>> newPos2Elves = elves.stream().collect(groupingBy(Elve::proposedPosition));
            for (Entry<Position, List<Elve>> singleNewPos2Elves : newPos2Elves.entrySet()) {
                if (singleNewPos2Elves.getValue().size() > 1) {
                    singleNewPos2Elves.getValue().forEach(Elve::cancelProposition);
                } else {
                    singleNewPos2Elves.getValue().forEach(Elve::applyProposition);
                }
            }
            System.out.println("After round " + (i + 1));
            plot();
        }
    }

    public int updateElvePositions() {
        int roundCount = 0;
        while (true) {
            boolean anyUpdate = false;
            for (Elve e : elves) {
                anyUpdate |= e.proposePosition(roundCount);
            }
            if (!anyUpdate) return roundCount + 1;
            Map<Position, List<Elve>> newPos2Elves = elves.stream().collect(groupingBy(Elve::proposedPosition));
            for (Entry<Position, List<Elve>> singleNewPos2Elves : newPos2Elves.entrySet()) {
                if (singleNewPos2Elves.getValue().size() > 1) {
                    singleNewPos2Elves.getValue().forEach(Elve::cancelProposition);
                } else {
                    singleNewPos2Elves.getValue().forEach(Elve::applyProposition);
                }
            }
            roundCount++;
            System.out.println("After round " + roundCount);
            plot();
        }
    }

    public int getFreeSpaceInBetween() {
        int upperBound = elves.get(0).position().row();
        int lowerBound = elves.get(0).position().row();
        int leftBound = elves.get(0).position().col();
        int rightBound = elves.get(0).position().col();

        for (Elve e : elves) {
            upperBound = Math.min(upperBound, e.position().row());
            lowerBound = Math.max(lowerBound, e.position().row());
            leftBound = Math.min(leftBound, e.position().col());
            rightBound = Math.max(rightBound, e.position().col());
        }

        int squareSize = (lowerBound - upperBound + 1) * (rightBound - leftBound + 1);
        return squareSize - elves.size();
    }

    private void plot() {
        int upperBound = elves.get(0).position().row();
        int lowerBound = elves.get(0).position().row();
        int leftBound = elves.get(0).position().col();
        int rightBound = elves.get(0).position().col();

        for (Elve e : elves) {
            upperBound = Math.min(upperBound, e.position().row());
            lowerBound = Math.max(lowerBound, e.position().row());
            leftBound = Math.min(leftBound, e.position().col());
            rightBound = Math.max(rightBound, e.position().col());
        }

        char[][] plot = new char[lowerBound - upperBound + 1][rightBound - leftBound + 1];
        for (int r = 0; r < plot.length; r++) {
            for (int c = 0; c < plot[0].length; c++) {
                plot[r][c] = '.';
            }
        }
        for (Elve e : elves) {
            plot[e.position().row() - upperBound][e.position().col() - leftBound] = '#';
        }

        for (int r = 0; r < plot.length; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < plot[0].length; c++) {
                line.append(plot[r][c]);
            }
            System.out.println(line);
        }
        System.out.println();
        System.out.println("-".repeat(plot[0].length));
        System.out.println();
    }

}
