package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTableProvider;

public class Drop {
    void dropFunction(String[] cmd) {
        try {
            new ObjectTableProvider().removeTable(cmd[1]);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
