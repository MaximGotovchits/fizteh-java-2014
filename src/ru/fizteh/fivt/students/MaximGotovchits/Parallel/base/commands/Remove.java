package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;

public class Remove extends Command {
    public boolean execute(String[] cmd) {
        if (cmd.length == 2) {
            if (CommadTools.tableIsChosen) {
                try {
                    ObjectStoreable temp = (ObjectStoreable) CommadTools.currentTable.remove(cmd[1]);
                    if (temp == null) {
                        System.out.println("not found");
                    } else {
                        System.out.println("removed");
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
        return "remove";
    }
}
