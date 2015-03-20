package ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter;

public abstract class Command {
    public abstract String getCmdName();

    public abstract boolean execute(String[] cmd);
}
