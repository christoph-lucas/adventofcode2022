package ch.lucas.y2022d21;

import ch.lucas.y2022d20.GrovePositioningSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonkeyMath {

    private List<Monkey> monkeys;

    private Map<String, Monkey> name2Monkey = new HashMap<>();

    public MonkeyMath(List<String> monkeysRaw) {
        monkeys = monkeysRaw.stream().map(Monkey::from).toList();
        for (Monkey monkey : monkeys) {
            name2Monkey.put(monkey.name, monkey);
        }
    }

    public static void main(String[] args) {
        System.out.println("Monkey Math\n");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d21.txt"));
            System.out.println("Root value: " +  new MonkeyMath(input).eval("root") + "\n"); // 331319379445180
            System.out.println("Human value: " +  new MonkeyMath(input).getHumanValue() + "\n"); // 3715799488132
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long eval(String name) {
        if (name.equals("humn")) System.out.println("Evaluating human -> should not happen in part 2!");
        Monkey monkey = name2Monkey.get(name);
        if (monkey.result == null) monkey.result = monkey.op.apply(eval(monkey.left), eval(monkey.right));
        return monkey.result;
    }

    public long getHumanValue() {
        Monkey root = name2Monkey.get("root");
        if (contains(root.left, "humn")) {
            // human on left side
            long rightSideValue = eval(root.right);
            return getHumanValue(root.left, rightSideValue);
        } else {
            // human on right side
            long leftSideValue = eval(root.left);
            return getHumanValue(root.right, leftSideValue);
        }

    }

    private long getHumanValue(String monkeyName, long target) {
        if (monkeyName.equals("humn")) return target;
        Monkey monkey = name2Monkey.get(monkeyName);
        if (monkey.result != null) throw new IllegalStateException("If based on human, cannot have result.");
        if (monkey.op == null) throw new IllegalStateException("If based on human, must contain some operation.");

        if (contains(monkey.left, "humn")) {
            // human on left side
            long rightSideValue = eval(monkey.right);
            long newTarget = switch (monkey.op) {
                case ADD -> target - rightSideValue;
                case SUBSTRACT -> target + rightSideValue;
                case MULT -> target / rightSideValue;
                case DIV -> target * rightSideValue;
            };
            return getHumanValue(monkey.left, newTarget);
        } else {
            // human on right side
            long leftSideValue = eval(monkey.left);
            long newTarget = switch (monkey.op) {
                case ADD -> target - leftSideValue;
                case SUBSTRACT -> leftSideValue - target;
                case MULT -> target / leftSideValue;
                case DIV -> leftSideValue / target;
            };
            return getHumanValue(monkey.right, newTarget);
        }
    }

    private boolean contains(String monkeyName, String name) {
        Monkey monkey = name2Monkey.get(monkeyName);
        if (monkey == null) return false;
        if (monkey.basedOnHuman != null) return monkey.basedOnHuman;

        if (monkey.name.equals(name)) {
            monkey.basedOnHuman = true;
            return monkey.basedOnHuman;
        }
        if (monkey.result != null) {
            monkey.basedOnHuman = false;
            return monkey.basedOnHuman;
        }
        if (contains(monkey.left, name)) {
            monkey.basedOnHuman = true;
            return monkey.basedOnHuman;
        }
        if (contains(monkey.right, name)) {
            monkey.basedOnHuman = true;
            return monkey.basedOnHuman;
        };
        monkey.basedOnHuman = false;
        return monkey.basedOnHuman;
    }


    public static class Monkey {
        public String name;
        public Long result;
        public String left;
        public String right;
        public Operation op;
        public Boolean basedOnHuman;

        public Monkey(String name, Long result, String left, String right, Operation op) {
            this.name = name;
            this.result = result;
            this.left = left;
            this.right = right;
            this.op = op;
        }

        public static Monkey from(String raw) {
            String parts[] = raw.split(": ");
            try {
                long result = Long.parseLong(parts[1].trim());
                return new Monkey(parts[0], result, null, null, null);
            } catch (NumberFormatException e) {
                String[] expr = parts[1].split(" ");
                return new Monkey(parts[0], null, expr[0], expr[2], Operation.from(expr[1]));
            }
        }
    }

    public enum Operation {
        ADD {
            @Override
            public long apply(long first, long second) {
                return first + second;
            }
        },
        SUBSTRACT {
            @Override
            public long apply(long first, long second) {
                return first - second;
            }
        },
        MULT {
            @Override
            public long apply(long first, long second) {
                return first * second;
            }
        },
        DIV {
            @Override
            public long apply(long first, long second) {
                return first / second;
            }
        };

        public static Operation from(String raw) {
            return switch(raw) {
                case "+" -> ADD;
                case "-" -> SUBSTRACT;
                case "*" -> MULT;
                case "/" -> DIV;
                default -> throw new IllegalArgumentException();
            };
        }
        public abstract long apply(long first, long second);
    }

}
