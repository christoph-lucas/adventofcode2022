package ch.lucas.y2022d18;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The first part uses a HashSet, hence it is linear for both time and space in the input size.
 * <p>
 * Using a 3d array for the second part makes both time and space cubic in the max coordinates. Since in the input
 * the max is 21, that is not an issue. The density is 2868/(21^3)~=31%, so the solution is actually still linear
 * in the input size.
 * <p>
 * For lower densities, there will most likely be several components of connected rock. Then, it would probably be
 * faster to determine the connected components of rock and solve the problem with the array solution for each connected
 * component separately. That solution should still be linear.
 */
public class BoilingBoulders {

    private final Set<Coordinates> coordinates;
    private final State[][][] states;
    int xMax;
    int yMax;
    int zMax;

    public BoilingBoulders(List<String> rawCoordinates) {
        List<Coordinates> coordinatesList = rawCoordinates.stream().map(Coordinates::from).toList();
        coordinates = new HashSet<>();
        for (Coordinates c : coordinatesList) {
            coordinates.add(c);
        }

        xMax = coordinates.stream().map(Coordinates::x).mapToInt(v -> v).max().orElseThrow();
        int xMin = coordinates.stream().map(Coordinates::x).mapToInt(v -> v).min().orElseThrow();
        yMax = coordinates.stream().map(Coordinates::x).mapToInt(v -> v).max().orElseThrow();
        int yMin = coordinates.stream().map(Coordinates::x).mapToInt(v -> v).min().orElseThrow();
        zMax = coordinates.stream().map(Coordinates::x).mapToInt(v -> v).max().orElseThrow();
        int zMin = coordinates.stream().map(Coordinates::x).mapToInt(v -> v).min().orElseThrow();

        System.out.println("WARN: Assuming that all xMin are 0 now (NB: Coordinates translated by -1). Check in the following line that this is the case.");
        System.out.println("xMin=" + xMin + ", " + "xMax=" + xMax + ", " + "yMin=" + yMin + ", " + "yMax=" + yMax + ", " + "zMin=" + zMin + ", " + "zMax=" + zMax);

        states = new State[xMax + 1][yMax + 1][zMax + 1];
        fillStateGrid();
    }


    public static void main(String[] args) {
        System.out.println("Proboscidea Volcanium!\n");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d18.txt"));
            System.out.println("=> Total Surface Area: " + new BoilingBoulders(input).surfaceArea()); // 4288
            System.out.println();
            System.out.println("=> Exterior Surface Area: " + new BoilingBoulders(input).exteriorSurfaceArea()); // 2494
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int surfaceArea() {
        int totalSurface = coordinates.size() * 6;
        for (Coordinates c : coordinates) {
            for (Coordinates cn : c.allNeighbouringCoordinates()) {
                if (coordinates.contains(cn)) totalSurface--;
            }
        }
        return totalSurface;
    }

    public int exteriorSurfaceArea() {
        int extSurface = coordinates.size() * 6;
        for (Coordinates c : coordinates) {
            for (Coordinates cn : c.allNeighbouringCoordinates()) {
                if (coordinates.contains(cn) || isEnclosed(cn)) {
                    extSurface--;
                }
            }
        }
        return extSurface;
    }

    private void fillStateGrid() {
        markSolidAndUnknown();
        markAirBubbles();
//        prettyPrint();
    }

    private void markSolidAndUnknown() {
        for (int x = 0; x <= xMax; x++) {
            for (int y = 0; y <= yMax; y++) {
                for (int z = 0; z <= zMax; z++) {
                    if (coordinates.contains(new Coordinates(x, y, z))) {
                        states[x][y][z] = State.SOLID;
                    } else {
                        states[x][y][z] = State.UNKNOWN;
                    }
                }
            }
        }
    }

    private void markAirBubbles() {
        for (int x = 0; x < states.length; x++) {
            for (int y = 0; y < states[0].length; y++) {
                for (int z = 0; z < states[0][0].length; z++) {
                    if (State.UNKNOWN.equals(states[x][y][z])) {
                        Coordinates c = new Coordinates(x, y, z);
                        Set<Coordinates> visited = new HashSet<>();
                        if (findEdge(c, visited)) {
                            markAllConnectedUnknown(c, State.EXTERIOR);
                        } else {
                            markAllConnectedUnknown(c, State.BUBBLE);
                        }
                    }
                }
            }
        }
    }

    private boolean findEdge(Coordinates c, Set<Coordinates> visited) {
        if (c.x < 0 || c.x > xMax || c.y < 0 || c.y > yMax || c.z < 0 || c.z > zMax) return true;
        if (visited.contains(c)) return false;
        visited.add(c);

        return switch (states[c.x][c.y][c.z]) {
            case SOLID -> false;
            case UNKNOWN -> c.allNeighbouringCoordinates().stream().map(cn -> findEdge(cn, visited)).anyMatch(it -> it);
            case BUBBLE, EXTERIOR -> throw new IllegalStateException();
        };
    }

    private void markAllConnectedUnknown(Coordinates c, State s) {
        if (c.x < 0 || c.x > xMax || c.y < 0 || c.y > yMax || c.z < 0 || c.z > zMax) return;
        if (State.UNKNOWN.equals(states[c.x][c.y][c.z])) {
            states[c.x][c.y][c.z] = s;
            c.allNeighbouringCoordinates().forEach(cn -> markAllConnectedUnknown(cn, s));
        }
    }

    private boolean isEnclosed(Coordinates c) {
        if (c.x < 0 || c.x > xMax || c.y < 0 || c.y > yMax || c.z < 0 || c.z > zMax) return false;
        return State.BUBBLE.equals(states[c.x][c.y][c.z]);
    }

    public void prettyPrint() {
        for (int x = 0; x < states.length; x++) {
            for (int y = 0; y < states[0].length; y++) {
                StringBuilder res = new StringBuilder();
                for (int z = 0; z < states[0][0].length; z++) {
                    res.append(states[x][y][z]);
                }
                System.out.println(res);
            }
            System.out.println("-----------------------");
        }
    }

    private enum State {
        UNKNOWN, SOLID, BUBBLE, EXTERIOR;

        @Override
        public String toString() {
            return switch (this) {
                case UNKNOWN -> "U";
                case SOLID -> "S";
                case BUBBLE -> "B";
                case EXTERIOR -> "E";
            };
        }
    }

    private record Coordinates(int x, int y, int z) {
        private static Coordinates from(String s) {
            String[] c = s.split(",");
            return new Coordinates(Integer.valueOf(c[0]) - 1, Integer.valueOf(c[1]) - 1, Integer.valueOf(c[2]) - 1);
        }

        private List<Coordinates> allNeighbouringCoordinates() {
            List<Coordinates> res = new ArrayList<>();
            res.add(new Coordinates(x - 1, y, z));
            res.add(new Coordinates(x + 1, y, z));
            res.add(new Coordinates(x, y - 1, z));
            res.add(new Coordinates(x, y + 1, z));
            res.add(new Coordinates(x, y, z - 1));
            res.add(new Coordinates(x, y, z + 1));
            return res;
        }
    }

}
