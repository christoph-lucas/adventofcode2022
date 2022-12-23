package ch.lucas.y2022d23;

import java.util.HashMap;
import java.util.Map;

import static ch.lucas.y2022d23.Elve.Direction.*;

public class Elve {

    private static final Direction[] checkOrder = {NORTH, SOUTH, WEST, EAST};

    private final Map<Position, Elve> pos2Elve;
    private Position position;
    private Position proposedPosition;

    public Elve(Position initialPosition, Map<Position, Elve> pos2Elve) {
        position = initialPosition;
        this.pos2Elve = pos2Elve;
    }

    public Position position() {
        return position;
    }

    public Position proposedPosition() {
        return proposedPosition;
    }

    public boolean proposePosition(int round) {
        if (!pos2Elve.containsKey(position.nw()) && !pos2Elve.containsKey(position.n()) && !pos2Elve.containsKey(position.ne())
                && !pos2Elve.containsKey(position.w()) && !pos2Elve.containsKey(position.e())
                && !pos2Elve.containsKey(position.sw()) && !pos2Elve.containsKey(position.s()) && !pos2Elve.containsKey(position.se())) {
            proposedPosition = position;
            return false;
        }

        Map<Direction, Boolean> possibleMoves = new HashMap<>();
        possibleMoves.put(NORTH, !pos2Elve.containsKey(position.nw()) && !pos2Elve.containsKey(position.n()) && !pos2Elve.containsKey(position.ne()));
        possibleMoves.put(WEST, !pos2Elve.containsKey(position.nw()) && !pos2Elve.containsKey(position.w()) && !pos2Elve.containsKey(position.sw()));
        possibleMoves.put(EAST, !pos2Elve.containsKey(position.ne()) && !pos2Elve.containsKey(position.e()) && !pos2Elve.containsKey(position.se()));
        possibleMoves.put(SOUTH, !pos2Elve.containsKey(position.sw()) && !pos2Elve.containsKey(position.s()) && !pos2Elve.containsKey(position.se()));

        int idxStart = round % 4;
        for (int i = 0; i < 4; i++) {
            Direction d = checkOrder[(idxStart + i) % 4];
            if (possibleMoves.get(d)) {
                proposedPosition = switch (d) {
                    case NORTH -> position.n();
                    case SOUTH -> position.s();
                    case WEST -> position.w();
                    case EAST -> position.e();
                };
                break;
            }
        }
        if (proposedPosition == null) proposedPosition = position;
        return true;
    }

    public boolean applyProposition() {
        if (!position.equals(proposedPosition)) {
            pos2Elve.remove(position);
            position = proposedPosition;
            pos2Elve.put(position, this);
            proposedPosition = null;
            return true;
        }
        proposedPosition = null;
        return false;
    }

    public void cancelProposition() {
        proposedPosition = null;
    }

    public enum Direction {
        NORTH, SOUTH, WEST, EAST
    }

}
