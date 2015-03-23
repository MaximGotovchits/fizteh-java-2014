package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

public abstract class CommandWithCheckedNumArgs extends Command {
    @Override
    public boolean execute(String[] cmd, int args) {
        if (CommandTools.amountOfArgumentsIs(args, cmd)) {
            executeWithCompleteArgs(cmd);
            return true;
        } else {
            return false;
        }
    }
    abstract void executeWithCompleteArgs(String[] cmd);
}

