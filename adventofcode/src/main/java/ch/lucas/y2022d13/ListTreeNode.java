package ch.lucas.y2022d13;

import com.sun.source.tree.Tree;

import java.util.ArrayList;
import java.util.List;

public final class ListTreeNode extends TreeNode {

    private List<TreeNode> nodes;

    public ListTreeNode(List<TreeNode> nodes) {
        this.nodes = nodes;
    }

    public static ListTreeNode parse(String input) {
        int curElemStartIndex = 0;
        int curNumberOfOpenBrackets = 0;
        List<TreeNode> elems = new ArrayList<>(); //Arrays.stream(.split(",")).map(this::parse).filter(Objects::nonNull).toList();
        for (int i = 0; i< input.length(); i++) {
            if (input.charAt(i) == '[') {
                curNumberOfOpenBrackets++;
            } else if (input.charAt(i) == ']') {
                curNumberOfOpenBrackets--;
            } else if (input.charAt(i) == ',' && curNumberOfOpenBrackets == 0) {
                elems.add(parse(input.substring(curElemStartIndex, i)));
                curElemStartIndex = i+1; // jump over ','
            }
        }
        if (curElemStartIndex < input.length()) elems.add(TreeNode.parse(input.substring(curElemStartIndex)));
        return new ListTreeNode(elems);
    }

    public int listCompareTo(ListTreeNode right) {
        int i = 0;
        while (i<nodes.size()) {
            if (i >= right.nodes.size()) return 1; // right runs out of items before left
            int comp = nodes.get(i).compareTo(right.nodes.get(i));
            if (comp != 0) return comp;
            i++;
        }
        if (i < right.nodes.size()) return -1; // left runs out of items before right
        return 0; // same length, no difference so far
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("[");
        for (TreeNode n : nodes) {
            res.append(n.toString());
        }
        res.append("]");
        return res.toString();
    }
}
