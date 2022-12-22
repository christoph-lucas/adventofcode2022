package ch.lucas.y2022d22;

import static ch.lucas.y2022d22.Direction.RIGHT;

public class CubeAgent implements Agent<CubeCoordinates> {

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

    public void moveTo(CubeCoordinates c) {
        this.coordinates = c;
    }

    public void turnLeft() {
        direction = direction.turnLeft();
    }

    public void turnRight() {
        direction = direction.turnRight();
    }

    public Direction direction() {
        return direction;
    }

    @Override
    public String toString() {
        return "(" + coordinates + ", " + direction + ")";
    }

}
