package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

public class Exit extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (CommandTools.currentTableProvider.getUsingTable() != null && exitAndUseAvailable()) {
            CommandTools.currentTableProvider.fillTable();
        } else {
            return false;
        }
        System.exit(0);
        return true;
    }

    public boolean exitAndUseAvailable() {
        if (CommandTools.currentTableProvider.getUsingTable() != null) {
            int uncommitedChanges = CommandTools.currentTableProvider.getChangesNumber();
            if (uncommitedChanges == 0 || !CommandTools.tableIsChosen) {
                return true;
            }
            System.out.println(uncommitedChanges + " uncommited changes");
            return false;
        }
        return true;
    }

    @Override
    public String getCmdName() {
        return "exit";
    }
}


