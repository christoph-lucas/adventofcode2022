package ch.lucas.y2022d17;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PyroclasticFlow {

    private final String jets;
    private final List<Rock> rockShapes = List.of(Rock.MINUS, Rock.PLUS, Rock.ELL, Rock.EYE, Rock.DOT);
    private final Map<RockJetCombi, Integer> rockJetCombiCounts = new HashMap<>();
    private final Map<RockJetCombi, Integer> rockJetCombiFirstOccurrence = new HashMap<>();
    private Chamber chamber;

    public PyroclasticFlow(String jets) {
        this.jets = jets;
        System.out.println("Jet Sequence Length: " + jets.length());
    }

    public static void main(String[] args) {
        System.out.println("Pyroclastic Flow!");
        try {
            int n = 50000000;
            //n = 100_000_000;  // takes 10s
            // Goal: 1_000_000_000_000
            Instant start = Instant.now();
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d17_ex.txt"));
            PyroclasticFlow pyroclasticFlow = new PyroclasticFlow(input.get(0));
            System.out.println("Rock Tower height (ex): " + pyroclasticFlow.getHeightAfterRocks(n));
            pyroclasticFlow.analyze(new BigInteger("1000000000000"));
            // n = 2022 ->  3068
            System.out.println("Elapsed time: " + Duration.between(start, Instant.now()).toSeconds() + "s");

            input = Files.readAllLines(Paths.get("src/main/resources/input_y22d17.txt"));
            start = Instant.now();
            pyroclasticFlow = new PyroclasticFlow(input.get(0));
            System.out.println("Rock Tower height: " + pyroclasticFlow.getHeightAfterRocks(n));
            pyroclasticFlow.analyze(new BigInteger("1000000000000"));
            // n = 2022 ->  3191
            // part 2: 1593471810062 -> WRONG -> need more data to find correct periodicity and offset
            // part 2: 1572093023267
            System.out.println("Elapsed time: " + Duration.between(start, Instant.now()).toSeconds() + "s");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getHeightAfterRocks(int nRocks) {
        this.chamber = new Chamber((nRocks * 4) + 50);
        return getHeightAfterRocks(nRocks, this.chamber, true);
    }


    public int getHeightAfterRocks(int nRocks, Chamber chamber, boolean trace) {
        int curRock = 0;
        int curJet = 0;

        for (int c = 0; c < nRocks; c++) {
            if (trace) {
                byte[] top = chamber.top();
                RockJetCombi combi = new RockJetCombi(curRock, curJet, top[0], top[1]);
                rockJetCombiCounts.put(combi, rockJetCombiCounts.getOrDefault(combi, 0) + 1);
                if (!rockJetCombiFirstOccurrence.containsKey(combi)) rockJetCombiFirstOccurrence.put(combi, c);
            }

            Rock r = rockShapes.get(curRock);
            curRock = (curRock + 1) % rockShapes.size();

            int left = 2;
            int bottom = chamber.currentHeight + 3;
            while (true) {
                // apply jet
                int d = getJetDelta(curJet);
                curJet = (curJet + 1) % jets.length();
                if (chamber.fitsAt(left + d, bottom, r)) {
                    left += d;
                }

                // fall one unit
                if (chamber.fitsAt(left, bottom - 1, r)) {
                    bottom--;
                } else {
                    break;
                }
            }
            chamber.plotAt(left, bottom, r);
        }
        return chamber.currentHeight;
    }

    public void analyze(BigInteger nRocks) {
        // There is most likely a better, more analytical way to determine the periodicity. The one here is a bit heuristical.
        // The problem seems to be to find the periodicity in the number of rocks. Finding it in the heights would be easier
        // since we have that sequence of numbers anyways.
        // In rocks it's harder since we have to determine a kind of "State" that also depends on the chamber. Looking for
        // new floors did not work. Using the top most 2 rows of the chamber was enough, but could have been wrong...

//        chamber.prettyPrint();
        List<Map.Entry<RockJetCombi, Integer>> sortedCombis = rockJetCombiCounts.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())).toList();

        int maxRepetitions = sortedCombis.get(0).getValue();
        sortedCombis.stream().filter(it -> it.getValue() == maxRepetitions).limit(10).forEach(it -> {
            System.out.println(it + ", first occurred at rock #" + rockJetCombiFirstOccurrence.get(it.getKey()));
        });

        int offsetInRocks = sortedCombis.stream()
                .filter(it -> it.getValue() == maxRepetitions)
                .map(it -> rockJetCombiFirstOccurrence.get(it.getKey()))
                .sorted().findFirst().orElseThrow();
        int periodicityInRocks = (int) Math.ceil(1.0 * chamber.nRocks / maxRepetitions);
        System.out.println("offsetInRocks=" + offsetInRocks + ", periodicityInRocks=" + periodicityInRocks);

        BigInteger nRocksMinusOffset = nRocks.subtract(BigInteger.valueOf(offsetInRocks));
        BigInteger[] repetetionsAndRemainder = nRocksMinusOffset.divideAndRemainder(BigInteger.valueOf(periodicityInRocks));
        BigInteger fittedRepetions = repetetionsAndRemainder[0];
        BigInteger remainder = repetetionsAndRemainder[1];
        System.out.println("fittedRepetions=" + fittedRepetions + ", remainder=" + remainder);

        int trial = offsetInRocks + periodicityInRocks;
        int heightOffsetPlus1Rep = getHeightAfterRocks(trial, new Chamber((trial * 4) + 20), false);
        trial += periodicityInRocks;
        int heightOffsetPlus2Rep = getHeightAfterRocks(trial, new Chamber((trial * 4) + 20), false);
        int diff = heightOffsetPlus2Rep - heightOffsetPlus1Rep;

        int singleRepPlusStartEnd = offsetInRocks + periodicityInRocks + remainder.intValue();
        int heightOffsetPlus1RepPlusRemainder = getHeightAfterRocks(singleRepPlusStartEnd, new Chamber((singleRepPlusStartEnd * 4) + 20), false);

        BigInteger res = BigInteger.valueOf(heightOffsetPlus1RepPlusRemainder).add(fittedRepetions.subtract(BigInteger.ONE).multiply(BigInteger.valueOf(diff)));
        System.out.println("res=" + res);
    }

    private int getJetDelta(int curJet) {
        return switch (jets.charAt(curJet)) {
            case '>' -> 1;
            case '<' -> -1;
            default -> throw new IllegalStateException("Unexpected value: " + jets.charAt(curJet));
        };
    }

    private record RockJetCombi(int r, int j, byte l1, byte l2) {
    }

}
