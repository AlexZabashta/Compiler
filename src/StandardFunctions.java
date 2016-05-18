import java.util.ArrayList;
import java.util.List;

import misc.EnumType;
import misc.Type;
import ast.Function;
import ast.SystemFunction;

public class StandardFunctions {

    public final static int MAX_LEVEL = 5;

    public final static String PAC = "sys";

    public static List<Function> getFunctions() {
        List<Function> f = new ArrayList<Function>();

        f.add(new SystemFunction(new Type(), "println", "$char.1"));
        f.add(new SystemFunction(Type.string(), "readline"));
        f.add(new SystemFunction(Type.string(), "add", "$char.1$char.1"));
        f.add(new SystemFunction(new Type(EnumType.INT), "add", "$int$int"));
        f.add(new SystemFunction(new Type(EnumType.BOOL), "less", "$int$int"));

        return f;
    }
}
