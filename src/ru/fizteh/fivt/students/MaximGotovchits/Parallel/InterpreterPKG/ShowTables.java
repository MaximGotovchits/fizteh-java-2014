package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTableProvider;

public class ShowTables {
    void showTablesFunction() {
        new ObjectTableProvider().getTableNames();
    }
}
