package ch.lucas.y2022d22;

public interface FlatMovable<T extends FlatMovable> {
    T moveRight();

    T moveLeft();

    T moveDown();

    T moveUp();
}
