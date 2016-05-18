package ast.node.leaf;

import java.io.PrintWriter;

import lex.token.fold.DeclarationToken;
import ast.node.AbstractNode;
import ast.node.LValue;
import ast.node.Values;
import code.Environment;
import code.VisibilityZone;
import code.act.MoveVar;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.TypeMismatch;
import exception.UnexpectedVoidType;

public class DeclarationNode extends AbstractNode implements LValue {
    public final DeclarationToken token;

    public DeclarationNode(DeclarationToken token) {
        this.token = token;
    }

    @Override
    public void action(VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            z.createVariable(token, e);
        } catch (UnexpectedVoidType | DeclarationException exception) {
            log.addException(new SemanticException(exception.getMessage(), token));
        }
    }

    @Override
    public void setVariable(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            Variable dst = z.createVariable(token, e);
            z.addAction(new MoveVar(dst, src, token.toString()));
        } catch (TypeMismatch | UnexpectedVoidType | DeclarationException exception) {
            log.addException(new SemanticException(exception.getMessage(), token));
        }
    }

    @Override
    public void print(PrintWriter out) {
        out.print(token.toTokenString());
    }

    @Override
    public void printTree(PrintWriter out, int indent) {
        printIndent(out, indent);
        out.println(token);
    }

}
