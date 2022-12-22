package ch.lucas.y2022d22;

public interface MappingAid<T extends FlatMovable<T>> {
    boolean isWall(T p);
}
