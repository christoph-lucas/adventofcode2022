package ch.lucas.y2022d16;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ProboscideaVolcanium {

    public static void main(String[] args) {
        System.out.println("Proboscidea Volcanium!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d16.txt"));
            System.out.println("Max pressure release with 1 agent: " + new ValveNetwork(input).computeMaxPressureReleaseMultiAgent(30, 1)); // 1701
            System.out.println("Max pressure release with 2 agents: " + new ValveNetwork(input).computeMaxPressureReleaseMultiAgent(26, 2)); // 2455
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
