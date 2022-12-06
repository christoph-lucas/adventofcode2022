package ch.lucas.y2022d5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static java.util.stream.Collectors.toList;

public class SupplyStacks {

    private final List<Move> moves;

    private final Stack<Character>[] stacks;

    public SupplyStacks(List<String> values) {
        moves = values.stream().filter(it -> it.length() > 0).map(Move::new).collect(toList());
        System.out.println("Moves: " + moves);
        stacks = generateStacks();
    }

    public static void main(String[] args) {
        System.out.println("Camp Cleanup!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d5.txt"));

            SupplyStacks ss = new SupplyStacks(input).applyMoves();
            System.out.println("Top crates after moves: " + ss.getTopCrates()); // TDCHVHJTG

            ss = new SupplyStacks(input).applyMoves9001();
            System.out.println("Top crates after moves 9001: " + ss.getTopCrates()); // NGCMPJLHV
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    [F]         [L]     [M]
    //    [T]     [H] [V] [G] [V]
    //    [N]     [T] [D] [R] [N]     [D]
    //    [Z]     [B] [C] [P] [B] [R] [Z]
    //    [M]     [J] [N] [M] [F] [M] [V] [H]
    //    [G] [J] [L] [J] [S] [C] [G] [M] [F]
    //    [H] [W] [V] [P] [W] [H] [H] [N] [N]
    //    [J] [V] [G] [B] [F] [G] [D] [H] [G]
    //     1   2   3   4   5   6   7   8   9
    private Stack<Character>[] generateStacks() {
        Stack<Character>[] result = new Stack[9];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Stack<Character>();
        }
        pushAll("JHGMZNTF", result[0]);
        pushAll("VWJ", result[1]);
        pushAll("GVLJBTH", result[2]);
        pushAll("BPJNCDVL", result[3]);
        pushAll("FWSMPRG", result[4]);
        pushAll("GHCFBNVM", result[5]);
        pushAll("DHGMR", result[6]);
        pushAll("HNMVZD", result[7]);
        pushAll("GNFH", result[8]);


        for (int i = 0; i < result.length; i++) {
            System.out.println((i + 1) + ": " + Arrays.toString(result[i].toArray()));
        }

        return result;
    }

    private void pushAll(String elements, Stack<Character> stack) {
        elements.chars().mapToObj(e -> (char) e).forEach(it -> stack.push(it));
    }

    public SupplyStacks applyMoves() {
        for (Move m : moves) {
            for (int i = 0; i < m.quantity; i++) {
                stacks[m.to - 1].push(stacks[m.from - 1].pop());
            }
        }
        return this;
    }

    public SupplyStacks applyMoves9001() {
        for (Move m : moves) {
            Stack<Character> temp = new Stack<>();
            for (int i = 0; i < m.quantity; i++) {
                temp.push(stacks[m.from - 1].pop());
            }
            while (!temp.isEmpty()) {
                stacks[m.to - 1].push(temp.pop());
            }
        }
        return this;
    }

    public String getTopCrates() {
        StringBuilder result = new StringBuilder();
        for (Stack<Character> s : stacks) {
            result.append(s.peek());
        }
        return result.toString();
    }

    private record Move(int quantity, int from, int to) {

        public Move(String in) {
            this(Integer.valueOf(in.split(" ")[1]), Integer.valueOf(in.split(" ")[3]), Integer.valueOf(in.split(" ")[5]));
        }

        @Override
        public String toString() {
            return quantity + ":" + from + "->" + to;
        }
    }

}
