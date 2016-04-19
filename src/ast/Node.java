package ast;

import java.io.PrintWriter;

public abstract class Node {

	public abstract void printTree(PrintWriter out, int indent);

	public void printIndent(PrintWriter out, int indent) {
		while (--indent >= 0) {
			out.print("    ");
		}
	}
}
