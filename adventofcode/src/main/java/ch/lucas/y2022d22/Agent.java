package ch.lucas.y2022d22;

import ch.lucas.y2022d22.MonkeyMap.Direction;
import ch.lucas.y2022d22.MonkeyMap.Position;

import static ch.lucas.y2022d22.MonkeyMap.Direction.*;

public class Agent {

    public Position position;
    public Direction direction;

    public Agent(Position position) {
        this.position = position;
        this.direction = RIGHT;
    }

    public void turnLeft() {
        direction = switch (direction) {
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
        };
    }

    public void turnRight() {
        direction = switch (direction) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    @Override
    public String toString() {
        return "(" + position + ", " + direction + ")";
    }
}
