package ch.lucas.y2022d15;

public record Coordinates(int x, int y) {
    public static Coordinates from(String s) {
        // x=2, y=18
        String[] parts = s.split(", ");
        int x = Integer.valueOf(parts[0].substring(2));
        int y = Integer.valueOf(parts[1].substring(2));
        return new Coordinates(x, y);
    }
}
