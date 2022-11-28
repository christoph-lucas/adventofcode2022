package ch.lucas.y2021d01;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class SonarSweeps {

    private final int[] depths;

    public SonarSweeps(List<String> values) {
        depths = values.stream().map(it -> it.trim()).filter(it -> it.length() > 0)
                .map(it -> Integer.valueOf(it)).mapToInt(Integer::intValue).toArray();
    }

    public static void main(String[] args) {
        System.out.println("Sonar Sweep!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y21d1.txt"));
            SonarSweeps sweeps = new SonarSweeps(input);
            System.out.println("Increases: " + sweeps.countIncreases());
            System.out.println("Increases (Triple Window): " + sweeps.countIncreasesTripleWindow());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int countIncreases() {
        int count = 0;
        for (int i = 0; i < depths.length - 1; i++) {
            if (depths[i + 1] > depths[i]) count++;
        }
        return count;
    }

    public int countIncreasesTripleWindow() {
        int count = 0;
        for (int i = 0; i < depths.length - 3; i++) {
            if (sumOf3(i + 1) > sumOf3(i)) count++;
        }
        return count;
    }

    private int sumOf3(int startIndex) {
        return depths[startIndex] + depths[startIndex + 1] + depths[startIndex + 2];
    }

}
