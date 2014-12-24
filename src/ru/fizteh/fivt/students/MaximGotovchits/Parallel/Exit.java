package ru.fizteh.fivt.students.MaximGotovchits.Parallel;

public class Exit extends CommandsTools {
    void exitFunction() throws Exception {
        if (currentTableObject != null) {
            new FillTable().fillTableFunction(currentTableObject);
        }
        System.exit(0);
    }
    boolean exitAndUseAvailable() {
        if (currentTableObject != null) {
            int uncommitedChanges = currentTableObject.storage.get().size()
                    - currentTableObject.commitStorage.size();
            if (uncommitedChanges == 0 || !tableIsChosen) {
                return true;
            }
            System.out.println(uncommitedChanges + " uncommited changes");
            return false;
        }
        return true;
    }
}


