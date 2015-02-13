package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.*;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Interpreter;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;
import java.util.HashSet;
import java.util.Set;

public class Main { // Using JSON format.
    public static void main(final String[] args) throws Exception {
        new MakeDirs().execute(null);
        Set<Command> commandSet = new HashSet<>();
        ObjectTable currentTableObject = null;
        boolean fromCmdLine;
        if (args.length == 0) {
            fromCmdLine = false;
            fillCommandSet(commandSet, currentTableObject, fromCmdLine);
            Interpreter interpreter = new Interpreter(commandSet);
            interpreter.startUp(null, false);
        } else {
            fromCmdLine = true;
            fillCommandSet(commandSet, currentTableObject, fromCmdLine);
            Interpreter interpreter = new Interpreter(commandSet);
            String cmd = new String();
            for (String arg : args) {
                cmd = cmd.join(" ", cmd, arg);
            }
            cmd = cmd.replaceAll("\\s+", " ");
            System.out.println(cmd);
            interpreter.startUp(cmd, fromCmdLine);
            new Exit().execute(null);
        }
    }

    private static void fillCommandSet(Set<Command> commandSet, ObjectTable currentTableObject, boolean fromCmdLine) {
        commandSet.add(new Commit());
        commandSet.add(new Create());
        commandSet.add(new Drop());
        commandSet.add(new Exit());
        commandSet.add(new FillTable());
        commandSet.add(new Get());
        commandSet.add(new List());
        commandSet.add(new MakeDirs());
        commandSet.add(new Put());
        commandSet.add(new Remove());
        commandSet.add(new Rollback());
        commandSet.add(new ShowTables());
        commandSet.add(new Use());
    }
}
