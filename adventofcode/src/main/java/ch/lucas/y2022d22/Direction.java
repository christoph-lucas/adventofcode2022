package ch.lucas.y2022d22;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public Direction turnLeft() {
        return switch (this) {
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
        };
    }

    public Direction turnRight() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    public int passwordPart() {
        return switch (this) {
            case UP -> 3;
            case DOWN -> 1;
            case LEFT -> 2;
            case RIGHT -> 0;
        };
    }

}
