package ch.lucas.y2022d3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;

public class RucksackReorganization {

    private final List<String> rucksacks;

    public RucksackReorganization(List<String> values) {
        rucksacks = values.stream().filter(it -> it.length() > 0).toList();
    }

    public static void main(String[] args) {
        System.out.println("Rucksack Reorganization!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d3.txt"));
            RucksackReorganization rr = new RucksackReorganization(input);
            System.out.println("Total misplaced priorities: " + rr.sumMisplacedPriorities()); //8394
            System.out.println("Total badge priorities: " + rr.sumBadgePriorities()); //2413
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int sumMisplacedPriorities() {
        return rucksacks.stream()
                .map(this::misplacedItem)
                .map(this::priority)
                .reduce(Integer::sum).orElseThrow();
    }

    public int sumBadgePriorities() {
        return groupsOf(rucksacks, 3).stream()
                .map(this::getCommonItem)
                .map(this::priority)
                .reduce(Integer::sum).orElseThrow();
    }

    private Collection<List<String>> groupsOf(List<String> items, int n) {
        final AtomicInteger counter = new AtomicInteger(0);
        return items.stream().collect(groupingBy(it -> counter.getAndIncrement() / n)).values();
    }

    private char misplacedItem(String rucksack) {
        return getCommonItem(List.of(
                rucksack.substring(0, rucksack.length() / 2),
                rucksack.substring(rucksack.length() / 2)
        ));
    }

    private char getCommonItem(List<String> itemCollections) {
        Set<Character> options = null;
        for (String items : itemCollections) {
            if (options == null) {
                options = items.chars().mapToObj(e -> (char) e).collect(toCollection(() -> new HashSet<>()));
            } else {
                options = items.chars().mapToObj(e -> (char) e).filter(options::contains).collect(toCollection(() -> new HashSet<>()));
            }
        }
        if (options.size() != 1) throw new IllegalArgumentException();
        return options.stream().findFirst().orElseThrow();
    }

    private int priority(char c) {
        if ('a' <= c && c <= 'z') {
            return c - 'a' + 1;
        }
        return c - 'A' + 27;
    }
}
