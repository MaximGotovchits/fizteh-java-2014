package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CommandTools {
    protected static final String DATA_BASE_NAME = System.getProperty("fizteh.db.dir");
    static boolean tableIsChosen = false;
    static final int DIR_NUM = 16;
    static final int FILE_NUM = 16;
    static final String DIR_EXT = ".dir";
    static final String FILE_EXT = ".dat";
    static String usingTableName;
    public static final Charset UTF = StandardCharsets.UTF_8;
    static ObjectTable currentTable;
    public static void informToChooseTable() {
        System.err.println("table is not chosen");
    }

    public static boolean amountOfArgumentsIs(int argsAmount, String[] cmd) {
        return argsAmount == cmd.length;
    }

    public static boolean amountOfArgumentsIsMoreThan(int argsAmount, String[] cmd) {
        return argsAmount < cmd.length;
    }
}
