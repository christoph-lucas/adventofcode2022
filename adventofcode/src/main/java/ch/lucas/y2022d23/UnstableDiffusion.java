package ch.lucas.y2022d23;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class UnstableDiffusion {

    public static void main(String[] args) {
        System.out.println("Unstable Diffusion\n");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d23.txt"));
            part1(new ElveGroup(input), 10); // 3987
            part2(new ElveGroup(input)); //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void part1(ElveGroup elves, int rounds) {
        elves.updateElvePositions(rounds);
        System.out.println("Free space in between: " + elves.getFreeSpaceInBetween());
    }

    public static void part2(ElveGroup elves) {
        int reqRounds = elves.updateElvePositions();
        System.out.println("Required number of rounds: " + reqRounds);
    }

}
