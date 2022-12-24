package ch.lucas.y2022d24;

public record Position(int row, int col) {
    public Position up() {
        return new Position(row - 1, col);
    }

    public Position down() {
        return new Position(row + 1, col);
    }

    public Position left() {
        return new Position(row, col - 1);
    }

    public Position right() {
        return new Position(row, col + 1);
    }
}
