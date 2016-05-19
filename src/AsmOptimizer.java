import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asm.Command;
import asm.com.Nop;

public class AsmOptimizer {
    public static void removeNop(List<Command> commands) {
        List<Command> clearCode = new ArrayList<Command>();

        List<String> labels = new ArrayList<>();
        List<String> comments = new ArrayList<>();

        Map<String, String> map = new HashMap<String, String>();

        for (Command command : commands) {
            if (command.label != null) {
                labels.add(command.label);
            }
            if (command.comment != null) {
                comments.add(command.comment);
            }

            if (!(command instanceof Nop)) {
                if (!labels.isEmpty()) {
                    command.label = labels.get(0);
                    for (String label : labels) {
                        map.put(label, command.label);
                    }
                    labels.clear();
                }

                if (!comments.isEmpty()) {
                    StringBuilder builder = new StringBuilder();

                    for (String comment : comments) {
                        builder.append(' ');
                        builder.append(comment);
                    }

                    command.comment = builder.toString();
                    comments.clear();
                }

                clearCode.add(command);
            }
        }

        commands.clear();

        for (Command command : clearCode) {
            String target = map.get(command.target);
            if (target != null) {
                command.target = target;
            }
            commands.add(command);
        }

    }
}
