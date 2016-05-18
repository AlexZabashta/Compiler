package code.var;

import exception.UnexpectedVoidType;
import lex.token.fold.DeclarationToken;
import misc.Type;
import asm.mem.RWMemory;
import asm.mem.RamLabel;

public class GlobalVariable extends Variable {
    public final String location;

    public GlobalVariable(Type type, String location) throws UnexpectedVoidType {
        super(type);
        this.location = location;
    }

    public GlobalVariable(DeclarationToken token) throws UnexpectedVoidType {
        super(token.typeToken.type);
        this.location = token.varToken.toTokenString();
    }

    @Override
    public RWMemory rwMemory() {
        return new RamLabel(location);
    }

}
