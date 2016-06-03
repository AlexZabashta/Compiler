package misc;

import java.util.Arrays;

public class MaxMatching {

    public static int[] match(int n, int m, boolean[][] edge) {
        int[] matching = new int[m];

        Arrays.fill(matching, -1);
        for (int u = 0; u < n; u++) {
            findPath(m, edge, u, matching, new boolean[n]);
        }

        return matching;
    }

    static boolean findPath(int m, boolean[][] edge, int u, int[] matching, boolean[] color) {
        if (u == -1) {
            return true;
        }
        if (color[u]) {
            return false;
        }
        color[u] = true;

        for (int v = 0; v < m; v++) {
            if (edge[u][v] && findPath(m, edge, matching[v], matching, color)) {
                matching[v] = u;
                return true;
            }
        }
        return false;
    }

}
