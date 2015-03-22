package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class CommandTools {
    public static final String DATA_BASE_NAME = System.getProperty("fizteh.db.dir");
    static boolean tableIsChosen = false;
    static String usingTableName;
    public static final Charset UTF = StandardCharsets.UTF_8;
    static ObjectTableProvider currentTableProvider = new ObjectTableProvider(DATA_BASE_NAME);
    static void informToChooseTable() {
        System.err.println("table is not chosen");
    }

    static boolean amountOfArgumentsIs(int argsAmount, String[] cmd) {
        return argsAmount == cmd.length;
    }

    static boolean amountOfArgumentsIsMoreThan(int argsAmount, String[] cmd) {
        return argsAmount < cmd.length;
    }
}
