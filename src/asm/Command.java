package asm;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public abstract class Command extends AsmLine {

    public String target;

    public Command(String label, String target, String comment) {
        super(label, comment);
        this.target = target;
    }

    public abstract void execute(State state, Reader input, Writer output) throws IOException;

}
