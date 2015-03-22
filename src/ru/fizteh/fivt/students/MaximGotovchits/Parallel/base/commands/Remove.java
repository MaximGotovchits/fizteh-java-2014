package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;

public class Remove extends Command {
    public boolean execute(String[] cmd) {
        if (CommandTools.amountOfArgumentsIs(2, cmd)) {
            if (CommandTools.tableIsChosen) {
                try {
                    ObjectStoreable temp = (ObjectStoreable) CommandTools.currentTableProvider.getCurrentTableObject()
                            .remove(cmd[1]);
                    if (temp == null) {
                        System.out.println("not found");
                    } else {
                        System.out.println("removed");
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            } else {
                CommandTools.informToChooseTable();
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
