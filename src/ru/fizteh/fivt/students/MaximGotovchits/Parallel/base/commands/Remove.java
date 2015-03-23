package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;

public class Remove extends CommandWithCheckedNumArgs {
    @Override
    void executeWithCompleteArgs(String[] cmd) {
        if (CommandTools.tableIsChosen) {
            try {
                ObjectStoreable temp = (ObjectStoreable) CommandTools.getUsingTable().remove(cmd[1]);
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
    }

    @Override
    public String getCmdName() {
        return "remove";
    }
}
