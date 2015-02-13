package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

public class ShowTables extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (cmd.length == 2) {
            new ObjectTableProvider().getTableNames();
            return true;
        }
        return false;
    }

    @Override
    public String getCmdName() {
        return "show";
    }
}
