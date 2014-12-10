package ru.fizteh.fivt.students.MaximGotovchits.Storeable;

public class Exit extends CommandsTools {
    void exitFunction() {
        System.exit(0);
    }
    Boolean exitAndUseAvailable() {
        uncommitedChanges = Math.abs(storage.size() - commitStorage.size());
        if (uncommitedChanges == 0 || !tableIsChosen) {
            return true;
        }
        System.out.println(uncommitedChanges + " uncommited changes");
        return false;
    }
}

