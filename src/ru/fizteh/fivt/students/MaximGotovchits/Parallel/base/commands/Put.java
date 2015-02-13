package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

public class Put extends Command {
    public boolean execute(String[] cmd) {
        if (cmd.length > 3) {
            if (CommadTools.tableIsChosen) {
                String putParameter = new String();
                String key = cmd[1];
                for (int ind = 2; ind < cmd.length; ++ind) {
                    putParameter += cmd[ind] + " ";
                }
                putParameter = putParameter.substring(0, putParameter.length() - 1);
                try {
                    ObjectStoreable value = (ObjectStoreable) new ObjectTableProvider().
                            deserialize(CommadTools.currentTable, putParameter);
                    ObjectStoreable temp = (ObjectStoreable) CommadTools.currentTable.put(key, value);
                    if (temp == null) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite");
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            } else {
                CommadTools.informToChooseTable();
            }
            return true;
        }
        return false;
    }

    @Override
    public String getCmdName() {
        return "put";
    }
}
