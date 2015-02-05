package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTableProvider;

public class Put {
    void putFunction(String[] cmd, boolean fromCmdLine) {
        if (Interpreter.tableIsChosen) {
            String putParameter = new String();
            String key = cmd[1];
            for (int ind = 2; ind < cmd.length; ++ind) {
                putParameter += cmd[ind] + " ";
            }
            putParameter = putParameter.substring(0, putParameter.length() - 1);
            try {
                ObjectStoreable value = (ObjectStoreable) new ObjectTableProvider().
                        deserialize(Interpreter.currentTableObject, putParameter);
                ObjectStoreable temp = (ObjectStoreable) Interpreter.currentTableObject.put(key, value);
                if (temp == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            Interpreter.informToChooseTable();
        }
    }
}
