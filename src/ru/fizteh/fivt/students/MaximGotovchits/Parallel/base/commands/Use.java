package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import java.io.*;
import java.text.ParseException;

public class Use extends CommandWithCheckedNumArgs {
    @Override
    public String getCmdName() {
        return "use";
    }

    @Override
    void executeWithCompleteArgs(String[] cmd) {
        try {
            useFunction(cmd[1], CommandTools.currentTableProvider.getUsingTableName());
        } catch (IOException e) {
            System.err.println(e);
        } catch (ParseException e) {
            System.err.println(e);
        }
    }

    public boolean useFunction(String tableName, String oldTableName) throws IOException, ParseException {
        return CommandTools.currentTableProvider.changeUsingTable(tableName, oldTableName);
    }
}
