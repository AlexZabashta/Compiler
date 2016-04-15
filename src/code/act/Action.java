package code.act;

import java.io.PrintWriter;

import lex.Location;

public abstract class Action {

	public static int lableName = 0;

	public String lable;
	public Location location;

	public Action(String lable, Location location) {
		this.lable = lable;
		this.location = location;
	}

	public abstract void print(int tab, PrintWriter out);

}
