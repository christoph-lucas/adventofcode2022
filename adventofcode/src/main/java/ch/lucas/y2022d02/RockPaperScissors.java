package ch.lucas.y2022d02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class RockPaperScissors {

    private static final Map<Character, Integer> POINTS_PER_SYMBOL = Map.of(
            'A', 1, // Rock
            'B', 2, // Paper
            'C', 3  //Scissors
    );

    private static final Map<Character, Map<Character, Integer>> POINTS_FROM_GAME = Map.of(
            'A', Map.of('A', 3, 'B', 0, 'C', 6),
            'B', Map.of('A', 6, 'B', 3, 'C', 0),
            'C', Map.of('A', 0, 'B', 6, 'C', 3)
    );

    private static final Map<Character, Character> RESOLVE_SYMBOL = Map.of(
            'X', 'A',
            'Y', 'B',
            'Z', 'C'
    );

    private static final Map<Character, Map<Character, Character>> RESOLVE_SYMBOL_2 = Map.of(
            'X', Map.of('A', 'C', 'B', 'A', 'C', 'B'),
            'Y', Map.of('A', 'A', 'B', 'B', 'C', 'C'),
            'Z', Map.of('A', 'B', 'B', 'C', 'C', 'A')
    );


    private final List<String> rounds;

    public RockPaperScissors(List<String> values) {
        rounds = values.stream().filter(it -> it.length() > 0).collect(toList());
    }

    public static void main(String[] args) {
        System.out.println("Rock Paper Scissors!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d2.txt"));
            RockPaperScissors rps = new RockPaperScissors(input);
            System.out.println("Total Score: " + rps.computeTotalScore(r -> RESOLVE_SYMBOL.get(r.charAt(2)))); // 15337
            System.out.println("Total Score 2: " + rps.computeTotalScore(r -> RESOLVE_SYMBOL_2.get(r.charAt(2)).get(r.charAt(0)))); // 11696
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int computeTotalScore(Function<String, Character> resolver) {
        return rounds.stream().map(round -> {
            Character opp = round.charAt(0);
            Character me = resolver.apply(round);
            return POINTS_PER_SYMBOL.get(me) + POINTS_FROM_GAME.get(me).get(opp);
        }).reduce(Integer::sum).orElseThrow();
    }

}
