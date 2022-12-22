package ch.lucas.y2022d22;

public interface Agent<T extends FlatMovable<T>> {
    T wouldMoveTo();

    void moveTo(T p);

    void turnLeft();

    void turnRight();

    Direction direction();
}
