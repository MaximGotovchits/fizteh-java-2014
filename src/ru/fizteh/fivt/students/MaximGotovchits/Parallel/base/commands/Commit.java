package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

public class Commit extends CommandWithCheckedNumArgs {
    @Override
    void executeWithCompleteArgs(String[] cmd) {
        CommandTools.getUsingTable().commit();
    }

    @Override
    public String getCmdName() {
        return "commit";
    }
}
