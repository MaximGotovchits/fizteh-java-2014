package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FillTable extends Command {
private final Charset coding = StandardCharsets.UTF_8;

    @Override
    public boolean execute(String[] cmd) throws Exception {
        for (Map.Entry<String, ObjectStoreable> entry : CommandTools.currentTable.commitStorage.entrySet()) {
            int hashCode = entry.getKey().hashCode();
            Integer nDirectory = hashCode % CommandTools.DIR_NUM;
            Integer nFile = hashCode / CommandTools.DIR_NUM % CommandTools.FILE_NUM;
            Path fileName = Paths.get(CommandTools.DATA_BASE_NAME, CommandTools.currentTable.getName(), nDirectory
                    + CommandTools.DIR_EXT);
            File file = new File(fileName.toString());
            if (!file.exists()) {
                file.mkdir();
            }
            fileName = Paths.get(fileName.toString(), nFile + CommandTools.FILE_EXT);
            file = new File(fileName.toString());
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] bytesKey = (" " + entry.getKey() + " ").getBytes(coding);
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(fileName.toString(), true));
            stream.write((int) bytesKey.length);
            stream.write(bytesKey);
            byte[] bytesVal = ((" " + entry.getValue().serialisedValue + " ").getBytes(coding));
            stream.write((int) bytesVal.length - 1);
            stream.write(bytesVal);
            stream.close();
        }
        return true;
    }

    @Override
    public String getCmdName() {
        return "fill table";
    }

    private void streamWrite() {

    }
}