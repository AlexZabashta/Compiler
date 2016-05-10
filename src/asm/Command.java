package asm;

import java.io.PrintWriter;

import lex.Token;

public abstract class Command extends AsmLine {

    public Command(String label, Token token) {
        super(label, token);
    }

    public abstract void execute(State state);

}
