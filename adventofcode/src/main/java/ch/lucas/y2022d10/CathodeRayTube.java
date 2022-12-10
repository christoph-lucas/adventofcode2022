package ch.lucas.y2022d10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.toCollection;

public class CathodeRayTube {

    private final List<Instruction> instructions;

    public CathodeRayTube(List<String> instructions) {
        this.instructions = instructions.stream().map(Instruction::from).toList();
    }

    public static void main(String[] args) {
        System.out.println("Cathode-Ray Tube!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d10.txt"));
            CathodeRayTube crt = new CathodeRayTube(input);
            List<Integer> signalStrengths = crt.getSignalStrengthsAt(List.of(20, 60, 100, 140, 180, 220));
            System.out.println("Signal Strengths: " + signalStrengths); // [60, 1260, 1700, 2940, 3780, 4620]
            System.out.println("Sum: " + signalStrengths.stream().reduce(Integer::sum).orElse(0)); // 14360
            crt.draw(); // BGKAEREZ
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getSignalStrengthsAt(List<Integer> cycleNumbers) {
        Queue<Integer> signalChecks = cycleNumbers.stream().sorted().collect(toCollection(() -> new LinkedList<>()));
        List<Integer> result = new ArrayList<>();
        BiConsumer<Integer, Integer> getSignalStrengths = (Integer cycle, Integer x) -> {
            if (signalChecks.peek().equals(cycle)) {
                signalChecks.poll();
                result.add(cycle * x);
            }
        };
        runProgram(cycleNumbers.get(cycleNumbers.size() - 1), getSignalStrengths);
        return result;
    }

    public void draw() {
        int height = 6;
        int width = 40;
        char[][] crt = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                crt[i][j] = '.';
            }
        }
        BiConsumer<Integer, Integer> updateCrt = (Integer cycle, Integer x) -> {
            int curX = (cycle - 1) % width;
            int curY = (cycle - 1) / width;
            if (curX >= x - 1 && curX <= x + 1) {
                crt[curY][curX] = '#';
            }
        };
        runProgram(240, updateCrt);
        prettyPrint(crt);
    }

    private void prettyPrint(char[][] a) {
        for (int r = 0; r < a.length; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < a[r].length; c++) {
                line.append(a[r][c]);
            }
            System.out.println(line);
        }
    }

    private void runProgram(int n_cycles, BiConsumer<Integer, Integer> action) {
        Queue<Instruction> instrQ = instructions.stream().collect(toCollection(() -> new LinkedList<>()));
        int performNextInstructionAtCycle = instrQ.peek().type.duration;
        int x = 1;
        for (int cycle = 1; cycle <= n_cycles; cycle++) {
            action.accept(cycle, x);
            if (cycle == performNextInstructionAtCycle) {
                x = instrQ.poll().applyTo(x);
                if (!instrQ.isEmpty()) {
                    performNextInstructionAtCycle = cycle + instrQ.peek().type.duration;
                }
            }
        }
    }

    private enum InstructionType {
        NOOP(1),
        ADDX(2);

        private final int duration;

        InstructionType(int duration) {
            this.duration = duration;
        }

        public static InstructionType from(String s) {
            return switch (s) {
                case "addx" -> ADDX;
                case "noop" -> NOOP;
                default -> throw new IllegalStateException("Unexpected value: " + s);
            };
        }
    }

    private record Instruction(InstructionType type, int value) {

        public static Instruction from(String s) {
            String[] parts = s.split(" ");
            InstructionType type = InstructionType.from(parts[0]);
            return switch (type) {
                case ADDX -> new Instruction(type, Integer.valueOf(parts[1]));
                case NOOP -> new Instruction(type, 0);
            };
        }

        public int applyTo(int x) {
            return switch (type) {
                case ADDX -> x + value;
                case NOOP -> x;
            };
        }
    }

}
