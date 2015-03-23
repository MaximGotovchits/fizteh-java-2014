package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

public class CommandTools {
    static boolean tableIsChosen = false;
    public static final String DATA_BASE_NAME = System.getProperty("fizteh.db.dir");
    //static ObjectTable currentTableObject;
    static ObjectTableProvider currentTableProvider = new ObjectTableProvider(System.getProperty("fizteh.db.dir"));
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
