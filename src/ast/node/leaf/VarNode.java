package ast.node.leaf;

import java.io.PrintWriter;
import java.util.List;

import lex.token.fold.DeclarationToken;
import lex.token.fold.VarToken;
import misc.Type;
import ast.node.AbstractNode;
import ast.node.LRValue;
import ast.node.Values;
import code.Environment;
import code.Variable;
import code.VisibilityZone;
import code.act.LoadGVar;
import code.act.SetGVar;
import code.act.SetLVar;

public class VarNode extends AbstractNode implements LRValue {
    public final VarToken token;

    public VarNode(VarToken token) {
        this.token = token;
    }

    @Override
    public void lValue(Variable src, VisibilityZone z, Environment e, List<String> errors) {
        if (token.pac == null) {
            Variable dst = e.lv.get(token.toTokenString());
            if (dst == null) {
                errors.add("Cant find declaration of " + token.toTokenString());
                return;
            }

            if (Values.cmp(dst.type, src.type, errors, token)) {
                z.addAction(new SetLVar(dst, src, token));
            }

        } else {
            DeclarationToken declarationToken = e.gv.get(token.toTokenString());

            if (declarationToken == null) {
                errors.add("Cant find declaration of " + token);
                return;
            }

            if (Values.cmp(declarationToken, src.type, errors)) {
                z.addAction(new SetGVar(token.toTokenString(), src, null, declarationToken));
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
    public void rValue(Variable dst, VisibilityZone z, Environment e, List<String> errors) {
        if (token.pac == null) {
            Variable src = e.lv.get(token.toTokenString());
            if (src == null) {
                errors.add("Cant find declaration of " + token.toTokenString());
                return;
            }

            if (Values.cmp(dst.type, src.type, errors, token)) {
                z.addAction(new SetLVar(dst, src, token));
            }

        } else {
            DeclarationToken declarationToken = e.gv.get(token.toTokenString());

            if (declarationToken == null) {
                errors.add("Cant find declaration of " + token);
                return;
            }

            if (Values.cmp(dst.type, declarationToken, errors)) {
                z.addAction(new LoadGVar(dst, token.toTokenString(), declarationToken));
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
        return new Type();
    }
}
