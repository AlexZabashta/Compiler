package tmpast;

import java.io.PrintWriter;

public abstract class Node {

	public abstract void print(PrintWriter out, int indent);

	public void print(PrintWriter out) {		
		print(out, 0);
	}

	public void printIndent(PrintWriter out, int indent) {
		while (--indent >= 0) {
			out.print("    ");
		}
	}
}
