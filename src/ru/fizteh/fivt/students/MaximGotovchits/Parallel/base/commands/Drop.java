package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

public class Drop extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (cmd.length == 2) {
            try {
                new ObjectTableProvider().removeTable(cmd[1]);
                System.out.println("dropped");
            } catch (Exception e) {
                System.err.println(e);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getCmdName() {
        return "drop";
    }
}
