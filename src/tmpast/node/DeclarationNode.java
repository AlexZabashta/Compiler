package tmpast.node;

import java.io.PrintWriter;

public class DeclarationNode extends Node {

    public final TypeNode typeNode;
    public final VarNode varNode;

    public DeclarationNode(TypeNode typeNode, VarNode varNode) {
        this.typeNode = typeNode;
        this.varNode = varNode;
    }

    @Override
    public void print(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println("declaration");
        typeNode.print(out, indent + 1);
        varNode.print(out, indent + 1);
    }

}
