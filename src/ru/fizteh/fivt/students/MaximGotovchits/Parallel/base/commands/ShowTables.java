package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;

public class ShowTables extends Command {
    @Override
    public boolean execute(String[] cmd) {
        if (CommandTools.amountOfArgumentsIs(2, cmd)) {
            java.util.List<String> tableNamesList = CommandTools.currentTableProvider.getAdvancedTableNames();
            for (String tmp : tableNamesList) {
                System.out.println(tmp);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getCmdName() {
        return "show";
    }
}
