package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectStoreable;

public class Remove {
    void removeFunction(String[] cmd) {
        if (Interpreter.tableIsChosen) {
            try {
                ObjectStoreable temp = (ObjectStoreable) Interpreter.currentTableObject.remove(cmd[1]);
                if (temp == null) {
                    System.out.println("not found");
                } else {
                    System.out.println("removed");
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            Interpreter.informToChooseTable();
        }
    }
}
