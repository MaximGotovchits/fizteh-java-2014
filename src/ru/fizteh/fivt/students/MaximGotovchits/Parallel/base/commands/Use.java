package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import java.io.*;
import java.text.ParseException;

public class Use extends Command {
    @Override
    public String getCmdName() {
        return "use";
    }

    @Override
    public boolean execute(String[] cmd) {
        if (CommandTools.amountOfArgumentsIs(2, cmd)) {
            try {
                useFunction(cmd[1], CommandTools.currentTableProvider.getUsingTableName());
            } catch (IOException e) {
                System.err.println(e);
            } catch (ParseException e) {
                System.err.println(e);
            }
            return true;
        }
        return false;
    }

    public boolean useFunction(String tableName, String oldTableName) throws IOException, ParseException {
        return CommandTools.currentTableProvider.changeUsingTable(tableName, oldTableName);
    }
}
