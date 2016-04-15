package ast;

import java.util.Arrays;
import java.util.List;

import lex.BadToken;

public class AST {
	public static BadNode build(List<BadToken> list) {
		int l = 0, r = list.size() - 1;
		int[] cb = new int[r + 1];
		Arrays.fill(cb, -1);

		int[][] rightIndex = new int[4][r + 1];

		for (int i = r; i >= 0; i--) {
			if (i < r) {
				for (int p = 0; p < rightIndex.length; p++) {
					rightIndex[p][i] = rightIndex[p][i + 1];
				}
			}
			BadToken token = list.get(i);
			if (token.type == "{") {
				rightIndex[0][i] = i;
			}
			if (token.type == ";") {
				rightIndex[1][i] = i;
			}

			if (token.type == "(") {
				rightIndex[2][i] = i;
			}
			if (token.type == ",") {
				rightIndex[3][i] = i;
			}
		}

		int[] type = new int[r + 1];
		int sp = 0;

		for (int i = 0; i <= r; i++) {
			BadToken token = list.get(i);
			if (token.type == "{") {
				type[sp++] = +i;
			}
			if (token.type == "(") {
				type[sp++] = ~i;
			}

			if (token.type == "}") {
				if (sp == 0) {
					throw new RuntimeException("Negative balance of brackets at " + token);
				}
				if (type[--sp] < 0) {
					throw new RuntimeException("Expected ')' at " + token);
				}
				cb[i] = +type[sp];
			}
			if (token.type == ")") {
				if (sp == 0) {
					throw new RuntimeException("Negative balance of brackets at " + token);
				}
				if (type[--sp] >= 0) {
					throw new RuntimeException("Expected '}' at " + token);
				}
				cb[i] = ~type[sp];
			}
		}

		return build(l, r, list, cb);
	}

	public static BadNode build(int l, int r, List<BadToken> list, int[] cb) {

		return null;
	}

}
