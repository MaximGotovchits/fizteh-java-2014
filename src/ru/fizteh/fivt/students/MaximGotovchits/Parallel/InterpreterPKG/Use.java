package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTableProvider;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public class Use {
    public boolean useFunction(String tableName, String oldTableName) throws Exception {
        if (!tableName.equals(oldTableName)) {
            String outputName = tableName;
            String tablePath = Interpreter.DATA_BASE_NAME + File.separator + tableName;
            File file = new File(tablePath);
            if (file.exists()) {
                Interpreter.usingTableName = tableName;
                if (Interpreter.tableIsChosen) {
                    new FillTable().fillTableFunction(Interpreter.currentTableObject);
                    Interpreter.currentTableObject.storage.get().clear();
                    Interpreter.currentTableObject.commitStorage.clear();
                }
                Interpreter.currentTableObject = new ObjectTable(Interpreter.DATA_BASE_NAME + File.separator
                        + Interpreter.usingTableName);
                for (Integer i = 0; i < Interpreter.dirNum; ++i) {
                    for (Integer j = 0; j < Interpreter.fileNum; ++j) {
                        tablePath = Interpreter.DATA_BASE_NAME + File.separator + tableName + File.separator
                                + i + Interpreter.dirExt + File.separator + j + Interpreter.fileExt;
                        if (new File(tablePath).exists()) {
                            fillStorage(tablePath, file);
                            PrintWriter writer = new PrintWriter(new File(tablePath));
                            writer.print("");
                            writer.close();
                        }
                    }
                }
                System.out.println("using " + outputName);
                Interpreter.tableIsChosen = true;
            } else {
                System.err.println(tableName + " not exists");
                return false;
            }
        } else {
            System.out.println("using " + oldTableName);
        }
        Interpreter.usingTableName = Interpreter.currentTableObject.getName();
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
            Interpreter.currentTableObject.storage.get().put(keyForMap, valForMap);
            Interpreter.currentTableObject.commitStorage.put(keyForMap, valForMap);
            counter = counter + offset + 3;
        }
        stream.close();
    }
}
