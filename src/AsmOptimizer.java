import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import asm.Command;
import asm.com.Nop;

public class AsmOptimizer {

    public static Map<String, List<Command>> removeDeadCode(String start, Map<String, List<Command>> map) {
        Map<String, List<Command>> clearCode = new HashMap<String, List<Command>>();

        Queue<String> targets = new ArrayDeque<>();
        targets.add(start);

        while (!targets.isEmpty()) {
            String target = targets.poll();
            if (clearCode.containsKey(target)) {
                continue;
            }

            List<Command> commands = map.get(target);
            if (commands == null) {
                continue;
            }

            clearCode.put(target, commands);

            for (Command command : commands) {
                if (command.target != null) {
                    targets.add(command.target);
                }
            }
        }

        return clearCode;
    }

    public static void removeNop(List<Command> commands) {
        List<Command> clearCode = new ArrayList<Command>();

        List<String> labels = new ArrayList<>();
        List<String> comments = new ArrayList<>();

        Map<String, String> map = new HashMap<String, String>();

        for (Command command : commands) {
            command = command.optimize();

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
