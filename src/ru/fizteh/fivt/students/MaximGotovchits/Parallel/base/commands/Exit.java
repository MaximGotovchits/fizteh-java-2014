package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

public class Exit extends Command {
    @Override
    public boolean execute(String[] cmd) throws Exception {
        if (CommadTools.currentTable != null) {
            new FillTable().execute(null);
        }
        System.exit(0);
        return true;
    }

    public boolean exitAndUseAvailable() {
        if (CommadTools.currentTable != null) {
            int uncommitedChanges = CommadTools.currentTable.storage.get().size()
                    - CommadTools.currentTable.commitStorage.size();
            if (uncommitedChanges == 0 || !CommadTools.tableIsChosen) {
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


