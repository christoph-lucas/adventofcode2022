package ch.lucas.y2022d19;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Most likely not the correct, or at least not the full answer! It computes the result correctly, but is mostly a
 * brute force approach with some heuristics to improve run time.
 */
public class NotEnoughMinerals {

    private final List<Blueprint> blueprints;

    private Map<State, Integer> cache;

    public NotEnoughMinerals(List<String> blueprintsRaw) {
        blueprints = blueprintsRaw.stream().map(Blueprint::from).collect(toList());
        blueprints.forEach(System.out::println);
    }

    public static void main(String[] args) {
        System.out.println("Not Enough Minerals\n");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d19.txt"));
            NotEnoughMinerals nem = new NotEnoughMinerals(input);
            nem.evaluateBlueprints(32, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void evaluateBlueprints(int minutes, int limitBlueprints) {
        State init = new State(
                new Robots(1, 0, 0, 0),
                new Resources(0, 0, 0, 0),
                minutes
        );

        int sumQualityLevels = 0;
        int productNumberOfGeodes = 1;
        for (Blueprint blueprint : blueprints.stream().limit(limitBlueprints).toList()) {
            Instant start = Instant.now();
            cache = new HashMap<>();
            int res = simulate(blueprint, init, 0);
            sumQualityLevels += blueprint.idx * res;
            productNumberOfGeodes *= res;
            System.out.println("Blueprint " + blueprint.idx + " has a maximum of " + res + " geode capacity. Elapsed time: " + Duration.between(start, Instant.now()).toSeconds() + "s");
        }
        System.out.println("Sum of Quality Levels: " + sumQualityLevels);
        System.out.println("Product of number of Geodes: " + productNumberOfGeodes);
    }

    private int simulate(Blueprint blueprint, State s, int knownMax) {
        int max = s.resources.geode;
        if (s.minRemaining == 0) return max;
        if (cache.containsKey(s)) return cache.get(s);
        if (bestConceivableGeodeNumber(s) <= knownMax) return knownMax;

        State sNext = s.updateOneTimeStep(); // let the bots collect
        for (Action a : getPossibleActions(blueprint, s)) { // determine possible actions based on state at beginning of round
            State sNextWithAction = apply(a, sNext, blueprint);
            max = Math.max(max, simulate(blueprint, sNextWithAction, max));
        }
        cache.put(s, max);
        return max;
    }

    private int bestConceivableGeodeNumber(State s) {
        int res = s.resources.geode + (s.robots.geodeBots * s.minRemaining);
        for (int i = s.minRemaining - 1; i > 0; i--) {
            res += i;
        }
        return res;
    }

    private State apply(Action a, State s, Blueprint bp) {
        return switch (a) {
            case BUILD_ORE_BOT -> s.addOreBot(bp);
            case BUILD_CLAY_BOT -> s.addClayBot(bp);
            case BUILD_OBSIDIAN_BOT -> s.addObsidianBot(bp);
            case BUILD_GEODE_BOT -> s.addGeodeBot(bp);
            case WAIT -> s;
        };
    }

    private List<Action> getPossibleActions(Blueprint bp, State s) {
        List<Action> res = new ArrayList<>();
        if (canBuild(bp.geodeBot, s.resources)) {
            res.add(Action.BUILD_GEODE_BOT);
            return res; // assume: if geode bot possible, that is the best action
        }
        if (canBuild(bp.obsidianBot, s.resources)) {
            int capacity = s.resources.obsidian + (s.robots.obsidianBots * s.minRemaining);
            int maxConsumption = bp.maxObsidian() * s.minRemaining;
            if (maxConsumption > capacity) res.add(Action.BUILD_OBSIDIAN_BOT);
        }
        if (canBuild(bp.clayBot, s.resources)) {
            int capacity = s.resources.clay + (s.robots.clayBots * s.minRemaining);
            int maxConsumption = bp.maxClay() * s.minRemaining;
            if (maxConsumption > capacity) res.add(Action.BUILD_CLAY_BOT);
        }
        if (canBuild(bp.oreBot, s.resources)) {
            int capacity = s.resources.ore + (s.robots.oreBots * s.minRemaining);
            int maxConsumption = bp.maxOre() * s.minRemaining;
            if (maxConsumption > capacity) res.add(Action.BUILD_ORE_BOT);
        }
        res.add(Action.WAIT);
        return res;
    }

    private boolean canBuild(Robot bot, Resources r) {
        return bot.ore <= r.ore
                && bot.clay <= r.clay
                && bot.obsidian <= r.obsidian;
    }

    private enum Action {
        WAIT, BUILD_ORE_BOT, BUILD_CLAY_BOT, BUILD_OBSIDIAN_BOT, BUILD_GEODE_BOT
    }

    private record State(Robots robots, Resources resources, int minRemaining) {
        public State updateOneTimeStep() {
            Resources newResources = new Resources(
                    resources.ore + robots.oreBots,
                    resources.clay + robots.clayBots,
                    resources.obsidian + robots.obsidianBots,
                    resources.geode + robots.geodeBots);
            return new State(robots, newResources, minRemaining - 1);
        }

        public State addOreBot(Blueprint blueprint) {
            return new State(robots.addOreBot(), resources.reduce(blueprint.oreBot), minRemaining);
        }

        public State addClayBot(Blueprint blueprint) {
            return new State(robots.addClayBot(), resources.reduce(blueprint.clayBot), minRemaining);
        }

        public State addObsidianBot(Blueprint blueprint) {
            return new State(robots.addObsidianBot(), resources.reduce(blueprint.obsidianBot), minRemaining);
        }

        public State addGeodeBot(Blueprint blueprint) {
            return new State(robots.addGeodeBot(), resources.reduce(blueprint.geodeBot), minRemaining);
        }
    }

    public record Robots(int oreBots, int clayBots, int obsidianBots, int geodeBots) {
        public Robots addOreBot() {
            return new Robots(oreBots + 1, clayBots, obsidianBots, geodeBots);
        }

        public Robots addClayBot() {
            return new Robots(oreBots, clayBots + 1, obsidianBots, geodeBots);
        }

        public Robots addObsidianBot() {
            return new Robots(oreBots, clayBots, obsidianBots + 1, geodeBots);
        }

        public Robots addGeodeBot() {
            return new Robots(oreBots, clayBots, obsidianBots, geodeBots + 1);
        }
    }

    public record Resources(int ore, int clay, int obsidian, int geode) {
        public Resources reduce(Robot bot) {
            return new Resources(ore - bot.ore, clay - bot.clay, obsidian - bot.obsidian, geode);
        }
    }


    public record Blueprint(int idx, Robot oreBot, Robot clayBot, Robot obsidianBot, Robot geodeBot) {

        public static Blueprint from(String raw) {
            String[] parts = raw.split(":");
            int idx = Integer.valueOf(parts[0].split(" ")[1]);

            String[] robotsRaw = parts[1].split("\\.");
            Robot ore = Robot.from(robotsRaw[0]);
            Robot clay = Robot.from(robotsRaw[1]);
            Robot obsidian = Robot.from(robotsRaw[2]);
            Robot geode = Robot.from(robotsRaw[3]);

            return new Blueprint(idx, ore, clay, obsidian, geode);
        }

        public int maxOre() {
            return Stream.of(oreBot.ore, clayBot.ore, obsidianBot.ore, geodeBot.ore).max(Integer::compareTo).orElseThrow();
        }

        public int maxClay() {
            return Stream.of(oreBot.clay, clayBot.clay, obsidianBot.clay, geodeBot.clay).max(Integer::compareTo).orElseThrow();
        }

        public int maxObsidian() {
            return Stream.of(oreBot.obsidian, clayBot.obsidian, obsidianBot.obsidian, geodeBot.obsidian).max(Integer::compareTo).orElseThrow();
        }
    }

    public record Robot(int ore, int clay, int obsidian) {

        public static Robot from(String raw) {
            String[] resources = raw.split("costs")[1].split("and");
            int ore = 0;
            int clay = 0;
            int obsidian = 0;
            for (String rawResource : resources) {
                String[] parts = rawResource.trim().split(" ");
                switch (parts[1]) {
                    case "ore":
                        ore = Integer.valueOf(parts[0]);
                        break;
                    case "clay":
                        clay = Integer.valueOf(parts[0]);
                        break;
                    case "obsidian":
                        obsidian = Integer.valueOf(parts[0]);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
            return new Robot(ore, clay, obsidian);
        }

    }

}
