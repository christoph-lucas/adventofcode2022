package ch.lucas.y2022d25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FullOfHotAir {

    private final List<String> input;

    public FullOfHotAir(List<String> input) {
        this.input = input;
    }

    public static void main(String[] args) {
        System.out.println("Full of Hot Air\n");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d25.txt"));
            new FullOfHotAir(input).part1();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void part1() {
        long sumOfSnafuValuesInDecimal = input.stream().map(this::snafu2Decimal).reduce(Long::sum).orElseThrow();
        System.out.println("The sum of the snafu values in decimal is " + sumOfSnafuValuesInDecimal);
        String res = decimal2Snafu(sumOfSnafuValuesInDecimal);
        System.out.println("In SNAFU that's " + res);
    }

    private long snafu2Decimal(String snafu) {
        long res = 0;
        long base = 1;
        for (int i = snafu.length() - 1; i >= 0; i--) {
            switch (snafu.charAt(i)) {
                case '2' -> res += 2 * base;
                case '1' -> res += 1 * base;
                case '0' -> res += 0 * base;
                case '-' -> res += (-1) * base;
                case '=' -> res += (-2) * base;
            }
            base *= 5;
        }
        return res;
    }

    private String decimal2Snafu(long val) {
        long r = val;
        StringBuilder res = new StringBuilder();

        while (true) {
            int cur = (int) (r % 5);
            r /= 5;
            char c = 'x';
            switch (cur) {
                case 0 -> c = '0';
                case 1 -> c = '1';
                case 2 -> c = '2';
                case 3 -> {
                    c = '=';
                    r++;
                }
                case 4 -> {
                    c = '-';
                    r++;
                }
            }
            res.append(c);
            if (r == 0) break;
        }

        return res.reverse().toString();
    }

}
