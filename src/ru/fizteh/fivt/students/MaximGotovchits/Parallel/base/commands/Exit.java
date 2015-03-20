package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

public class Exit extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (CommandTools.currentTable != null && exitAndUseAvailable()) {
            new FillTable().execute(null);
        } else {
            return false;
        }
        System.exit(0);
        return true;
    }

    public boolean exitAndUseAvailable() {
        if (CommandTools.currentTable != null) {
            int uncommitedChanges = CommandTools.currentTable.storage.get().size()
                    - CommandTools.currentTable.commitStorage.size();
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


