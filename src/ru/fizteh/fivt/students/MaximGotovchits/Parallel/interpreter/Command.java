package ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands.*;

public abstract class Command {
    public abstract String getCmdName();

    public abstract boolean execute(String[] cmd) throws Exception;
}
