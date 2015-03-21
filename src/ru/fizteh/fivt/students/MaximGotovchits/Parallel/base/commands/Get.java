package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;

public class Get extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (CommandTools.amountOfArgumentsIs(2, cmd)) {
            try {
                if (CommandTools.tableIsChosen) {
                    ObjectStoreable temp = (ObjectStoreable) CommandTools.currentTable.get(cmd[1]);
                    if (temp == null) {
                        System.err.println("not found");
                    } else {
                        System.out.println("found");
                        System.out.println(temp.serialisedValue);
                    }
                } else {
                    CommandTools.informToChooseTable();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return true;
        }
        return false;
    }
    @Override
    public String getCmdName() {
        return "get";
    }
}
