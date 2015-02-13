package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;

public class CommadTools {
    static final String INVITATIONAL_SYMBOL = "$ ";
    private static final String SPLIT_BY_COMMA = "\\s*;\\s*";
    protected static final String DATA_BASE_NAME = System.getProperty("fizteh.db.dir");
    static boolean tableIsChosen = false;
    static final int DIR_NUM = 16;
    static final int FILE_NUM = 16;
    static final String DIR_EXT = ".dir";
    static final String FILE_EXT = ".dat";
    static String usingTableName;
    static ObjectTable currentTable;
    public static void informToChooseTable() {
        System.err.println("table is not chosen");
    }
}
