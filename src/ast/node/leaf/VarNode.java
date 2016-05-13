package ast.node.leaf;

import java.io.PrintWriter;

import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import misc.Type;
import ast.node.AbstractNode;
import ast.node.LValue;
import ast.node.RValue;
import ast.node.Values;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.LoadGVar;
import code.act.SetGVar;
import code.act.SetLVar;
import exception.Log;
import exception.ParseException;
import exception.SemanticException;

public class VarNode extends AbstractNode implements LValue, RValue {
    public final VarToken token;

    public VarNode(VarToken token) {
        this.token = token;
    }

    @Override
    public void setVariable(Variable src, VisibilityZone z, Environment e, Log log) throws ParseException {
        if (token.pac == null) {
            Variable dst = e.lv.get(token.toTokenString());
            if (dst == null) {
                log.addException(new SemanticException("Cant find declaration", token));
                return;
            }

            if (Values.cmp(dst.type, src.type, log, token)) {
                z.addAction(new SetLVar(dst, src, token.toString()));
            }

        } else {
            DeclarationToken declarationToken = e.gv.get(token.toTokenString());

            if (declarationToken == null) {
                log.addException(new SemanticException("Cant find declaration", token));
                return;
            }

            if (Values.cmp(declarationToken, src.type, log)) {
                z.addAction(new SetGVar(token.toTokenString(), src, null, declarationToken.toString()));
            }
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
        if (token.pac == null) {
            Variable src = e.lv.get(token.toTokenString());
            if (src == null) {
                log.addException(new SemanticException("Cant find declaration", token));
                return;
            }

            if (Values.cmp(dst.type, src.type, log, token)) {
                z.addAction(new SetLVar(dst, src, token.toString()));
            }

        } else {
            DeclarationToken declarationToken = e.gv.get(token.toTokenString());

            if (declarationToken == null) {
                log.addException(new SemanticException("Cant find declaration", token));
                return;
            }

            if (Values.cmp(dst.type, declarationToken, log)) {
                z.addAction(new LoadGVar(dst, token.toTokenString(), declarationToken.toString()));
            }
        }
    }

    @Override
    public Type type(Environment e) {
        if (token.pac == null) {
            Variable variable = e.lv.get(token.toTokenString());
            if (variable != null) {
                return variable.type;
            }
        } else {
            DeclarationToken declarationToken = e.gv.get(token.toTokenString());
            if (declarationToken != null) {
                return declarationToken.typeToken.type;
            }
        }
        return null;
    }

}
