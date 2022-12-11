package ch.lucas.y2022d11;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.LongUnaryOperator;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

public class MonkeyInTheMiddle {

    private final List<Monkey> monkeys;

    private final int mainDivisible;

    public MonkeyInTheMiddle(List<String> lines) {
        this.monkeys = new ArrayList<>();
        Iterator<String> iter = lines.iterator();
        while (iter.hasNext()) {
            String cur = iter.next();
            if (!cur.startsWith("Monkey")) throw new IllegalArgumentException();

            List<String> monkeyLines = new ArrayList<>();
            for (int i = 0; i < 5; i++) monkeyLines.add(iter.next());
            monkeys.add(new Monkey(monkeyLines));
            if (iter.hasNext()) iter.next(); // skip empty line at end
        }
        mainDivisible = this.monkeys.stream().map(Monkey::divisible).reduce((i1, i2) -> i1 * i2).orElseThrow();
        System.out.println("mainDivisible: " + mainDivisible);
    }

    public static void main(String[] args) {
        System.out.println("Monkey in the Middle!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d11.txt"));
            System.out.println("Monkey Business: " + new MonkeyInTheMiddle(input).runRounds(20, 3)); // 112'815
            System.out.println("Monkey Business: " + new MonkeyInTheMiddle(input).runRounds(10000, 1)); // 25'738'411'485
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BigInteger runRounds(int n, int worryDivider) {
        for (int round = 0; round < n; round++) {
            for (Monkey m : monkeys) {
                while (m.hasItem()) {
                    ThrowEvent throwEvent = m.inspectItem(worryDivider, mainDivisible);
                    monkeys.get(throwEvent.to).addItem(throwEvent.item);
                }
            }
        }

        return monkeys.stream().sorted(comparingInt(Monkey::inspections).reversed()).limit(2)
                .map(Monkey::inspections).map(BigInteger::valueOf).reduce((i1, i2) -> i1.multiply(i2)).orElseThrow();
    }


    private record ThrowEvent(int to, long item) {
    }

    private static final class Monkey {
        public final Deque<Long> items;
        public final int divisible;
        private final int throwToIfDivisible;
        private final int throwToIfNotDivisible;
        public int inspections = 0;
        private LongUnaryOperator operation;

        public Monkey(List<String> monkeyLines) {
            //   Starting items: 98, 92, 99, 51
            String prefix = "  Starting items: ";
            this.items = Arrays.stream(monkeyLines.get(0).substring(prefix.length()).split(", "))
                    .map(Long::valueOf).collect(Collectors.toCollection(() -> new ArrayDeque<>()));

            //   Operation: new = old * old
            prefix = "  Operation: new = ";
            String[] ops = monkeyLines.get(1).substring(prefix.length()).split(" ");
            if (ops[0].equals("old") && ops[2].equals("old")) {
                this.operation = switch (ops[1]) {
                    case "*" -> old -> old * old;
                    case "+" -> old -> old + old;
                    default -> throw new UnsupportedOperationException();
                };
            } else if (ops[0].equals("old")) {
                this.operation = switch (ops[1]) {
                    case "*" -> old -> old * Integer.valueOf(ops[2]);
                    case "+" -> old -> old + Integer.valueOf(ops[2]);
                    default -> throw new UnsupportedOperationException();
                };
            }

            //   Test: divisible by 17
            prefix = "  Test: divisible by ";
            this.divisible = Integer.valueOf(monkeyLines.get(2).substring(prefix.length()));
            //     If true: throw to monkey 6
            prefix = "    If true: throw to monkey ";
            this.throwToIfDivisible = Integer.valueOf(monkeyLines.get(3).substring(prefix.length()));
            //    If false: throw to monkey 3
            prefix = "    If false: throw to monkey ";
            this.throwToIfNotDivisible = Integer.valueOf(monkeyLines.get(4).substring(prefix.length()));
        }

        public boolean hasItem() {
            return !this.items.isEmpty();
        }

        public ThrowEvent inspectItem(int worryDivider, int modulo) {
            if (!hasItem()) throw new IllegalStateException();
            this.inspections++;

            long item = this.items.pollFirst();
            item = this.operation.applyAsLong(item);
            if (worryDivider > 1) item /= worryDivider;
            item %= modulo;
            if (item % divisible == 0) {
                return new ThrowEvent(throwToIfDivisible, item);
            }
            return new ThrowEvent(throwToIfNotDivisible, item);
        }

        public void addItem(long item) {
            this.items.add(item);
        }

        public int inspections() {
            return inspections;
        }

        public int divisible() {
            return divisible;
        }
    }
}
