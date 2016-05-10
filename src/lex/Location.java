package lex;

import java.util.Objects;

public class Location implements Comparable<Location> {
    public final String file;
    public final int line, column;

    public Location(String file, int line, int column) {
        this.file = Objects.requireNonNull(file);
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return file + ", line " + line + ", column " + column;
    }

    @Override
    public int compareTo(Location location) {

        int cmp;

        if ((cmp = file.compareTo(location.file)) != 0) {
            return cmp;
        }

        if ((cmp = Integer.compare(line, location.line)) != 0) {
            return cmp;
        }

        if ((cmp = Integer.compare(column, location.column)) != 0) {
            return cmp;
        }

        return 0;
    }

}
