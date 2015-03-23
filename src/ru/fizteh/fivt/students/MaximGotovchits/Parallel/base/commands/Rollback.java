package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

public class Rollback extends CommandWithCheckedNumArgs {
    @Override
    void executeWithCompleteArgs(String[] cmd) {
        CommandTools.getUsingTable().rollback();
    }
    @Override
    public String getCmdName() {
        return "rollback";
    }
}
