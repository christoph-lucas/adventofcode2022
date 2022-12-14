package ch.lucas.y2022d14;

import ch.lucas.y2022d12.HillClimbingAlgorithm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RegolithReservoir {

    public static void main(String[] args) {
        System.out.println("Regolith Reservoir!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d14.txt"));
            CaveSystem caveSystem = CaveSystem.parse(input, false);
            simulateSand(caveSystem);
//            System.out.println(caveSystem);
            System.out.println("Added Sandcorns: " + caveSystem.sandcorns()); //

            caveSystem = CaveSystem.parse(input, true);
            simulateSand(caveSystem);
            System.out.println(caveSystem);
            System.out.println("Added Sandcorns with floor: " + caveSystem.sandcorns()); //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void simulateSand(CaveSystem caveSystem) {
        while (caveSystem.addSandCorn()) {
        }
    }

}
