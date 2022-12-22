package ch.lucas.y2022d22;

public record Position(int x, int y) implements FlatMovable<Position> {
    public Position moveRight() {
        return new Position(x + 1, y);
    }

    public Position moveLeft() {
        return new Position(x - 1, y);
    }

    public Position moveDown() {
        return new Position(x, y + 1);
    }

    public Position moveUp() {
        return new Position(x, y - 1);
    }
}
