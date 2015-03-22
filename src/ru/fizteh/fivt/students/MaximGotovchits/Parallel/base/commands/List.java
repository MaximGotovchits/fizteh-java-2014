package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

public class List extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (CommandTools.amountOfArgumentsIs(1, cmd)) {
            if (CommandTools.tableIsChosen) {
                java.util.List<String> list = CommandTools.currentTableProvider.getCurrentTableObject().list();
                int size = 0;
                for (Object iter : list) {
                    if (size < CommandTools.currentTableProvider.getCurrentTableObject().storage.get().size() - 1) {
                        System.out.print(iter + ", ");
                    } else {
                        System.out.print(iter);
                    }
                    ++size;
                }
                if (size != 0) {
                    System.out.println();
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
        return "list";
    }
}
