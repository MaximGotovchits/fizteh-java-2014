package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

public class Drop extends CommandWithCheckedNumArgs {
    @Override
    void executeWithCompleteArgs(String[] cmd) {
        try {
            new ObjectTableProvider().removeTable(cmd[1]);
            System.out.println("dropped");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    public String getCmdName() {
        return "drop";
    }
}
