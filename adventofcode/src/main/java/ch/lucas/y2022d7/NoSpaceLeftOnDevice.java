package ch.lucas.y2022d7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoSpaceLeftOnDevice {

    public final Dir fileSystemTree;

    public NoSpaceLeftOnDevice(String[] commandLineStream) {
        fileSystemTree = parseCommandLineStream(commandLineStream);
    }

    public static void main(String[] args) {
        System.out.println("No Space Left On Device!");
        try {
            List<String> inputList = Files.readAllLines(Paths.get("src/main/resources/input_y22d7.txt"));
            String[] input = new String[inputList.size()];
            inputList.toArray(input);
            NoSpaceLeftOnDevice noSpaceLeftOnDevice = new NoSpaceLeftOnDevice(input);
            List<Dir> dirsSmaller100k = noSpaceLeftOnDevice.dirSmallerThan(100000);
            System.out.println("Dirs < 100k: " + dirsSmaller100k);
            System.out.println("Sum of Size: " + dirsSmaller100k.stream().map(Dir::size).reduce(Integer::sum).orElse(0)); // 1778099

            System.out.println();

            // Part 2
            int rootSize = noSpaceLeftOnDevice.fileSystemTree.size();
            System.out.println("Root size: " + rootSize);
            int remaining = 70000000 - rootSize;
            System.out.println("Remaining size: " + remaining);
            int requiredToFreeup = 30000000 - remaining;
            System.out.println("Required to free up: " + requiredToFreeup);
            Optional<Dir> dir = noSpaceLeftOnDevice.smallestDirGreaterThan(requiredToFreeup);
            System.out.println("Smallest dir large enough: " + dir.get() + " (" + dir.get().size() + ")"); // 1623571
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Dir> dirSmallerThan(int limit) {
        List<Dir> result = new ArrayList<>();
        return dirSmallerThan(fileSystemTree, limit, result);
    }

    private List<Dir> dirSmallerThan(Dir d, int limit, List<Dir> result) {
        if (d.size() <= limit) result.add(d);
        for (Node child : d.children) {
            if (child instanceof Dir dc) dirSmallerThan(dc, limit, result);
        }
        return result;
    }

    public Optional<Dir> smallestDirGreaterThan(int limit) {
        return smallestDirGreaterThan(fileSystemTree, limit);
    }

    private Optional<Dir> smallestDirGreaterThan(Dir d, int limit) {
        Optional<Dir> currentSelection = Optional.empty();
        if (d.size() >= limit) currentSelection = Optional.of(d);
        for (Node child : d.children) {
            if (child instanceof Dir dc) {
                currentSelection = min(currentSelection, smallestDirGreaterThan(dc, limit));
            }
        }
        return currentSelection;
    }

    private Optional<Dir> min(Optional<Dir> c1, Optional<Dir> c2) {
        if (c1.isEmpty()) return c2;
        if (c2.isEmpty()) return c1;
        if (c1.get().size() < c2.get().size()) return c1;
        return c2;
    }

    private Dir parseCommandLineStream(String[] cls) {
        if (!cls[0].equals("$ cd /")) throw new IllegalArgumentException("Must start at root.");

        Dir root = new Dir("/", null);
        Dir currentDir = root;
        for (int i = 1; i < cls.length; i++) {
            String cmd = cls[i];
            if (cmd.equals("$ cd ..")) {
                currentDir = currentDir.parent;
            } else if (cmd.startsWith("$ cd ")) {
                currentDir = currentDir.getSubDirWithName(cmd.substring(5));
            } else if (cmd.equals("$ ls")) {
                while (i + 1 < cls.length && !cls[i + 1].startsWith("$")) {
                    i++;
                    String entry = cls[i];
                    if (entry.startsWith("dir ")) {
                        currentDir.addChild(new Dir(entry.substring(4), currentDir));
                    } else {
                        String[] sizeName = entry.split(" ", 2);
                        currentDir.addChild(new File(sizeName[1], Integer.valueOf(sizeName[0])));
                    }
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
        return root;
    }

    private interface Node {
        int size();
    }

    private static class Dir implements Node {
        private final String name;
        private final Dir parent;
        private final List<Node> children = new ArrayList<>();

        public Dir(String name, Dir parent) {
            this.name = name;
            this.parent = parent;
        }

        public void addChild(Node c) {
            this.children.add(c);
        }

        public Dir getSubDirWithName(String subDir) {
            for (Node c : children) {
                if (c instanceof Dir d && d.name.equals(subDir)) return d;
            }
            throw new IllegalArgumentException("No Sub Directory with name " + subDir);
        }

        public int size() {
            return children.stream().map(Node::size).reduce(Integer::sum).orElse(0);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private record File(String name, int size) implements Node {
        public int size() {
            return size;
        }
    }

}
