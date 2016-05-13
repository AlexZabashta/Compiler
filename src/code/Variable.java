package code;

import java.util.List;

import asm.Command;
import asm.mem.RWMemory;
import asm.mem.RamEsp;
import misc.Type;

public class Variable {
    public final Type type;
    public final VisibilityZone visibilityZone;
    public final int index;

    public String location = "where you want"; // TODO

    public Variable(Type type, VisibilityZone visibilityZone, int index) {
        if (type.idVoid()) {
            throw new RuntimeException("Can't create void variable");
        }
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

    public void subscribe(List<Command> programText) {
        // TODO
    }

    public void unsubscribe(List<Command> programText) {
        // TODO
    }

    public RWMemory memory() {
        // TODO ADD distance
        return new RamEsp(index);
    }

    @Override
    public String toString() {
        return "<" + visibilityZone.label + " " + index + " " + type + ">";
    }

}
