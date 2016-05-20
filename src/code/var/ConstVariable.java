package code.var;

import exception.UnexpectedVoidType;
import lex.Token;
import lex.token.ConstValueToken;
import misc.Label;
import misc.Type;
import asm.mem.ConstInt;
import asm.mem.Memory;
import asm.mem.RWMemory;
import asm.mem.RamLabel;

public class ConstVariable extends GlobalVariable {

    public String bigData;
    public int smallData;

    public static final RamLabel NULL = new RamLabel("emptyarray");
    public static final ConstInt TRUE = new ConstInt(-1);
    public static final ConstInt FALSE = new ConstInt(0);

    public ConstVariable(Type type, String location, int data) throws UnexpectedVoidType {
        super(type, location);
        this.bigData = null;
        this.smallData = data;
    }

    public ConstVariable(Type type, String location, String bigData) throws UnexpectedVoidType {
        super(type, location);
        this.bigData = bigData;
        this.smallData = 0;
    }

    public ConstVariable(ConstValueToken token) throws UnexpectedVoidType {
        super(token.type(), Label.getDataLabel());

        this.bigData = token.stringValue();
        this.smallData = token.intValue();
    }

    @Override
    public Memory memory() {
        if (bigData == null) {
            return new ConstInt(smallData);
        } else {
            return new RamLabel(location);
        }
    }

    @Override
    public RWMemory rwMemory() {
        throw new RuntimeException("Can't modify cost variable");
    }

}
