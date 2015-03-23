package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

public class ShowTables extends CommandWithCheckedNumArgs {
    @Override
    void executeWithCompleteArgs(String[] cmd) {
        java.util.List<String> tableNamesList = CommandTools.currentTableProvider.getAdvancedTableNames();
        for (String tmp : tableNamesList) {
            System.out.println(tmp);
        }
    }
    @Override
    public String getCmdName() {
        return "show";
    }
}
