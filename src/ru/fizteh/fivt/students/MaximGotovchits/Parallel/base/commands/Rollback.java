package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

public class Rollback extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (CommandTools.amountOfArgumentsIs(1, cmd)) {
            CommandTools.currentTableProvider.getUsingTable().rollback();
            return true;
        }
        return false;
    }

    @Override
    public String getCmdName() {
        return "rollback";
    }
}
