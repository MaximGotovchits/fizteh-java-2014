package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

public class Exit {
    public void exitFunction() throws Exception {
        if (Interpreter.currentTableObject != null) {
            new FillTable().fillTableFunction(Interpreter.currentTableObject);
        }
        System.exit(0);
    }
    public boolean exitAndUseAvailable() {
        if (Interpreter.currentTableObject != null) {
            int uncommitedChanges = Interpreter.currentTableObject.storage.get().size()
                    - Interpreter.currentTableObject.commitStorage.size();
            if (uncommitedChanges == 0 || !Interpreter.tableIsChosen) {
                return true;
            }
            System.out.println(uncommitedChanges + " uncommited changes");
            return false;
        }
        return true;
    }
}


