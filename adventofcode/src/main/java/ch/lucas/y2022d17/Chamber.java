package ch.lucas.y2022d17;

public class Chamber {

    private static final int X_MAX = 7;

    private final byte[] chamber;
    public int currentHeight;

    public int nRocks = 0;

    public Chamber(int size) {
        chamber = new byte[size];
    }

    public byte[] top() {
        if (currentHeight < 2) return new byte[]{0, 0};
        return new byte[]{chamber[currentHeight - 1], chamber[currentHeight - 2]};
    }

    public boolean fitsAt(int left, int bottom, Rock r) {
        for (int y = 0; y < r.shape.length; y++) {
            for (int x = 0; x < r.shape[0].length; x++) {
                if (r.shape[y][x] == '#') {
                    int yprime = bottom + r.shape.length - 1 - y;
                    int xprime = left + x;
                    if (yprime < 0 || yprime >= chamber.length
                            || xprime < 0 || xprime >= X_MAX
                            || !free(yprime, xprime)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void plotAt(int left, int bottom, Rock r) {
        for (int y = 0; y < r.shape.length; y++) {
            for (int x = 0; x < r.shape[0].length; x++) {
                if (r.shape[y][x] == '#') {
                    int yprime = bottom + r.shape.length - 1 - y;
                    int xprime = left + x;
                    put(yprime, xprime);
                }
            }
        }
        nRocks++;
        int highestPointOfRock = bottom + r.shape.length - 1;
        currentHeight = Math.max(currentHeight, highestPointOfRock + 1);
    }
    
    public void prettyPrint() {
        int top = currentHeight + 8;
        int padding = Integer.toString(top).length() + 1;

        for (int r = currentHeight + 8; r >= 0; r--) {
            StringBuilder res = new StringBuilder(String.format("%" + padding + "d", r) + " |");
            for (int c = 0; c < X_MAX; c++) {
                res.append(free(r, c) ? '.' : '#');
            }
            res.append("|     ");
            res.append(chamber[r]);
            System.out.println(res);
        }
        System.out.println(String.format("%" + padding + "s", "") + " ---------");
        System.out.println();
    }

    private boolean free(int y, int x) {
        return (chamber[y] & (1 << x)) == 0;
    }

    private void put(int y, int x) {
        chamber[y] |= (1 << x);
    }

}
