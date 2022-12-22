package ch.lucas.y2022d22;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CubeCoordinates {

    private static final Function<Integer, Integer> inc = x -> x + 1;
    private static final Function<Integer, Integer> dec = x -> x - 1;

    private final int n;
    private final Map<Axis, Integer> coords = new HashMap<>();

    private final Axis leftRightAxis;
    private final Axis upDownAxis;
    private final Axis constantAxis;
    private final Function<Integer, Integer> moveRightOp;
    private final Function<Integer, Integer> moveLeftOp;
    private final Function<Integer, Integer> moveUpOp;
    private final Function<Integer, Integer> moveDownOp;
    private final boolean increaseToTheRight;
    private final boolean increaseDownwards;

    public CubeCoordinates(int n, int x, int y, int z,
                           Axis leftRightAxis, Axis upDownAxis,
                           boolean increaseToTheRight, boolean increaseDownwards) {
        this.n = n;
        this.leftRightAxis = leftRightAxis;
        this.upDownAxis = upDownAxis;
        constantAxis = getConstantAxis(leftRightAxis, upDownAxis);

        coords.put(Axis.X, x);
        coords.put(Axis.Y, y);
        coords.put(Axis.Z, z);

        this.increaseToTheRight = increaseToTheRight;
        this.increaseDownwards = increaseDownwards;

        moveRightOp = increaseToTheRight ? inc : dec;
        moveLeftOp = increaseToTheRight ? dec : inc;
        moveDownOp = increaseDownwards ? inc : dec;
        moveUpOp = increaseDownwards ? dec : inc;

        if (!isValidState()) throw new IllegalStateException();
    }

    public int x() {
        return coords.get(Axis.X);
    }

    public int y() {
        return coords.get(Axis.Y);
    }

    public int z() {
        return coords.get(Axis.Z);
    }

    public CubeCoordinates moveRight() {
        int leftRightCoord = coords.get(leftRightAxis);
        int leftRightCoordNew = moveRightOp.apply(leftRightCoord);
        Map<Axis, Integer> newCoords = cloneCoords();
        newCoords.put(leftRightAxis, leftRightCoordNew);

        if (inRange(leftRightCoordNew)) { // easy case, no wrapping
            return new CubeCoordinates(n, newCoords.get(Axis.X), newCoords.get(Axis.Y), newCoords.get(Axis.Z),
                    leftRightAxis, upDownAxis, increaseToTheRight, increaseDownwards);
        }

        // need to swap leftRightAxis with constantAxis
        int constantAxisCoordCur = coords.get(constantAxis); // must be 0 or n+1 -> will be 1 or n
        int constantAxisCoordNew = constantAxisCoordCur == 0 ? 1 : n;
        newCoords.put(constantAxis, constantAxisCoordNew);
        return new CubeCoordinates(n, newCoords.get(Axis.X), newCoords.get(Axis.Y), newCoords.get(Axis.Z),
                constantAxis, upDownAxis, constantAxisCoordNew == 1, increaseDownwards);
    }

    public CubeCoordinates moveLeft() {
        int leftRightCoord = coords.get(leftRightAxis);
        int leftRightCoordNew = moveLeftOp.apply(leftRightCoord);
        Map<Axis, Integer> newCoords = cloneCoords();
        newCoords.put(leftRightAxis, leftRightCoordNew);

        if (inRange(leftRightCoordNew)) { // easy case, no wrapping
            return new CubeCoordinates(n, newCoords.get(Axis.X), newCoords.get(Axis.Y), newCoords.get(Axis.Z),
                    leftRightAxis, upDownAxis, increaseToTheRight, increaseDownwards);
        }

        // need to swap leftRightAxis with constantAxis
        int constantAxisCoordCur = coords.get(constantAxis); // must be 0 or n+1 -> will be 1 or n
        int constantAxisCoordNew = constantAxisCoordCur == 0 ? 1 : n;
        newCoords.put(constantAxis, constantAxisCoordNew);
        return new CubeCoordinates(n, newCoords.get(Axis.X), newCoords.get(Axis.Y), newCoords.get(Axis.Z),
                constantAxis, upDownAxis, constantAxisCoordNew != 1, increaseDownwards);
    }

    public CubeCoordinates moveDown() {
        int upDownCoord = coords.get(upDownAxis);
        int upDownCoordNew = moveDownOp.apply(upDownCoord);
        Map<Axis, Integer> newCoords = cloneCoords();
        newCoords.put(upDownAxis, upDownCoordNew);

        if (inRange(upDownCoordNew)) { // easy case, no wrapping
            return new CubeCoordinates(n, newCoords.get(Axis.X), newCoords.get(Axis.Y), newCoords.get(Axis.Z),
                    leftRightAxis, upDownAxis, increaseToTheRight, increaseDownwards);
        }

        // need to swap upDownAxis with constantAxis
        int constantAxisCoordCur = coords.get(constantAxis); // must be 0 or n+1 -> will be 1 or n
        int constantAxisCoordNew = constantAxisCoordCur == 0 ? 1 : n;
        newCoords.put(constantAxis, constantAxisCoordNew);
        return new CubeCoordinates(n, newCoords.get(Axis.X), newCoords.get(Axis.Y), newCoords.get(Axis.Z),
                leftRightAxis, constantAxis, increaseToTheRight, constantAxisCoordNew == 1);
    }

    public CubeCoordinates moveUp() {
        int upDownCoord = coords.get(upDownAxis);
        int upDownCoordNew = moveUpOp.apply(upDownCoord);
        Map<Axis, Integer> newCoords = cloneCoords();
        newCoords.put(upDownAxis, upDownCoordNew);

        if (inRange(upDownCoordNew)) { // easy case, no wrapping
            return new CubeCoordinates(n, newCoords.get(Axis.X), newCoords.get(Axis.Y), newCoords.get(Axis.Z),
                    leftRightAxis, upDownAxis, increaseToTheRight, increaseDownwards);
        }

        // need to swap upDownAxis with constantAxis
        int constantAxisCoordCur = coords.get(constantAxis); // must be 0 or n+1 -> will be 1 or n
        int constantAxisCoordNew = constantAxisCoordCur == 0 ? 1 : n;
        newCoords.put(constantAxis, constantAxisCoordNew);
        return new CubeCoordinates(n, newCoords.get(Axis.X), newCoords.get(Axis.Y), newCoords.get(Axis.Z),
                leftRightAxis, constantAxis, increaseToTheRight, constantAxisCoordNew != 1);
    }

    private Axis getConstantAxis(Axis a1, Axis a2) {
        if (Axis.X != a1 && Axis.X != a2) return Axis.X;
        if (Axis.Y != a1 && Axis.Y != a2) return Axis.Y;
        if (Axis.Z != a1 && Axis.Z != a2) return Axis.Z;
        throw new IllegalStateException();
    }

    private boolean isValidState() {
        if (leftRightAxis == upDownAxis || upDownAxis == constantAxis || leftRightAxis == constantAxis) return false;

        int constCoord = coords.get(constantAxis);
        if (constCoord != 0 && constCoord != n + 1) return false;

        int leftRightCoord = coords.get(leftRightAxis);
        if (!inRange(leftRightCoord)) return false;

        int upDownCoord = coords.get(upDownAxis);
        if (!inRange(upDownCoord)) return false;

        if (moveRightOp == moveLeftOp) return false;
        if (moveDownOp == moveUpOp) return false;

        return upDownCoord > 0 && upDownCoord <= n;
    }

    private Map<Axis, Integer> cloneCoords() {
        Map<Axis, Integer> result = new HashMap<>();
        result.putAll(coords);
        return result;
    }

    private boolean inRange(int coord) {
        return coord >= 1 && coord <= n;
    }

    @Override
    public String toString() {
        return "(x=" + x() + ", y=" + y() + ", z=" + z() + ")";
    }

    public enum Axis {X, Y, Z}
}
