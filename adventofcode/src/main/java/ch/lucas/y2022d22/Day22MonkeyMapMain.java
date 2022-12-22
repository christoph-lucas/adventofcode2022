package ch.lucas.y2022d22;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Day22MonkeyMapMain {

    private final MonkeyMap map;
    private final String commands;

    public Day22MonkeyMapMain(List<String> input) {
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
        map = MonkeyMap.from(mapData);
        System.out.println(map);
        commands = inputIterator.next();
    }

    public static void main(String[] args) {
        System.out.println("Monkey Map\n");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d22.txt"));
            Day22MonkeyMapMain mm = new Day22MonkeyMapMain(input);
            mm.providePassword(); // 11464
            mm.providePassword2(50); // 197122
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void providePassword() {
        MapAgent mapAgent = new MapAgent(map.getInitialPosition(), map);

        commandInterpreter(mapAgent, map);

        int password = 1000 * mapAgent.position.y() + 4 * mapAgent.position.x() + mapAgent.direction().passwordPart();
        System.out.println("The password for Part 1 is " + password);
//        System.out.println(mapAgent);
        System.out.println();
        System.out.println();
    }

    public void providePassword2(int cubeSize) {
        MonkeyCube c = new MonkeyCube(map, cubeSize);
        CubeAgent agent = new CubeAgent(c.getInitialCoordinates());

        commandInterpreter(agent, c);

        System.out.println(agent);
        System.out.println(c.getMapPosition(agent.coordinates));
        System.out.println();
        c.printPassword(agent);
    }

    private <T extends FlatMovable<T>> void commandInterpreter(Agent<T> agent, MappingAid<T> map) {
        int cmdPtr = 0;
        while (cmdPtr < commands.length()) {
//            System.out.println(agent);
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
//                System.out.println("Command says move " + steps + " " + agent.direction());
                for (int j = 0; j < steps; j++) {
                    T next = agent.wouldMoveTo();
                    if (!map.isWall(next)) {
                        agent.moveTo(next);
                    } else {
                        break;
                    }
                }
                cmdPtr = i;
            }
        }
    }

}
