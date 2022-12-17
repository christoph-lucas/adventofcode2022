package ch.lucas.y2022d17;

public enum Rock {
    MINUS(new char[][]{{'#', '#', '#', '#'}}),

    PLUS(new char[][]{
            {'.', '#', '.'},
            {'#', '#', '#'},
            {'.', '#', '.'}
    }),

    ELL(new char[][]{
            {'.', '.', '#'},
            {'.', '.', '#'},
            {'#', '#', '#'},
    }),

    EYE(new char[][]{{'#'}, {'#'}, {'#'}, {'#'}}),

    DOT(new char[][]{
            {'#', '#'},
            {'#', '#'},
    });

    public final char[][] shape;

    Rock(char[][] shape) {
        this.shape = shape;
    }

}
