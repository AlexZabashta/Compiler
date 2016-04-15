package code;

import java.io.PrintWriter;
import java.util.List;

import code.act.Action;
import code.act.VisibilityZone;

public class Function {
	public final VisibilityZone root;
	public final FuncitonHeader header;

	public Function(FuncitonHeader header, VisibilityZone root) {
		this.header = header;
		this.root = root;

	}

	public void print(PrintWriter out) {
		header.print(out);

		for (Action action : root.actions) {
			action.print(1, out);
		}

		out.println();

	}
}
