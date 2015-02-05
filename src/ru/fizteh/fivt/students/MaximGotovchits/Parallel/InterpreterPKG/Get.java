package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectStoreable;

public class Get {
    void getFunction(String[] cmd, boolean fromCmdLine) {
        try {
            if (Interpreter.tableIsChosen) {
                ObjectStoreable temp = (ObjectStoreable) Interpreter.currentTableObject.get(cmd[1]);
                if (temp == null) {
                    System.err.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(temp.serialisedValue);
                }
            } else {
                Interpreter.informToChooseTable();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
