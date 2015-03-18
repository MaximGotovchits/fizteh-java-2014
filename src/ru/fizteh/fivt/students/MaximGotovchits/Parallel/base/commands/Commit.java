package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

public class Commit extends Command {
    public Commit() {}
    @Override
    public boolean execute(String[] cmd) {
        if (cmd.length == 1) {
            CommandTools.currentTable.commit();
            return true;
        }
        return false;
    }

    @Override
    public String getCmdName() {
        return "commit";
    }
}
