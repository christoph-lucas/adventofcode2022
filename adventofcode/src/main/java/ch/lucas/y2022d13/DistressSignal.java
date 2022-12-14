package ch.lucas.y2022d13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DistressSignal {

    List<PairOfPackets> packetPairs = new ArrayList<>();
    List<TreeNode> allPackets = new ArrayList<>();

    public DistressSignal(List<String> input) {
        Iterator<String> lineIter = input.iterator();
        while (lineIter.hasNext()) {
            TreeNode left = TreeNode.parse(lineIter.next());
            TreeNode right = TreeNode.parse(lineIter.next());
            packetPairs.add(new PairOfPackets(left, right));
            allPackets.add(left);
            allPackets.add(right);
            if (lineIter.hasNext()) lineIter.next(); // skip empty line
        }
    }

    public static void main(String[] args) {
        System.out.println("Distress Signal!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d13.txt"));
            System.out.println("Correctly Ordered Pairs: " + new DistressSignal(input).findCorrectlyOrderedPairs()); // 5682
            System.out.println("Marker Indizes: " + new DistressSignal(input).orderAllWithMarkers()); // 20304?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int findCorrectlyOrderedPairs() {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i<packetPairs.size(); i++) {
            if (packetPairs.get(i).left.compareTo(packetPairs.get(i).right) < 0) {
                res.add(i+1);
            }
        }
        System.out.println(res);
        return res.stream().reduce(Integer::sum).orElseThrow();
    }

    public int orderAllWithMarkers() {
        TreeNode marker1 = TreeNode.parse("[[2]]");
        TreeNode marker2 = TreeNode.parse("[[6]]");
        allPackets.add(marker1);
        allPackets.add(marker2);
        List<TreeNode> allSorted = allPackets.stream().sorted().toList();

//        for (TreeNode n: allSorted) {
//            System.out.println(n.toString());
//        }

        int m1Idx = allSorted.indexOf(marker1) + 1;
        int m2Idx = allSorted.indexOf(marker2) + 1;
        return m1Idx * m2Idx;
    }

    private record PairOfPackets(TreeNode left, TreeNode right){}

}
