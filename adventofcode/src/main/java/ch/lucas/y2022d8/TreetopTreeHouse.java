package ch.lucas.y2022d8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TreetopTreeHouse {

    private final int[][] trees;
    private final int[][] visible;
    private final int n_rows;
    private final int n_cols;
    private int visibleTrees = 0;

    public TreetopTreeHouse(List<String> values) {
        n_rows = values.size();
        n_cols = values.get(0).length();
        visible = new int[n_rows][n_cols];

        trees = new int[n_rows][n_cols];
        int r = 0;
        for (String row : values) {
            if (row.length() != n_cols) throw new IllegalArgumentException();
            for (int c = 0; c < n_cols; c++) {
                trees[r][c] = row.charAt(c) - '0';
            }
            r++;
        }
//        prettyPrint(trees);
    }

    public static void main(String[] args) {
        System.out.println("Treetop Tree House!");
        try {
            List<String> input = Files.readAllLines(Paths.get("src/main/resources/input_y22d8.txt"));
            TreetopTreeHouse tth = new TreetopTreeHouse(input);
            System.out.println("Visible trees: " + tth.visibleTrees()); // 1832
            System.out.println("Max scenic score: " + tth.maxScenicScore()); // 157320
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int visibleTrees() {
        markVisibleFromSide();
        markVisibleFromAboveOrBelow();
        prettyPrint(visible);
        return visibleTrees;
    }

    private void markVisibleFromSide() {
        for (int r = 0; r < n_rows; r++) {
            int maxLeft = -1;
            int maxRight = -1;
            int left = 0;
            int right = n_cols - 1;
            while (left <= right || maxLeft != maxRight) {
                maxLeft = checkMax(r, left++, maxLeft);
                maxRight = checkMax(r, right--, maxRight);
            }
        }
    }

    private void markVisibleFromAboveOrBelow() {
        for (int c = 0; c < n_cols; c++) {
            int maxTop = -1;
            int maxBottom = -1;
            int top = 0;
            int bottom = n_rows - 1;
            while (top <= bottom || maxTop != maxBottom) {
                maxTop = checkMax(top++, c, maxTop);
                maxBottom = checkMax(bottom--, c, maxBottom);
            }
        }
    }

    private int checkMax(int r, int c, int curMax) {
        if (trees[r][c] > curMax) {
            if (visible[r][c] == 0) visibleTrees++;
            visible[r][c] = 1;
            return trees[r][c];
        }
        return curMax;
    }

    // This is a straight forward O(n^3) solution. O(n^2) is possible but more cumbersome.
    public int maxScenicScore() {
        int max = -1;
        for (int r = 0; r < n_rows; r++) {
            for (int c = 0; c < n_cols; c++) {
                max = Math.max(max, scenicScore(r, c));
            }
        }
        return max;
    }

    private int scenicScore(int row, int col) {
        return dist(row, col, 1, 0)
                * dist(row, col, -1, 0)
                * dist(row, col, 0, 1)
                * dist(row, col, 0, -1);
    }

    private int dist(int start_row, int start_col, int delta_row, int delta_col) {
        int height = trees[start_row][start_col];
        int d = 0;
        int row = start_row;
        int col = start_col;
        while (inbounds(row + delta_row, col + delta_col)) {
            d++;
            row += delta_row;
            col += delta_col;
            if (trees[row][col] >= height) break;
        }
        return d;
    }

    private boolean inbounds(int r, int c) {
        return r >= 0 && r < n_rows && c >= 0 && c < n_cols;
    }

    private void prettyPrint(int[][] a) {
        for (int r = 0; r < a.length; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < a[r].length; c++) {
                line.append(a[r][c]);
            }
            System.out.println(line);
        }
    }
}
