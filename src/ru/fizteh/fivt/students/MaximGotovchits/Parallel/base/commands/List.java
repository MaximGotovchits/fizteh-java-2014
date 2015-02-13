package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

public class List extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (cmd.length == 1) {
            if (CommadTools.tableIsChosen) {
                CommadTools.currentTable.list();
            } else {
                CommadTools.informToChooseTable();
            }
            return true;
        }
        return false;
    }

    @Override
    public String getCmdName() {
        return "list";
    }
}
