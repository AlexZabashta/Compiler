package asm;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public abstract class Command extends AsmLine {

    public Command(String label, String comment) {
        super(label, comment);
    }

    public abstract void execute(State state, Reader input, Writer output) throws IOException;

}
