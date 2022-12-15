package ch.lucas.y2022d15;

public record Sensor(Coordinates position, Coordinates closestBeacon) {

    public static Sensor from(String s) {
        // Sensor at x=2, y=18: closest beacon is at x=-2, y=15
        String[] parts = s.split(":");

        String prefix = "Sensor at ";
        Coordinates sensor = Coordinates.from(parts[0].substring(prefix.length()));

        prefix = " closest beacon is at ";
        Coordinates beacon = Coordinates.from(parts[1].substring(prefix.length()));

        return new Sensor(sensor, beacon);
    }

    public int dist() {
        return Math.abs(position.x() - closestBeacon.x()) + Math.abs(position.y() - closestBeacon.y());
    }
}
