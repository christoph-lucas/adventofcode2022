package ch.lucas.y2022d20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * The first two solutions with the DoubleLinkedList seem to be way simpler and more elegant, yet they do not work. They
 * do produce the correct result on the example, but not on the real input. Where the bug is, is not clear to me. Some
 * day I might compare the order after every single mixing step to see where it goes wrong...
 * <p>
 * The first solution is actually the most elegant, yet it would not work at all for the second part.
 */
public class GrovePositioningSystem {

    private final DoubleLinkedListNode head;
    private final int size;
    private final List<DoubleLinkedListNode> nodesOrig = new ArrayList<>();
    private final List<PlainNode> plainNodes = new ArrayList<>();
    private final long decryptionKey;
    private DoubleLinkedListNode zeroNode;
    private PlainNode zeroPlainNode;

    public GrovePositioningSystem(List<String> file, long decryptionKey) {
        size = file.size();
        this.decryptionKey = decryptionKey;
        System.out.println("Size = " + size);

        DoubleLinkedListNode dummyHead = new DoubleLinkedListNode(Integer.MIN_VALUE);
        DoubleLinkedListNode cur = dummyHead;
        for (String elem : file) {
            Long val = Long.valueOf(elem) * decryptionKey;
            cur.next = new DoubleLinkedListNode(val);
            nodesOrig.add(cur.next);
            if (cur.next.val == 0) {
                zeroNode = cur.next;
            }
            cur.next.prev = cur;
            cur = cur.next;

            PlainNode n = new PlainNode(val);
            plainNodes.add(n);
            if (val == 0) zeroPlainNode = n;
        }

        head = dummyHead.next;
        // make the list circular on purpose
        head.prev = cur;
        cur.next = head;
        System.out.println(head);
    }

    public static void main(String[] args) {
        System.out.println("Grove Positioning System\n");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d20.txt"));
            System.out.println("=> Grove Coordinates: " + new GrovePositioningSystem(input, 1).getGroveCoordinates() + "\n"); // NOT -5
            System.out.println("=> Grove Coordinates2: " + new GrovePositioningSystem(input, 1).getGroveCoordinates2() + "\n"); // NOT -5
            System.out.println("=> Grove Coordinates3: " + new GrovePositioningSystem(input, 1).getGroveCoordinates3(1) + "\n"); // 8302
            System.out.println("=> Grove Coordinates3: " + new GrovePositioningSystem(input, 811589153).getGroveCoordinates3(10) + "\n"); // 656575624777
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getGroveCoordinates() {
        DoubleLinkedListNode cur = head.next;
        head.mix();
//        printFile();
        int count = 1;
        while (count < size) {
            DoubleLinkedListNode next = cur.next;
            if (cur.mix()) {
                count++;
//                printFile();
            }
            cur = next;
        }
        printFile();
        return computeGroveCoordinates();
    }

    public int getGroveCoordinates2() {
        for (DoubleLinkedListNode n : nodesOrig) n.mix();
        printFile();
        return computeGroveCoordinates();
    }

    private int computeGroveCoordinates() {
        System.out.println(zeroNode);
        DoubleLinkedListNode node = zeroNode;
        int res = 0;
        node = moveSteps(node, 1000);
        res += node.val;
        node = moveSteps(node, 1000);
        res += node.val;
        node = moveSteps(node, 1000);
        res += node.val;
        return res;
    }

    private DoubleLinkedListNode moveSteps(DoubleLinkedListNode node, int n) {
        for (int i = 0; i < n; i++) node = node.next;
        return node;
    }


    private void printFile() {
        List<Long> elems = new ArrayList<>();
        elems.add(head.val);
        DoubleLinkedListNode cur = head.next;
        while (cur != head) {
            elems.add(cur.val);
            cur = cur.next;
        }
        System.out.println(elems.stream().map(it -> it.toString()).collect(joining(", ")));
    }

    public long getGroveCoordinates3(int rounds) {
        List<PlainNode> mixed = new ArrayList<>(plainNodes);
        for (int i = 0; i < rounds; i++) {
            System.out.println("Started round " + (i + 1));
            for (PlainNode n : plainNodes) {
                int idxBefore = mixed.indexOf(n);

                long idxAfter;
                if (n.val > 0 && (idxBefore + n.val) >= size - 1) {
                    idxAfter = (idxBefore + n.val - size + 1) % (size - 1);
                } else if (n.val < 0 && idxBefore + n.val < 0) {
                    idxAfter = (size - (Math.abs(n.val) - idxBefore) - 1) % (size - 1);
                    if (idxAfter < 0) idxAfter += size - 1;
                } else {
                    idxAfter = idxBefore + n.val;
                }

                mixed.remove(n);
                mixed.add((int) idxAfter, n);
//                System.out.println(Arrays.toString(mixed.toArray()));
            }
        }
        System.out.println(Arrays.toString(mixed.toArray()));

        return computeGroveCoordinates(mixed);
    }

    private long computeGroveCoordinates(List<PlainNode> mixed) {
        int idxOfZero = mixed.indexOf(zeroPlainNode);
        long res = 0;
        int idx = (idxOfZero + 1000) % size;
        res += mixed.get(idx).val;
        idx = (idx + 1000) % size;
        res += mixed.get(idx).val;
        idx = (idx + 1000) % size;
        res += mixed.get(idx).val;
        return res;
    }

    private static class PlainNode {
        private final long val;

        public PlainNode(long val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return Long.toString(val);
        }
    }
}
