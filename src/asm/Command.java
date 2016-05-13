package asm;

import java.io.PrintWriter;

import lex.Token;

public abstract class Command extends AsmLine {

    public Command(String label, String comment) {
        super(label, comment);
    }

    public abstract void execute(State state);

}
