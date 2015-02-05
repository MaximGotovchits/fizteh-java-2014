package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FillTable {
    private static final String FILE_EXT = ".dat";
    private static final Integer FILE_NUM = 16;
    private static final String DIR_EXT = ".dir";
    private static final Integer DIR_NUM = 16;
    private static final String DATA_BASE_NAME = System.getProperty("fizteh.db.dir");
    public void fillTableFunction(ObjectTable usingTable) throws Exception {
        for (Map.Entry<String, ObjectStoreable> entry : usingTable.commitStorage.entrySet()) {
            int hashCode = entry.getKey().hashCode();
            Integer nDirectory = hashCode % DIR_NUM;
            Integer nFile = hashCode / DIR_NUM % FILE_NUM;
            Path fileName = Paths.get(DATA_BASE_NAME + File.separator + usingTable.getName(), nDirectory + DIR_EXT);
            File file = new File(fileName.toString());
            if (!file.exists()) {
                file.mkdir();
            }
            fileName = Paths.get(fileName.toString(), nFile + FILE_EXT);
            file = new File(fileName.toString());
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] bytesKey = (" " + entry.getKey() + " ").getBytes(StandardCharsets.UTF_8);
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(fileName.toString(), true));
            stream.write((int) bytesKey.length);
            stream.write(bytesKey);
            byte[] bytesVal = ((" " + entry.getValue().serialisedValue + " ").getBytes(StandardCharsets.UTF_8));
            stream.write((int) bytesVal.length - 1);
            stream.write(bytesVal);
            stream.close();
        }
    }
}
