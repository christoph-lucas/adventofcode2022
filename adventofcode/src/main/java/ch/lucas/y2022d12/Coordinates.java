package ch.lucas.y2022d12;

import java.util.ArrayList;
import java.util.List;

public class Coordinates {

    public static final char END = '$';

    private char[][] topo;
    private int row;
    private int col;
    private int dist;
    private CheckingDirection dir;
    private boolean endMarked = false;

    public Coordinates(char[][] topo, int row, int col, int dist, CheckingDirection dir) {
        this.topo = topo;
        this.row = row;
        this.col = col;
        this.dist = dist;
        this.dir = dir;
    }

    public int dist() {
        return dist;
    }

    public boolean end() {
        return topo[row][col] == END;
    }

    public boolean valley() {
        return eval() == 'a';
    }

    public List<Coordinates> getAndMarkValidNeighbours() {
        List<Coordinates> res = new ArrayList<>();
        if (canGo(left())) res.add(left());
        if (canGo(right())) res.add(right());
        if (canGo(up())) res.add(up());
        if (canGo(down())) res.add(down());
        res.forEach(Coordinates::mark);
        return res;
    }

    private boolean valid() {
        return row >= 0 && row < topo.length
                && col >= 0 && col < topo[0].length
                && (end() || !marked());
    }

    private void mark() {
        if (end()) {
            endMarked = true;
        } else {
            topo[row][col] += 'A' - 'a';
        }
    }

    private boolean marked() {
        if (end()) return endMarked;
        char val = topo[row][col];
        return !(val >= 'a' && val <= 'z');
    }

    private char eval() {
        if (!marked() || end()) return topo[row][col];
        return (char) (topo[row][col] - 'A' + 'a');
    }

    private Coordinates left() {
        return new Coordinates(topo, row, col - 1, dist + 1, dir);
    }

    private Coordinates right() {
        return new Coordinates(topo, row, col + 1, dist + 1, dir);
    }

    private Coordinates up() {
        return new Coordinates(topo, row - 1, col, dist + 1, dir);
    }

    private Coordinates down() {
        return new Coordinates(topo, row + 1, col, dist + 1, dir);
    }

    private boolean canGo(Coordinates n) {
        return n.valid() && validStep(n);
    }

    private boolean validStep(Coordinates n) {
        if (CheckingDirection.UP.equals(dir)) {
            char next = n.end() ? 'z' : n.eval();
            return next - eval() <= 1;
        }
        char cur = end() ? 'z' : eval();
        return cur - n.eval() <= 1;
    }

    public enum CheckingDirection {
        UP, DOWN;
    }
}
