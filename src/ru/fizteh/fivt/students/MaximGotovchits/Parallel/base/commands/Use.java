package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;
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
                useFunction(cmd[1], CommandTools.usingTableName);
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
        if (!tableName.equals(oldTableName)) {
            String outputName = tableName;
            String tablePath = CommandTools.DATA_BASE_NAME + File.separator + tableName;
            File file = new File(tablePath);
            if (file.exists()) {
                CommandTools.usingTableName = tableName;
                if (CommandTools.tableIsChosen) {
                    CommandTools.currentTableProvider.fillTable();
                    CommandTools.currentTableProvider.getCurrentTableObject().storage.get().clear();
                    CommandTools.currentTableProvider.getCurrentTableObject().commitStorage.clear();
                }
                CommandTools.currentTableProvider.setCurrentTableObject(new ObjectTable(CommandTools.DATA_BASE_NAME
                        + File.separator + CommandTools.usingTableName));
                for (Integer i = 0; i < CommandTools.currentTableProvider.getDirNum(); ++i) {
                    for (Integer j = 0; j < CommandTools.currentTableProvider.getFileNum(); ++j) {
                        tablePath = CommandTools.DATA_BASE_NAME + File.separator + tableName + File.separator
                                + i + CommandTools.currentTableProvider.getDirExt() + File.separator + j
                                + CommandTools.currentTableProvider.getFileExt();
                        if (new File(tablePath).exists()) {
                            CommandTools.currentTableProvider.fillStorage(tablePath, file);
                            PrintWriter writer = new PrintWriter(new File(tablePath));
                            writer.print("");
                            writer.close();
                        }
                    }
                }
                System.out.println("using " + outputName);
                CommandTools.tableIsChosen = true;
            } else {
                System.err.println(tableName + " not exists");
                return false;
            }
        } else {
            System.out.println("using " + oldTableName);
        }
        CommandTools.usingTableName = CommandTools.currentTableProvider.getCurrentTableObject().getName();
        return true;
    }
}
