package ch.lucas.y2022d13;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class TreeNode implements Comparable<TreeNode> permits ListTreeNode, IntTreeNode  {

    public static TreeNode parse(String input) {
        if (input.startsWith("[")) {
            return ListTreeNode.parse(input.substring(1, input.length()-1));
        } else if (input.isEmpty()) {
            return null;
        } else {
            return IntTreeNode.parse(input);
        }
    }


    @Override
    public int compareTo(TreeNode right) {
        if (this instanceof ListTreeNode l1 && right instanceof ListTreeNode l2) {
            return l1.listCompareTo(l2);
        }
        if (this instanceof IntTreeNode i1 && right instanceof IntTreeNode i2) {
            return i1.intCompareTo(i2);
        }
        if (this instanceof ListTreeNode l1 && right instanceof IntTreeNode i2) {
            return l1.listCompareTo(i2.convertToList());
        }
        if (this instanceof IntTreeNode i1 && right instanceof ListTreeNode l2) {
            return i1.convertToList().listCompareTo(l2);
        }
        throw new IllegalArgumentException();
    }

}
