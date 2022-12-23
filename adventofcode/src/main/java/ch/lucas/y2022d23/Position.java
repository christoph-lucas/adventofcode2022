package ch.lucas.y2022d23;

public record Position(int row, int col) {
    public Position nw() {
        return new Position(row - 1, col - 1);
    }

    public Position n() {
        return new Position(row - 1, col);
    }

    public Position ne() {
        return new Position(row - 1, col + 1);
    }

    public Position w() {
        return new Position(row, col - 1);
    }

    public Position e() {
        return new Position(row, col + 1);
    }

    public Position sw() {
        return new Position(row + 1, col - 1);
    }

    public Position s() {
        return new Position(row + 1, col);
    }

    public Position se() {
        return new Position(row + 1, col + 1);
    }
}
