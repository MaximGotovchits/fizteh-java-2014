package ru.fizteh.fivt.students.MaximGotovchits.Parallel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public class Use extends CommandsTools {
    public boolean useFunction(String tableName, String oldTableName) throws Exception {
        if (!tableName.equals(oldTableName)) {
            String outputName = tableName;
            String tablePath = dataBaseName + "/" + tableName;
            File file = new File(tablePath);
            if (file.exists()) {
                usingTableName = tableName;
                if (tableIsChosen) {
                    new FillTable().fillTableFunction(currentTableObject);
                    currentTableObject.storage.get().clear();
                    currentTableObject.commitStorage.clear();
                }
                currentTableObject = new ObjectTable(dataBaseName + File.separator + usingTableName);
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
                return false;
            }
        } else {
            System.out.println("using " + oldTableName);
        }
        usingTableName = currentTableObject.getName();
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
            currentTableObject.storage.get().put(keyForMap, valForMap);
            currentTableObject.commitStorage.put(keyForMap, valForMap);
            counter = counter + offset + 3;
        }
        stream.close();
    }
}
