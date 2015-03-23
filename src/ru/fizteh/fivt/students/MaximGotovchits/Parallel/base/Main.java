package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.*;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Interpreter;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Main { // Using JSON format.
    public static void main(final String[] args) throws Exception {
        boolean fromCmdLine;
        if (args.length == 0) {
            fromCmdLine = false;
            launchInterpreter(fromCmdLine, null);
        } else {
            fromCmdLine = true;
            String cmd = String.join(" ", args).replaceAll("\\s+", " ");
            launchInterpreter(fromCmdLine, cmd);
            new Exit().execute(null);
        }
    }

    private static void launchInterpreter(boolean fromCmdLine, String cmd) {
        Set<Command> commandSet = new HashSet<>();
        makeDirs();
        fillCommandSet(commandSet);
        Interpreter interpreter = new Interpreter(commandSet);
        interpreter.startUp(cmd, fromCmdLine);
    }

    private static void fillCommandSet(Set<Command> commandSet) {
        commandSet.add(new Commit());
        commandSet.add(new Create());
        commandSet.add(new Drop());
        commandSet.add(new Exit());
        commandSet.add(new Get());
        commandSet.add(new List());
        commandSet.add(new Put());
        commandSet.add(new Remove());
        commandSet.add(new Rollback());
        commandSet.add(new ShowTables());
        commandSet.add(new Use());
    }

    private static void makeDirs() {
        File file = new File(CommandTools.DATA_BASE_NAME);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            if (!file.isDirectory()) {
                System.err.println(CommandTools.DATA_BASE_NAME + " is not a directory");
                System.exit(1);
            } else {
                for (File sub : file.listFiles()) {
                    if (!sub.isDirectory() && !sub.isHidden()) {
                        System.err.println(CommandTools.DATA_BASE_NAME + File.separator
                                + sub.getName() + " is not a directory");
                    }
                }
            }
        }
    }
}
