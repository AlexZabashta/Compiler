package code;

import misc.EnumType;
import misc.Type;

public class Variable {
    public final Type type;
    public final VisibilityZone visibilityZone;
    public final int index;

    public Variable(Type type, VisibilityZone visibilityZone, int index) {
        this.type = type;
        this.visibilityZone = visibilityZone;
        this.index = index;
    }

    public int distance(VisibilityZone from) {
        VisibilityZone to = visibilityZone, cur = from;
        int distance = 0;

        while (cur != null) {
            if (to == cur) {
                if (cur.parent == null) {
                    distance += 1;
                }
                distance += cur.numberOfVars() - index - 1;
                break;
            }
            distance += cur.numberOfVars();
            cur = cur.parent;
        }

        if (cur == null) {
            throw new RuntimeException("Can't find " + this + " from " + from);
        }

        return distance;
    }

    @Override
    public String toString() {
        return "<" + visibilityZone.label() + " " + index + " " + type + ">";
    }

}
