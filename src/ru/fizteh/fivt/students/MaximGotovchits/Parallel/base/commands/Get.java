package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;

public class Get extends CommandWithCheckedNumArgs {
    @Override
    void executeWithCompleteArgs(String[] cmd) {
        try {
            if (CommandTools.tableIsChosen) {
                ObjectStoreable temp = (ObjectStoreable) CommandTools.getUsingTable()
                        .get(cmd[1]);
                if (temp == null) {
                    System.err.println("not found");
                } else {
                    System.out.println("found");
                    System.out.println(temp.getSerialisedValue());
                }
            } else {
                CommandTools.informToChooseTable();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public String getCmdName() {
        return "get";
    }
}
