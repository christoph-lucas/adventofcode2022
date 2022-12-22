package ch.lucas.y2022d22;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The two methods to go over the commands and move the agent could (and should) be unified, by having a generic Agent
 * for either Position or CubeCoordinates. This refactoring is left to the reader as an exercise ;).
 */
public class MonkeyMap {

    private final MyMap map;
    private final String commands;

    public MonkeyMap(List<String> input) {
        List<String> mapData = new ArrayList<>();
        Iterator<String> inputIterator = input.iterator();
        while (inputIterator.hasNext()) {
            String line = inputIterator.next();
            if (line.length() > 0) {
                mapData.add(line);
            } else {
                break;
            }
        }
        map = MyMap.from(mapData);
        System.out.println(map);
        commands = inputIterator.next();
    }

    public static void main(String[] args) {
        System.out.println("Monkey Map\n");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d22.txt"));
            MonkeyMap mm = new MonkeyMap(input);
//            mm.providePassword(); // 11464
            mm.providePassword2(50); // 197122
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void providePassword() {
        Agent agent = new Agent(map.getInitialPosition());

        int cmdPtr = 0;
        while (cmdPtr < commands.length()) {
            System.out.println(agent);
            if (!Character.isDigit(commands.charAt(cmdPtr))) {
                switch (commands.charAt(cmdPtr)) {
                    case 'R' -> agent.turnRight();
                    case 'L' -> agent.turnLeft();
                    default -> throw new IllegalArgumentException();
                }
                cmdPtr++;
            } else {
                int i = cmdPtr;
                while (i < commands.length() && Character.isDigit(commands.charAt(i))) i++;
                int steps = Integer.parseInt(commands.substring(cmdPtr, i));
                System.out.println("Command says move " + steps + " " + agent.direction);
                for (int j = 0; j < steps; j++) {
                    Position next = map.getNextPosition(agent.position, agent.direction);
                    if (!map.isWall(next)) {
                        agent.position = next;
                    } else {
                        break;
                    }
                }
                cmdPtr = i;
            }
        }
        int password = 1000 * agent.position.y + 4 * agent.position.x;
        switch (agent.direction) {
            case UP -> password += 3;
            case DOWN -> password += 1;
            case LEFT -> password += 2;
            case RIGHT -> password += 0;
        }
        System.out.println("The password is " + password);
        System.out.println(agent);
    }

    public void providePassword2(int cubeSize) {
        MonkeyCube c = new MonkeyCube(map, cubeSize);
        CubeAgent agent = new CubeAgent(c.getInitialCoordinates());

        int cmdPtr = 0;
        while (cmdPtr < commands.length()) {
            System.out.println(agent);
            if (!Character.isDigit(commands.charAt(cmdPtr))) {
                switch (commands.charAt(cmdPtr)) {
                    case 'R' -> agent.turnRight();
                    case 'L' -> agent.turnLeft();
                    default -> throw new IllegalArgumentException();
                }
                cmdPtr++;
            } else {
                int i = cmdPtr;
                while (i < commands.length() && Character.isDigit(commands.charAt(i))) i++;
                int steps = Integer.parseInt(commands.substring(cmdPtr, i));
                System.out.println("Command says move " + steps + " " + agent.direction);
                for (int j = 0; j < steps; j++) {
                    CubeCoordinates next = agent.wouldMoveTo();
                    if (!c.isWall(next)) {
                        agent.coordinates = next;
                    } else {
                        break;
                    }
                }
                cmdPtr = i;
            }
        }
        System.out.println(agent);
        System.out.println(c.getMapPosition(agent.coordinates));
        System.out.println();
        c.printPassword(agent);
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public Direction turnLeft() {
            return switch (this) {
                case UP -> LEFT;
                case LEFT -> DOWN;
                case DOWN -> RIGHT;
                case RIGHT -> UP;
            };
        }

        public Direction turnRight() {
            return switch (this) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }

        public int passwordPart() {
            return switch (this) {
                case UP -> 3;
                case DOWN -> 1;
                case LEFT -> 2;
                case RIGHT -> 0;
            };
        }

    }

    public record Position(int x, int y) {
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
}
