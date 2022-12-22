package ch.lucas.y2022d22;

import ch.lucas.y2022d22.MonkeyMap.Direction;

import static ch.lucas.y2022d22.MonkeyMap.Direction.RIGHT;

public class CubeAgent {

    public CubeCoordinates coordinates;
    public Direction direction;

    public CubeAgent(CubeCoordinates coordinates) {
        this.coordinates = coordinates;
        this.direction = RIGHT;
    }

    public CubeCoordinates wouldMoveTo() {
        return switch (direction) {
            case UP -> coordinates.moveUp();
            case LEFT -> coordinates.moveLeft();
            case DOWN -> coordinates.moveDown();
            case RIGHT -> coordinates.moveRight();
        };
    }

    public void turnLeft() {
        direction = direction.turnLeft();
    }

    public void turnRight() {
        direction = direction.turnRight();
    }

    @Override
    public String toString() {
        return "(" + coordinates + ", " + direction + ")";
    }

}
