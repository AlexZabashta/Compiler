package ast.node.leaf;

import java.io.PrintWriter;

import lex.token.fold.VarToken;
import misc.Type;
import ast.node.AbstractNode;
import ast.node.LValue;
import ast.node.RValue;
import code.Environment;
import code.VisibilityZone;
import code.act.MoveVar;
import code.var.GlobalVariable;
import code.var.LocalVariable;
import code.var.Variable;
import exception.DeclarationException;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;
import exception.TypeMismatch;

public class VarNode extends AbstractNode implements LValue, RValue {
    public final VarToken token;

    public VarNode(VarToken token) {
        this.token = token;
    }

    @Override
    public void setVariable(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            if (token.pac == null) {
                LocalVariable dst = e.localVar(token.toTokenString());
                z.addAction(new MoveVar(dst, src, token.toString()));
            } else {
                GlobalVariable dst = e.globalVar(token.toTokenString());
                z.addAction(new MoveVar(dst, src, token.toString()));
            }
        } catch (TypeMismatch | DeclarationException exception) {
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

    @Override
    public void getVariable(Variable dst, VisibilityZone z, Environment e, Log log) throws ParseException {
        try {
            if (token.pac == null) {
                LocalVariable src = e.localVar(token.toTokenString());
                z.addAction(new MoveVar(dst, src, token.toString()));
            } else {
                GlobalVariable src = e.globalVar(token.toTokenString());
                z.addAction(new MoveVar(dst, src, token.toString()));
            }
        } catch (TypeMismatch | DeclarationException exception) {
            log.addException(new SemanticException(exception.getMessage(), token));
        }
    }

    @Override
    public Type type(Environment e) throws DeclarationException {
        if (token.pac == null) {
            return e.localVar(token.toTokenString()).type;
        } else {
            return e.globalVar(token.toTokenString()).type;
        }
    }

}
