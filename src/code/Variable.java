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

    @Override
    public String toString() {
        return "<" + visibilityZone.label() + " " + index + " " + type + ">";
    }

}
