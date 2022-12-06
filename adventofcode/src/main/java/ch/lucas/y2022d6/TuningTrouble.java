package ch.lucas.y2022d6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

public class TuningTrouble {

    private final Character[] signals;

    public TuningTrouble(String stream) {
        signals = new Character[stream.length()];
        stream.chars().mapToObj(e -> (char) e).collect(toList()).toArray(signals);
        System.out.println(Arrays.toString(signals));
    }

    public static void main(String[] args) {
        System.out.println("Tuning Trouble!");
        try {
            String input = Files.readAllLines(Paths.get("src/main/resources/input_y22d6.txt")).get(0);

            TuningTrouble tt = new TuningTrouble(input);
            System.out.println("Sync Point: " + tt.getSyncPoint(3)); // 1896
            System.out.println("Msg Point: " + tt.getSyncPoint(14)); // 1896
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getSyncPoint(int l) {
        for (int i = l - 1; i < signals.length; i++) {
            if (Arrays.stream(signals, i - l + 1, i + 1).distinct().count() == l) return i + 1;
        }
        throw new IllegalArgumentException();
    }

}
