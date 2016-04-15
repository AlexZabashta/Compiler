package code.act;

import java.io.PrintWriter;

import lex.Location;

public class SetVar extends Action {

	String dst, src;
	int type;

	public SetVar(String lable, Location location, String dst, String src) {
		super(lable, location);

		this.dst = dst;
		this.src = src;

	}

	@Override
	public void print(int tab, PrintWriter out) {
		for (int t = 0; t < tab; t++) {
			out.print("    ");
		}
		// out.println(x);
	}

}
