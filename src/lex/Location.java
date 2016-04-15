package lex;

public class Location {
	public final String file;
	public final int line, column;

	public Location(String file, int line, int column) {
		this.file = file;
		this.line = line;
		this.column = column;
	}

	@Override
	public String toString() {
		return file + ", line " + line + ", column " + column;
	}

}
