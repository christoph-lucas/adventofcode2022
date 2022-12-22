package ch.lucas.y2022d22;

import static ch.lucas.y2022d22.Direction.RIGHT;

public class MapAgent implements Agent<Position> {

    private final MonkeyMap map;
    public Position position;
    public Direction direction;

    public MapAgent(Position position, MonkeyMap map) {
        this.position = position;
        this.map = map;
        this.direction = RIGHT;
    }

    public Position wouldMoveTo() {
        return map.getNextPosition(position, direction);
    }

    public void moveTo(Position p) {
        this.position = p;
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
        return "(" + position + ", " + direction + ")";
    }
}
