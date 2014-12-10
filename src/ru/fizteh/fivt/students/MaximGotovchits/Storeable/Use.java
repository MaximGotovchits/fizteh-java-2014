package ru.fizteh.fivt.students.MaximGotovchits.Storeable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public class Use extends CommandsTools {
    public void useFunction(String tableName, String oldTableName) throws Exception {
        usingTable = tableName;
        if (!tableName.equals(oldTableName)) {
            String outputName = tableName;
            String tablePath = dataBaseName + File.separator + tableName;
            File file = new File(tablePath);
            if (file.exists()) {
                if (tableIsChosen) {
                    new FillTable().fillTableFunction(oldTableName);
                    storage.clear();
                    commitStorage.clear();
                }
                for (Integer i = 0; i < dirNum; ++i) {
                    for (Integer j = 0; j < fileNum; ++j) {
                        tablePath = dataBaseName + File.separator + tableName + File.separator
                        + i + dirExt + File.separator + j + fileExt;
                        if (new File(tablePath).exists()) {
                            fillStorage(tablePath, file);
                            PrintWriter writer = new PrintWriter(new File(tablePath));
                            writer.print("");
                            writer.close();
                        }
                    }
                }
                System.out.println("using " + outputName);
                tableIsChosen = true;
            } else {
                System.err.println(tableName + " not exists");
            }
        } else {
            System.out.println("using " + oldTableName);
        }
        currentTableObject = new ObjectTable(dataBaseName + File.separator + usingTable);
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
        while (counter < file.length()) {
            offset = data[counter];
            keyForMap = new String(data, counter + 2, offset - 2, StandardCharsets.UTF_8);
            counter = counter + offset + 1;
            offset = data[counter];
            value = new String(data, counter + 2,  data.length - counter - 3, StandardCharsets.UTF_8);
            String tableName = new File(new File(datName).getParent()).getParent();
            ObjectStoreable valForMap = (ObjectStoreable)
            new ObjectTableProvider().deserialize(new ObjectTable(tableName), value);
            storage.put(keyForMap, valForMap);
            commitStorage.put(keyForMap, valForMap);
            counter = counter + offset + 1;
        }
        stream.close();
    }
}
