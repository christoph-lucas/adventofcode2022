package ch.lucas.y2022d01;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.PriorityQueue;

public class CalorieCounting {

    private final List<Integer> foodItems;

    public CalorieCounting(List<String> values) {
        foodItems = values.stream().map(it -> it.trim())
                .map(it -> it.length() > 0 ? Integer.valueOf(it) : -1).toList();
    }

    public static void main(String[] args) {
        System.out.println("Calorie Counting!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d1.txt"));
            CalorieCounting cc = new CalorieCounting(input);
            System.out.println("Top 1: " + cc.getTop(1)); // 69310
            System.out.println("Top 3: " + cc.getTop(3)); // 206104
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTop(int n) {
        PriorityQueue<Integer> topN = new PriorityQueue<>();
        int curSum = 0;
        for (Integer item : foodItems) {
            if (item >= 0) {
                curSum += item;
            } else {
                topN.add(curSum);
                if (topN.size() > n) topN.remove();
                curSum = 0;
            }
        }
        return topN.stream().reduce(Integer::sum).orElseThrow();
    }

}
