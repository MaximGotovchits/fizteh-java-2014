package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public class Use extends Command {
    @Override
    public String getCmdName() {
        return "use";
    }

    @Override
    public boolean execute(String[] cmd) throws Exception {
        if (cmd.length == 2) {
            useFunction(cmd[1], CommadTools.usingTableName);
            return true;
        }
        return false;
    }

    public boolean useFunction(String tableName, String oldTableName) throws Exception {
        if (!tableName.equals(oldTableName)) {
            String outputName = tableName;
            String tablePath = CommadTools.DATA_BASE_NAME + File.separator + tableName;
            File file = new File(tablePath);
            if (file.exists()) {
                CommadTools.usingTableName = tableName;
                if (CommadTools.tableIsChosen) {
                    new FillTable().execute(null);
                    CommadTools.currentTable.storage.get().clear();
                    CommadTools.currentTable.commitStorage.clear();
                }
                CommadTools.currentTable = new ObjectTable(CommadTools.DATA_BASE_NAME + File.separator
                        + CommadTools.usingTableName);
                for (Integer i = 0; i < CommadTools.DIR_NUM; ++i) {
                    for (Integer j = 0; j < CommadTools.FILE_NUM; ++j) {
                        tablePath = CommadTools.DATA_BASE_NAME + File.separator + tableName + File.separator
                                + i + CommadTools.DIR_EXT + File.separator + j + CommadTools.FILE_EXT;
                        if (new File(tablePath).exists()) {
                            fillStorage(tablePath, file);
                            PrintWriter writer = new PrintWriter(new File(tablePath));
                            writer.print("");
                            writer.close();
                        }
                    }
                }
                System.out.println("using " + outputName);
                CommadTools.tableIsChosen = true;
            } else {
                System.err.println(tableName + " not exists");
                return false;
            }
        } else {
            System.out.println("using " + oldTableName);
        }
        CommadTools.usingTableName = CommadTools.currentTable.getName();
        return true;
    }

    void fillStorage(String datName, File file) throws IOException, ParseException {
        DataInputStream stream = new DataInputStream(new FileInputStream(datName));
        file = new File(datName);
        byte[] data = new byte[(int) file.length()];
        stream.read(data);
        int counter = 0;
        int offset = 0;
        String keyForMap = "";
        String value = "";
        String aLL = new String(data);
        while (counter < file.length()) {
            offset = data[counter];
            keyForMap = new String(data, counter + 2, offset - 2, StandardCharsets.UTF_8);
            counter = counter + offset + 1;
            offset = data[counter];
            value = new String(data, counter + 2,  data.length - counter - 3, StandardCharsets.UTF_8);
            value = value.replaceAll("^\\s*|\\s*$", "");
            String tableName = new File(new File(datName).getParent()).getParent();
            ObjectStoreable valForMap = (ObjectStoreable)
                    new ObjectTableProvider().deserialize(new ObjectTable(tableName), value);
            CommadTools.currentTable.storage.get().put(keyForMap, valForMap);
            CommadTools.currentTable.commitStorage.put(keyForMap, valForMap);
            counter = counter + offset + 3;
        }
        stream.close();
    }
}
