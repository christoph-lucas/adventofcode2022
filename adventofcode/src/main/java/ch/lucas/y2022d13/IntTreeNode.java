package ch.lucas.y2022d13;

import java.util.List;

public final class IntTreeNode extends TreeNode{

    private int val;

    public IntTreeNode(int val) {
        this.val = val;
    }

    public static IntTreeNode parse(String input) {
        return new IntTreeNode(Integer.valueOf(input));
    }

    public int intCompareTo(IntTreeNode right) {
        return Integer.compare(val, right.val);
    }

    public ListTreeNode convertToList() {
        return new ListTreeNode(List.of(this));
    }

    @Override
    public String toString() {
        return Integer.toString(val);
    }
}
