package ch.lucas.y2022d22;

import ch.lucas.y2022d19.NotEnoughMinerals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MonkeyMap {

    private MyMap map;
    private String commands;

    public MonkeyMap(List<String> input) {
        List<String> mapData = new ArrayList<>();
        Iterator<String> inputIterator = input.iterator();
        while(inputIterator.hasNext()) {
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
            mm.providePassword(); // 11464
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
                for (int j = 0; j<steps; j++) {
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
        int password = 1000*agent.position.y + 4*agent.position.x;
        switch (agent.direction) {
            case UP -> password += 3;
            case DOWN -> password += 1;
            case LEFT -> password += 2;
            case RIGHT -> password += 0;
        }
        System.out.println("The password is " + password);
        System.out.println(agent);
    }

    public record Position(int x, int y){}
    public enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }
}
